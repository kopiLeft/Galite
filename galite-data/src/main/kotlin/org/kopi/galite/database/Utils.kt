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

package org.kopi.galite.database

import java.sql.SQLException
import java.util.UUID

import org.jetbrains.exposed.sql.NextVal
import org.jetbrains.exposed.sql.Sequence
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.nextIntVal
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager

class Utils {
  companion object {

    /**
     * Increments table sequence and returns the first free ID of table.
     *
     * The sequence name of a table can be one of the following :
     *    - @param sequence : the sequence name added manually by the developer,
     *    - "<Table_Name>Id : default sequence name for Galite tables,
     *    - <Table_Name>_<Id_Name>_seq : default sequence name assigned by postgres.
     */
    fun getNextTableId(table: Table, id: String, sequence: Sequence? = null): Int {
      var seqNextVal: NextVal<Int>

      return try {
        TransactionManager.current().runWithSavepoint(UUID.randomUUID()) {
          seqNextVal = (sequence ?: Sequence("${table.nameInDatabaseCase()}Id")).nextIntVal()

          Table.Dual.slice(seqNextVal).selectAll().single()[seqNextVal]
        }
      } catch (e: SQLException) {
        try {
          TransactionManager.current().runWithSavepoint(UUID.randomUUID()) {
            seqNextVal = Sequence("${table.nameInDatabaseCase()}_${id}_seq").nextIntVal()

            Table.Dual.slice(seqNextVal).selectAll().single()[seqNextVal]
          }
        } catch (e: SQLException) {
          throw RuntimeException("Unable to get the sequence next value for table ${table.nameInDatabaseCase()} : ${e.message}")
        }
      }
    }

    /**
     * Run a transaction block with a savepoint : allow rollback to the added savepoint
     */
    fun <T> Transaction.runWithSavepoint(id: UUID, block: () -> T): T {
      val savepoint = connection.setSavepoint("SAVE_POINT_$id")

      return try {
        block()
      } catch (e: SQLException) {
        connection.rollback(savepoint)
        throw e
      } finally {
        connection.releaseSavepoint(savepoint)
      }
    }

    fun trimString(input: String): String {
      val buffer = CharArray(input.length)
      var bufpos = 0
      var state = 0
      for (element in input) {
        if (Character.isWhitespace(element)) {
          state = if (state == 0) 0 else 2
        } else {
          if (state == 2) {
            buffer[bufpos++] = ' '
          }
          buffer[bufpos++] = element
          state = 1
        }
      }
      return if (bufpos == 0) "" else String(buffer, 0, bufpos)
    }

    fun trailString(input: String): String? {
      var last = -1
      var i = input.length - 1
      while (last == -1 && i >= 0) {
        if (!Character.isWhitespace(input[i])) {
          last = i
        }
        --i
      }
      return if (last == -1) {
        ""
      } else if (last == input.length) {
        input
      } else {
        input.substring(0, last + 1)
      }
    }

    fun toSql(l: String?): String {
      return if (l == null) {
        NULL_LITERAL
      } else {
        val b = StringBuffer()
        b.append('\'')
        for (element in l) {
          if (element == '\'') {
            b.append('\'')
          }
          b.append(element)
        }
        b.append('\'')
        b.toString()
      }
    }

    const val NULL_LITERAL = "NULL"
  }
}
