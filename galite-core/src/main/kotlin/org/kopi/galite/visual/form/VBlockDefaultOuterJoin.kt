/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.kopi.galite.visual.form

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class VBlockDefaultOuterJoin(block: VBlock) {

  /**
   * constructs an outer join tree.
   */
  private fun getJoinCondition(rootTable: Table, table: Table): Join {
    lateinit var joinTables: Join

    if (table == rootTable) {
      joinTables = Join(table)
      addToJoinedTables(rootTable)
    } else {
      throw Exception("Use root table instead of ${table.tableName}")
    }

    return getJoinCondition(rootTable, table, joinTables)
  }

  /**
   * constructs an outer join tree.
   */
  private fun getJoinCondition(rootTable: Table, table: Table, initialJoinTables: Join): Join {
    var field: VField
    var additionalConstraint: (SqlExpressionBuilder.() -> Op<Boolean>)? = null
    var condition: Op<Boolean>? = null
    var joinTables = initialJoinTables

    for (i in fields.indices) {
      if (isProcessedField(i)) {
        continue
      }
      field = fields[i]

      if (field.getColumnCount() > 1) {
        val tableColumn = field.fetchColumn(table)    // TODO should return Column<*>?
        val rootColumn = field.fetchColumn(rootTable) // TODO should return Column<*>?

        if (tableColumn != -1 && rootColumn != -1) {    // TODO should we check if tableColumn != null?
          for (j in 0 until field.getColumnCount()) {
            val joinType = if (field.getColumn(tableColumn)!!.nullable ||
                    field.getColumn(rootColumn)!!.nullable) {
              JoinType.LEFT
            } else {
              JoinType.INNER
            }

            if (j != tableColumn) {
              if(!isJoinedTable(field.getColumn(tableColumn)!!.column.table)) {

                if (rootTable == table) {
                  val joinTable = field.getColumn(tableColumn)!!.getTable()

                  // start of an outer join
                  addToJoinedTables(field.getColumn(tableColumn)!!.getTable())
                  joinTables = joinTables.join(joinTable, joinType, field.getColumn(tableColumn)!!.column,
                                               field.getColumn(j)!!.column,
                                               additionalConstraint)
                }
                if (j == field.getColumnCount() || field.getColumnCount() == 2) {
                  // a field is only processed if all columns processed
                  // for nullable or has only 2 columns and one has been
                  // already processed
                  // must be marked before going to next level
                  addToProcessedFields(i)
                }
                if (rootTable == table) {
                  getJoinCondition(rootTable, field.getColumn(tableColumn)!!.getTable(), joinTables)
                }
              } else if (isJoinedTable(field.getColumn(j)!!.column.table)) {
                // the table for this column is present in the outer join tree
                // as caster outer joins do not work, we assume that the
                // condition will apply to the root
                if (j == rootColumn) {
                  condition = if (condition != null) {
                    condition and (field.getColumn(tableColumn)!!.column eq field.getColumn(j)!!.column)
                  } else {
                    field.getColumn(tableColumn)!!.column eq field.getColumn(j)!!.column
                  }
                  additionalConstraint = { condition }
                }

                if (j == field.getColumnCount() || field.getColumnCount() == 2) {
                  // a field is only processed if all columns processed
                  // for nullable or has only 2 columns and one has been
                  // already processed
                  addToProcessedFields(i)
                }
              } else {
                if (rootTable == table) {
                  val joinTable = field.getColumn(j)!!.getTable()

                  // start of an outer join
                  addToJoinedTables(field.getColumn(j)!!.getTable())
                  joinTables = joinTables.join(joinTable, joinType, field.getColumn(tableColumn)!!.column,
                                               field.getColumn(j)!!.column,
                                               additionalConstraint)
                }
                if (j == field.getColumnCount() || field.getColumnCount() == 2) {
                  // a field is only processed if all columns processed
                  // for nullable or has only 2 columns and one has been
                  // already processed
                  // must be marked before going to next level
                  addToProcessedFields(i)
                }
                if (rootTable == table) {
                  getJoinCondition(rootTable, field.getColumn(j)!!.getTable(), joinTables)
                }
              }
            }
          }
        }
      }
    }

    return joinTables
  }

  companion object {

    /**
     * search from-clause condition
     */
    fun getSearchTables(block: VBlock?): Join? {
      return VBlockDefaultOuterJoin(block!!).getSearchTablesCondition()
    }

    fun getFetchRecordCondition(fields: List<VField>): Op<Boolean>? {
      val fetchRecordCondition = mutableListOf<Op<Boolean>>()

      for (field in fields) {

        if (field.hasNullableCols()) {
          for (j in 1 until field.getColumnCount()) {

            if (!field.getColumn(j)!!.nullable) {
              fetchRecordCondition.add(Op.build {
                (field.getColumn(j)!!.column eq field.getColumn(0)!!.column)
              }
              )
            }
          }
        } else {
          for (j in 1 until field.getColumnCount()) {
            fetchRecordCondition.add(Op.build {
              (field.getColumn(j)!!.column eq field.getColumn(j - 1)!!.column)
            })
          }
        }
      }
      return if (fetchRecordCondition.isEmpty()) null else fetchRecordCondition.compoundAnd()
    }
  }

  private fun getSearchTablesCondition(): Join? {
    if (tables == null) {
      return null
    }

    // first search join condition for the block main table.
    var searchTablesCondition = getJoinCondition(tables!![0], tables!![0])

    // search join condition for other lookup tables  not joined with main table.
    for (i in 1 until tables!!.size) {
      if (!isJoinedTable(tables!![i])) {
        // all not joined tables need to be ran through
        searchTablesCondition = getJoinCondition(tables!![i], tables!![i], searchTablesCondition)
      }
    }
    return searchTablesCondition
  }

  fun getSearchCondition(field: VField): Op<Boolean> {
    val searchCondition = mutableListOf<Op<Boolean>>()

    if (field.hasNullableCols()) {
      for (j in 1 until field.getColumnCount()) {
        if (!field.getColumn(j)!!.nullable) {

          searchCondition.add(Op.build {
            (field.getColumn(j)!!.column eq field.getColumn(0)!!.column)
          })

        }
      }
    } else {
      for (j in 1 until field.getColumnCount()) {

        searchCondition.add(Op.build {
          (field.getColumn(j)!!.column eq field.getColumn(j - 1)!!.column)
        })
      }
    }
    return searchCondition.compoundAnd()
  }

  private fun addToJoinedTables(table: Table) {
    joinedTables!!.add(table)
  }

  private fun isJoinedTable(table: Table): Boolean = joinedTables!!.contains(table)

  private fun addToProcessedFields(field: Int) {
    processedFields!!.add(field.toString())
  }

  private fun isProcessedField(field: Int): Boolean = processedFields!!.contains(field.toString())

  private var fields: List<VField> = block.fields
  private var joinedTables: ArrayList<Table>? = ArrayList()
  private var processedFields: ArrayList<String>? = ArrayList()
  private var tables: List<Table>? = block.tables
}
