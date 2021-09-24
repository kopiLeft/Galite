/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.galite.type.Date
import java.sql.Blob

import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

class Query(conn: Connection) {

  fun addString(value: String?) {
    TODO()
  }

  fun getBlob(pos: Int): Blob {
    TODO()
  }

  fun next(): Boolean {
    TODO()
  }

  fun getWeek(pos: Int): Week = TODO()

  fun getString(pos: Int): String = TODO()

  fun addInt(value: Int) {
    TODO()
  }

  fun run(format: String?): Int = TODO()

  fun delete(format: String?): Int = TODO()

  fun getInt(pos: Int): Int = TODO()

  fun close() {
    TODO()
  }

  fun open(format: String) {
    TODO()
  }

  fun isNull(column: Int): Boolean = TODO()

  fun getDate(pos: Int): Date = TODO()

  fun getBoolean(column: Int): Boolean = TODO()

  fun getObject(pos: Int): Any = TODO()

  fun getTimestamp(pos: Int): Timestamp = TODO()

  fun getDecimal(pos: Int): Decimal = TODO()

  fun getTime(pos: Int): Time = TODO()

  fun getMonth(pos: Int): Month = TODO()
}
