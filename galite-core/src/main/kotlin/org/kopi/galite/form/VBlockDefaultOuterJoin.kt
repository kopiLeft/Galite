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

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.transactions.transaction

class VBlockDefaultOuterJoin(block: VBlock) {

  /**
   * constructs an outer join tree.
   */
  private fun getJoinCondition(rootTable: Int, table: Int): Op<Boolean>? {
    var joinBuffer: Op<Boolean>? = null
    var field: VField
    if (table == rootTable) {
      addToJoinedTables(tables?.get(rootTable)!!)
    }

    for (i in fields.indices) {
      if (isProcessedField(i)) {
        continue
      }
      field = fields[i]

      if (field.getColumnCount() > 1) {
        val tableColumn = field.fetchColumn(table)
        val rootColumn = field.fetchColumn(rootTable)

        if (tableColumn != -1) {
          if (field.getColumn(tableColumn)!!.nullable ||
                  field.getColumn(rootColumn)!!.nullable) {
            for (j in 0 until field.getColumnCount()) {
              if (j != tableColumn) {
                if (isJoinedTable(field.getColumn(j)!!.column!!.table)) {
                  // the table for this column is present in the outer join tree
                  // as caster outer joins do not work, we assume that the
                  // condition will apply to the root
                  if (j == rootColumn) {
                    transaction {
                      joinBuffer = Op.build {
                        field.getColumn(tableColumn)!!.column!! eq field.getColumn(j)!!.column!!
                      }
                    }
                  }

                  if (j == field.getColumnCount() || field.getColumnCount() == 2) {
                    // a field is only processed if all columns processed
                    // for nullable or has only 2 columns and one has been
                    // already processed
                    addToProcessedFields(i)
                  }
                } else {
                  if (rootTable == table) {
                    val mainTable = object : Table(tables!![table].tableName) {
                    }
                    val joinTable = object : Table(tables!![field.getColumn(j)!!.getTable()].tableName) {
                    }
                    // start of an outer join
                    addToJoinedTables(field.getColumn(j)!!.column!!.table)

                    transaction {
                      mainTable.join(joinTable, JoinType.LEFT, field.getColumn(tableColumn)!!.column!!, field.getColumn(j)!!.column!!)
                    }
                  }
                  if (j == field.getColumnCount() || field.getColumnCount() == 2) {
                    // a field is only processed if all columns processed
                    // for nullable or has only 2 columns and one has been
                    // already processed
                    // must be marked before going to next level
                    addToProcessedFields(i)
                  }
                  if (rootTable == table) {
                    getJoinCondition(rootTable, field.getColumn(j)!!.getTable())
                  }
                }
              }
            }
          }
        }
      }
    }
    return joinBuffer
  }

  companion object {

    /**
     * search from-clause condition
     */
    fun getSearchTables(block: VBlock?): Query? {
      return VBlockDefaultOuterJoin(block!!).getSearchTablesCondition()
    }
  }

  private fun getSearchTablesCondition(): Query? {

    if (tables == null) {
      return null
    }
    var operation: Query? = null

    // first search join condition for the block main table.
    transaction {
      operation = joinTables(tables!!).select {
        getJoinCondition(0, 0)!!
      }
    }
    // search join condition for other lookup tables  not joined with main table.
    for (i in 1 until tables!!.size) {
      if (!isJoinedTable(tables!![i])) {
        transaction {
          operation = joinTables(tables!!).select {
            // all not joined tables need to be ran through
            getJoinCondition(i, i)!!
          }
        }
      }
    }
    return operation
  }

  fun joinTables(tables: Array<Table>): Join {
    var op: Join? = null
    for (i in 1 until tables.size) {
      transaction {
        op = (tables[0].join(tables[i], JoinType.FULL, getJoinCondition(0, i)))
                .join(tables[i], JoinType.FULL, getJoinCondition(i, i))
      }
    }
    return op!!
  }

  fun getSearchCondition(fld: VField, op: Op<Boolean>?): Op<Boolean>? {
    var operation: Op<Boolean>? = null
    if (fld.hasNullableCols()) {
      for (j in 1 until fld.getColumnCount()) {
        if (!fld.getColumn(j)!!.nullable) {

          transaction {
            operation = Op.build {
              (fld.getColumn(j)!!.column!! eq fld.getColumn(0)!!.column!!)

            }
          }
        }
      }
    } else {
      for (j in 1 until fld.getColumnCount()) {

        transaction {
          operation = Op.build {
            (fld.getColumn(j)!!.column!! eq fld.getColumn(j - 1)!!.column!!)
          }
        }
      }
    }
    return operation
  }

  fun getFetchRecordCondition(fields: Array<VField>): Op<Boolean>? {
    var operation: Op<Boolean>? = null
    for (i in fields.indices) {
      val fld = fields[i]

      if (fld.hasNullableCols()) {
        for (j in 1 until fld.getColumnCount()) {

          if (!fld.getColumn(j)!!.nullable) {

            transaction {
              operation = Op.build {
                (fld.getColumn(j)!!.column!! eq fld.getColumn(0)!!.column!!)
              }
            }
          }
        }
      } else {
        for (j in 1 until fld.getColumnCount()) {

          if (!fld.getColumn(j)!!.nullable) {
            operation = Op.build {
                      (fld.getColumn(j)!!.column!! eq fld.getColumn(j - 1)!!.column!!)
            }
          }
        }
      }
    }
    return operation
  }

  private fun addToJoinedTables(table: Table) {
    joinedTables!!.add(table)
  }

  private fun isJoinedTable(table: Table): Boolean {
    return joinedTables!!.contains(table)
  }

  private fun addToProcessedFields(field: Int) {
    processedFields!!.add(field.toString())
  }

  private fun isProcessedField(field: Int): Boolean {
    return processedFields!!.contains(field.toString())
  }

  private var block: VBlock? = block
  private var fields: Array<VField> = block.fields
  private var joinedTables: ArrayList<Table>? = ArrayList()
  private var processedFields: ArrayList<String>? = ArrayList()
  private var tables: Array<Table>? = block.tables
}
