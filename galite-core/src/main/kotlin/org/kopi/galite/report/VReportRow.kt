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

package org.kopi.galite.report

abstract class VReportRow(val data: Array<Any?>) {
  var visible = false

  /**
   * Return the level of the node in the grouping tree, starting with 0 at
   * the base rows (leafs).
   *
   * @return        the level of the node in the grouping tree
   */
  abstract fun getLevel(): Int

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
  fun setValueAt(column: Int, value: Any) {
    data[column] = value
  }

  /**
   * Clone Array's objects
   */
  fun cloneArray() = data.clone()

  /**
   * Compare two VReportRows within specified column
   *
   * @param other Report row to compare to
   * @param position position in data
   * @param column
   * @return
   */
  fun compareTo(other: VReportRow, position: Int, column: VReportColumn): Int {
    val data = data[position]
    val rowData = other.data[position]

    // check for nulls: define null less than everything
    return when {
      data == null && rowData == null -> 0
      data == null -> -1
      rowData == null -> 1
      else -> column.compareTo(data, rowData)
    }
  }
}
