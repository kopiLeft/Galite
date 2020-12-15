/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.form

import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.compoundAnd

class VBlockDefaultOuterJoin(block: VBlock) {

  /**
   * constructs an outer join tree.
   */
  private fun getJoinCondition(rootTable: Int, table: Int): Join {
    var joinTables: Join? = null
    var field: VField

    if (table == rootTable) {
      joinTables = Join(tables!![table])
      addToJoinedTables(tables?.get(rootTable)!!)
    }

    return getJoinCondition(rootTable, table, joinTables!!)
  }

  /**
   * constructs an outer join tree.
   */
  private fun getJoinCondition(rootTable: Int, table: Int, joinTables: Join): Join {
    var field: VField
    var additionalConstraint: (SqlExpressionBuilder.() -> Op<Boolean>)? = null
    var condition: Op<Boolean>? = null

    for (i in fields.indices) {
      if (isProcessedField(i)) {
        continue
      }
      field = fields[i]

      if (field.getColumnCount() > 1) {
        val tableColumn = field.fetchColumn(table)    // TODO should return Column<*>?
        val rootColumn = field.fetchColumn(rootTable) // TODO should return Column<*>?

        if (tableColumn != -1) {    // TODO should check if tableColumn != null
          if (field.getColumn(tableColumn)!!.nullable ||
                  field.getColumn(rootColumn)!!.nullable) {
            for (j in 0 until field.getColumnCount()) {
              if (j != tableColumn) {
                if (isJoinedTable(field.getColumn(j)!!.column.table)) {
                  // the table for this column is present in the outer join tree
                  // as caster outer joins do not work, we assume that the
                  // condition will apply to the root
                  if (j == rootColumn) {
                    condition = if (condition != null) { // TODO Improve this!
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
                    val joinTable = tables!![field.getColumn(j)!!.getTable()]

                    // start of an outer join
                    addToJoinedTables(field.getColumn(j)!!.getTable_())
                    joinTables.join(joinTable, JoinType.LEFT, field.getColumn(tableColumn)!!.column,
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

    fun getFetchRecordCondition(fields: Array<VField>): Op<Boolean>? {
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

            if (!field.getColumn(j)!!.nullable) {
              fetchRecordCondition.add(Op.build {
                (field.getColumn(j)!!.column eq field.getColumn(j - 1)!!.column)
              })
            }
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
    val searchTablesCondition = getJoinCondition(0, 0)

    // search join condition for other lookup tables  not joined with main table.
    for (i in 1 until tables!!.size) {
      if (!isJoinedTable(tables!![i])) {
        // all not joined tables need to be ran through
        searchTablesCondition.join(getJoinCondition(i, i), JoinType.INNER) {
          fields.map { getSearchCondition(it) }.compoundAnd()
        }
      }
    }
    // add remaining tables (not joined tables) to the list of tables.
    for (i in 1 until tables!!.size) {
      if (!isJoinedTable(tables!![i])) {
        searchTablesCondition.join(tables!![i], JoinType.INNER) {
          fields.map { getSearchCondition(it) }.compoundAnd()
        }
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

  private var block: VBlock? = block
  private var fields: Array<VField> = block.fields
  private var joinedTables: ArrayList<Table>? = ArrayList()
  private var processedFields: ArrayList<String>? = ArrayList()
  private var tables: Array<Table>? = block.tables
}
