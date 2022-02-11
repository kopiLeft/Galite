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

package org.kopi.galite.visual.pivottable

import javax.swing.tree.DefaultMutableTreeNode

class Row(val data: Array<Any?>) : DefaultMutableTreeNode() {
  var visible = false

  /**
   * Return the object at column
   *
   * @param        column                the index of the column
   * @return        the object to be displayed
   */
  fun getValueAt(column: Int): Any? = data[column]

  /**
   * Sets data at column of certain index
   *
   * @param        column                the index of the column
   * @param        value                the value for the column
   */
  fun setValueAt(column: Int, value: Any?) {
    data[column] = value
  }

  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (this === other) return true
    if (other is Row) {
      if (data.contentEquals(other.data)) return true
    }
    return false
  }

  /**
   * Clone Array's objects
   */
  fun cloneArray() = data.clone()
}
