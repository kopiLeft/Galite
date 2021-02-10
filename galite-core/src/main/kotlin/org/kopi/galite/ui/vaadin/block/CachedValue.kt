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
package org.kopi.galite.ui.vaadin.block

/**
 * A cached value structure to be passed to the client side using the shared state mechanism.
 *
 * @param col The column index.
 * @param rec The record number.
 * @param value The cached value.
 */
class CachedValue(var col: Int, var rec: Int, value: String?) {

  /**
   * The cached value.
   */
  var value: String = value ?: ""

  /**
   *
   * @param other : cached value
   * @return true if there is an existing cached value having the same key
   */
  fun hasSameKey(other: CachedValue): Boolean {
    return col == other.col && rec == other.rec
  }

  override fun hashCode(): Int {
    return col + rec + value.hashCode()
  }

  override fun equals(obj: Any?): Boolean {
    return if (obj is CachedValue) {
      col == obj.col && rec == obj.rec && value == obj.value
    } else {
      super.equals(obj)
    }
  }
}
