/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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

import java.io.Serializable

import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.report.VReportRow

class MPivotTable : Serializable {

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  // Columns contains all columns defined by the user
  var columns = mutableListOf<VPivotTableColumn?>()    // array of column definitions

  // Baserows contains data give by the request of the user
  internal var userRows: ArrayList<VPivotTableRow>? = ArrayList()

  /**
   * Add a row to the list of rows defined by the user
   */
  fun addLine(line: Array<Any?>) {
    userRows!!.add(VPivotTableRow(line))
  }

  /**
   * Build the base row + initialisation
   */
  internal fun build() {
    // build accessible columns

    if (userRows!!.size == 0) {
      throw VNoRowException(MessageCode.getMessage("VIS-00015"))
    }
  }

  // --------------------------------------------------------------------
  // MEMBER ACCESS
  // --------------------------------------------------------------------
  /**
   * Return a column definition
   *
   * @param    column        the index of the desired column
   * @return    the desired column
   */
  fun getModelColumn(column: Int): VPivotTableColumn = columns[column]!!
  /**
   * Returns the number of model columns
   *
   * @return    the number or columns to display
   */
  fun getModelColumnCount(): Int = columns.size

  /**
   * Returns the number of rows.
   */
  fun getRowCount(): Int = userRows!!.size

  /**
   * Return a row definition
   *
   * @param    row        the index of the desired row
   * @return    the desired row
   */
  fun getRow(row: Int): VPivotTableRow? = userRows!![row]
}
