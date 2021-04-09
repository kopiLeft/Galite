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

package org.kopi.galite.db

import java.lang.RuntimeException

import org.jetbrains.exposed.sql.Sequence
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.nextIntVal
import org.jetbrains.exposed.sql.selectAll
import org.kopi.galite.type.Date
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

class Utils {
  companion object {

    /**
     * Returns first free ID of table.
     */
    fun getNextTableId(table: Table): Int {
      val seqNextVal = Sequence(table.nameInDatabaseCase() + "Id").nextIntVal()
      val seqNextValQuery = getDualTableName().slice(seqNextVal).selectAll()
      val id = seqNextValQuery.firstOrNull()?.get(seqNextVal)
        ?: throw RuntimeException("Unable to get the sequence next value for table ${table.nameInDatabaseCase()}")
      return id
    }

    fun getDualTableName(): Table = Table("DUAL")

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

    fun trailString(input: String): String = TODO()
    fun toSql(date: Date): String = TODO()

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

    fun toSql(t: Time?): String = TODO()
    fun toSql(d: Int?): String = d?.toString() ?: NULL_LITERAL
    fun toSql(t: Timestamp?): String? = TODO()
    fun toSql(t: Week?): String = TODO()
    fun toSql(m: Month?): String = TODO()

    const val NULL_LITERAL = "NULL"
  }
}
