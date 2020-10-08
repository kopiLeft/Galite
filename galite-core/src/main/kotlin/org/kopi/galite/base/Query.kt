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

import java.sql.SQLException

import org.kopi.galite.db.Connection
import org.kopi.galite.type.NotNullDate

class Query(defaultConnection: Connection) {

  fun getBoolean(pos: Int): Boolean = TODO()

  @Throws(SQLException::class)
  fun getDate(pos: Int): NotNullDate? {
    TODO()
  }

  fun addString(value: String) {
   TODO()
  }

  fun open(format: String)  {
    TODO()
  }

  fun next() : Boolean {
    TODO()
  }

  fun close() {
    TODO()
  }

  fun getString(pos: Int): String? {
    TODO()
  }

  fun getInt(pos: Int): Int {
    TODO()
  }

  fun isNull(pos: Int): Boolean {
    TODO()
  }

  fun getObject(pos: Int): Any {
    TODO()
  }

  fun addInt(value: Int) {
    TODO()
  }
}
