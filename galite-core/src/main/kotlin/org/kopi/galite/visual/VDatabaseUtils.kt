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

package org.kopi.galite.visual

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.kopi.galite.db.DBContextHandler
import org.kopi.galite.db.DBSchema
import org.kopi.galite.util.base.InconsistencyException

class VDatabaseUtils {
  val references = DBSchema.references
  var auxTable = VDatabaseUtils.AuxTable

  object AuxTable : Table() {
    var id = integer("id")
    val column = varchar("column", 255)
  }

  fun checkForeignKeys(context: DBContextHandler, id: Int, table: String) {
    Database.connect(context.getDBContext().defaultConnection.url)

    transaction {
      SchemaUtils.create(references)

      val query1 = references.slice(references.table, references.column, references.action)
              .select { references.reference eq table }
              .orderBy(references.action to SortOrder.DESC)
      val action = query1.forEach { query1Row ->
        when (query1Row[references.action] as Char) {
          'R' -> transaction {
            auxTable = Table(query1Row[references.table]) as AuxTable
            auxTable// How to add the column after object declaration?
            val query2 = auxTable.slice(auxTable.id)
                    .select { auxTable.id eq id }
            if (query2.toList()[1] != null) {
              throw VExecFailedException(MessageCode.getMessage("VIS-00021", arrayOf<Any>(
                      query1Row[references.table],
                      query1Row[references.column]
              )))
            }
          }

          'C' -> transaction {
            auxTable = Table(query1Row[references.table]) as AuxTable
            var query2 = auxTable.slice(auxTable.id)
                    .select { auxTable.id eq id }
            query2.forEach {
              checkForeignKeys(context, it[auxTable.id], query1Row[references.table])
            }

            auxTable.deleteWhere { auxTable.id eq id }
          }

          'N' -> transaction {
            auxTable = Table(query1Row[references.table]) as AuxTable
            auxTable.update({ auxTable.id eq id }) {
              it[auxTable.id] = 0
            }
          }
          else -> throw InconsistencyException()

        }
      }
    }
  }

  fun deleteRecords(context: DBContextHandler, table: String, condition: String?) {
    Database.connect(context.getDBContext().defaultConnection.url)

    transaction {
      SchemaUtils.create(references)
      auxTable = Table(table) as AuxTable
      val query1: org.jetbrains.exposed.sql.Query
      if (condition != null && condition.isNotEmpty()) {
        query1 = auxTable.slice(auxTable.id).selectAll().forUpdate()
      } else {
        query1 = auxTable.slice(auxTable.id).selectAll()
      }

      query1.forEach {
        checkForeignKeys(context, it[auxTable.id], table)
        Table(table).deleteAll()
      }
    }
  }
}
