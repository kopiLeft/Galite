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

package org.kopi.galite.base

import org.kopi.galite.db.Connection
import org.kopi.galite.type.NotNullDate

class Query(conn: Connection) {

  fun addString(value: String?) {
    TODO()
  }

   fun next(): Boolean {
    TODO()
  }

  fun getString(pos: Int): String {
    TODO()
  }

  fun addInt(value: Int) {
    TODO()
  }

  fun run(format: String?): Int = TODO()

  fun delete(format: String?): Int= TODO()

  fun getInt(pos: Int): Int = TODO()

  fun close() {
    TODO()
  }

  fun open(format: String) {
    TODO()
  }

  fun isNull(column: Int): Boolean = TODO()

  fun getDate(pos: Int): NotNullDate = TODO()

  fun getBoolean(column: Int): Boolean = TODO()
}
