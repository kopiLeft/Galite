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

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.db.DBContextHandler
import org.kopi.galite.db.Query

object VDatabaseUtils {

  fun checkForeignKeys(ctxt: DBContextHandler, id: Int, table: String) {
    var query2: Query
    val query1 = Query(ctxt.getDBContext().defaultConnection)

    query1.addString(table)
    query1.open("SELECT         tabelle, spalte, aktion " +
                "FROM		REFERENZEN " +
                "WHERE		referenz = #1 " +
                "ORDER BY	3 DESC")
    while (query1.next()) {
      when (query1.getString(3)[0]) {
        'R' -> {
          query2 = Query(ctxt.getDBContext().defaultConnection)
          query2.addString(query1.getString(1))
          query2.addString(query1.getString(2))
          query2.addInt(id)
          query2.open("SELECT ID FROM $1 WHERE $2 = #3")
          if (query2.next()) {
            query2.close()
            ctxt.getDBContext().abortWork()
            throw VExecFailedException(MessageCode.getMessage("VIS-00021", arrayOf(
                    query1.getString(2),
                    query1.getString(1)
            )))
          }
          query2.close()
        }
        'C' -> {
          query2 = Query(ctxt.getDBContext().defaultConnection)
          query2.addString(query1.getString(1))
          query2.addString(query1.getString(2))
          query2.addInt(id)
          query2.open("SELECT ID FROM $1 WHERE $2 = #3")
          while (query2.next()) {
            checkForeignKeys(ctxt, query2.getInt(1), query1.getString(1))
          }
          query2.close()

          // now delete the tuple
          query2.addString(query1.getString(1))
          query2.addString(query1.getString(2))
          query2.addInt(id)
          query2.run("DELETE FROM $1 WHERE $2 = #3")
        }
        'N' -> {
          query2 = Query(ctxt.getDBContext().defaultConnection)
          query2.addString(query1.getString(1))
          query2.addString(query1.getString(2))
          query2.addInt(id)
          query2.run("UPDATE $1 SET $2 = 0 WHERE $2 = #3")
        }
        else -> throw InconsistencyException()
      }
    }
    query1.close()
  }

  /**
   * Deletes current record of given block from database.
   * Is like DeleteChecked in Aegis
   */
  fun deleteRecords(ctxt: DBContextHandler,
                    table: String,
                    condition: String?) {
    val query: Query = Query(ctxt.getDBContext().defaultConnection)
    query.addString(table)
    if (condition != null && condition.isNotEmpty()) {
      query.addString(condition)
      query.open("SELECT ID FROM $1 WHERE $2 FOR UPDATE")
    } else {
      query.open("SELECT ID FROM $1 FOR UPDATE")
    }
    while (query.next()) {
      val id: Int = query.getInt(1)
      checkForeignKeys(ctxt, id, table)
      query.addString(table)
      query.delete("DELETE FROM $1")
    }
    query.close()
  }
}
