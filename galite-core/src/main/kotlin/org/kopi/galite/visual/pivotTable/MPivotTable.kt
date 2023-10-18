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

package org.kopi.galite.visual.pivotTable

import java.io.Serializable

import org.kopi.galite.visual.MessageCode

class MPivotTable : Constants, Serializable {

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  // Columns contains all columns defined by the user
  // accessiblecolumns is a part of columns which contains only visible columns
  var columns = mutableListOf<VReportColumn?>()    // array of column definitions
  var accessibleColumns: Array<VReportColumn?> = arrayOf() // array of visible or hide columns
    private set

  // Baserows contains data give by the request of the user
  internal var userRows: ArrayList<VBaseRow>? = ArrayList(500)
  lateinit var baseRows: Array<VReportRow?>    // array of base data rows

  // displayOrder contains index column model in display order
  // reverseOrder is calculate with displayOrder and contains index column display into model order
  private lateinit var displayOrder: IntArray    // column mapping from display to model
  private lateinit var reverseOrder: IntArray    // column mapping from model to display

  // The displayLevels variable is a table which contains the level of each column
  private lateinit var displayLevels: IntArray   // column levels in display order

  /**
   * Add a row to the list of rows defined by the user
   */
  fun addLine(line: Array<Any?>) {
    userRows!!.add(VBaseRow(line))
  }

  /**
   * Build the base row table + initialisation
   */
  internal fun build() {
    // build accessible columns
    if (userRows!!.size == 0) {
      throw VNoRowException(MessageCode.getMessage("VIS-00015"))
    }
    createAccessibleTab()
    baseRows = userRows!!.toTypedArray()
    userRows = null

//    // build working tables
//    val columnCount = getAccessibleColumnCount()
//    displayOrder = IntArray(columnCount)
//    reverseOrder = IntArray(columnCount)
//    displayLevels = IntArray(columnCount)
//    for (i in 0 until columnCount) {
//      displayOrder[i] = i
//      reverseOrder[i] = i
//      displayLevels[i] = -1
//    }
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
  fun getModelColumn(column: Int): VReportColumn = columns[column]!!

  /**
   * Returns the number of model columns
   *
   * @return    the number or columns to display
   */
  fun getModelColumnCount(): Int = columns.size

  /**
   * Returns the number of columns visible or hide
   *
   * @return    the number or columns in the model
   */
  fun getAccessibleColumnCount(): Int = accessibleColumns.size

  // --------------------------------------------------------------------
  // REDEFINITION OF METHODS IN AbstractTableModel
  // --------------------------------------------------------------------
  /**
   * Returns the number of columns managed by the data source object.
   *
   * @return    the number or columns to display
   */
  fun getColumnCount(): Int = accessibleColumns.size

  /**
   * Makes the table of accessible columns from the columns variable
   */
  private fun createAccessibleTab() {
    val columnCount = columns.size
    var accessiblecolumnCount = 0

    for (i in 0 until columnCount) {
        accessiblecolumnCount += 1
    }
    accessibleColumns = arrayOfNulls(accessiblecolumnCount)
    accessiblecolumnCount = 0
    for (i in 0 until columnCount) {
        accessibleColumns[accessiblecolumnCount++] = columns[i]
    }
  }

  companion object {
    private const val serialVersionUID = 0L
  }
}