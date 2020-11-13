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
import java.util.*

class VBlockDefaultOuterJoin {

  /**
   * Constructor
   */
  fun VBlockDefaultOuterJoin(block: VBlock) {
    this.block = block
    this.fields = block.fields
    this.tables = block.tables
    this.joinedTables = ArrayList<String>()
    this.processedFields = ArrayList<String>()
  }

  fun getFetchRecordCondition(fields: Array<VField>): Op<Boolean>? {
    var operation: Op<Boolean>? = null
    for (i in fields.indices) {
      val fld = fields[i]

      if (fld.hasNullableCols()) {
        for (j in 1 until fld.getColumnCount()) {
          val auxTable = object : Table() {

            val column1 = varchar(fld.getColumn(j)!!.getQualifiedName(), 30, null)
            val column2 = varchar(fld.getColumn(0)!!.getQualifiedName(), 30)
          }

          if (!fld.getColumn(j)!!.isNullable()) {
            operation = Op.build {
              (auxTable.column1 eq auxTable.column2)
            }
          }
        }
      } else {
        for (j in 1 until fld.getColumnCount()) {
          val auxTable2 = object : Table() {
            val column3 = varchar(fld.getColumn(j)!!.getQualifiedName(), 30)
            val column4 = varchar(fld.getColumn(j - 1)!!.getQualifiedName(), 30)
          }
          if (!fld.getColumn(j)!!.isNullable()) {
            operation =
              Op.build {
                 (auxTable2.column3 eq auxTable2.column4)
              }
            }
          }
        }
    }
    return operation
  }

  private var block: VBlock? = null
  private lateinit var fields: Array<VField>
  private var joinedTables: ArrayList<String>? = null
  private var processedFields: ArrayList<String>? = null
  private var tables: Array<String>? = null
}