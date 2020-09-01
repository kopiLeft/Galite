/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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

abstract class VReportRow(reportData: Array<Any>) {

  protected var data: Array<Any> = reportData
  private var visible: Boolean = false

  private val serialVersionUID = 857795567573130287L

  /**
   * Return the object at column
   *
   * @param        column                the index of the column
   * @return        the object to be displayed
   */
  fun getValueAt(column: Int): Any = data[column]

  /**
   * Sets data at column of certain index
   *
   * @param        column                the index of the column
   * @param        value                the value for the column
   */
  fun setValueAt(column: Int, value: Any) {
    data[column] = value
  }

  /**Return True if row is visible*/
  fun isVisible() = visible

  /**
   * Clone Array's objects
   */
  fun cloneArray() = data.clone() as Array<Any?>

  /**
   *
   */
  fun compareTo(other: VReportRow, position: Int, column: VReportColumn): Int {
    val data: Any = data[position]
    val rowData: Any = other.data[position]


    // check for nulls: define null less than everything
    return if (data == null && rowData == null) {
      0
    } else if (data == null) {
      -1
    } else if (rowData == null) {
      1
    } else {
      column.compareTo(data, rowData)
    }
  }
}
