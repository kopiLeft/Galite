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

package org.kopi.galite.list

import org.kopi.galite.util.base.InconsistencyException

class VIntegerCodeColumn : VCodeColumn {

  /**
   * Constructs a list column.
   */
  constructor(title: String, column: String, names: Array<String>, codes: Array<Int>,
              sortAscending: Boolean) : super(title, column, names, sortAscending) {
    this.codes = codes
  }

  /**
   * Constructs a list column.
   *
   */
  constructor(title: String, column: String, names: Array<String>, codes: IntArray,
              sortAscending: Boolean) : this(title, column, names, makeObjectArray(codes), sortAscending)

  /**
   * Returns the index.of given object
   */
  override fun getObjectIndex(value: Any): Int {
    for (i in codes.indices) {
      if (value == codes[i]) {
        return i
      }
    }
    throw InconsistencyException("bad code value " + value as Int)
  }

  override fun getDataType(): Class<*> {
    return Int::class.java
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------
  companion object {
    private fun makeObjectArray(input: IntArray): Array<Int> = input.toTypedArray()
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private val codes: Array<Int> // code array
}
