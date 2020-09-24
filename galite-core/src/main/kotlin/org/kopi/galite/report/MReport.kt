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

import java.util.*
import javax.swing.event.EventListenerList

class MReport {

  fun build(): Void = TODO()

  fun createTree(): Void = TODO()

  fun getModelColumnCount(): Int = TODO()

  fun setColumnFolded(column: Int, b: Boolean): Any = TODO()

  fun getModelColumn(i: Int): VReportColumn = TODO()

  fun foldingColumn(column: Int): Any = TODO()

  fun foldingRow(y: Int, x: Int): Any = TODO()

  fun isRowLine(i: Int): Boolean = TODO()

  fun getAccessibleColumn(column: Int): VReportColumn = TODO()

  fun unfoldingColumn(column: Int): Any = TODO()

  fun unfoldingRow(y: Int, x: Int): Any = TODO()

  fun sortColumn(selectedColumn: Int): Int = TODO()

  fun getRow(y: Int): VReportRow = TODO()

  fun getValueAt(): Any = TODO()

  fun isColumnFold(column: Int): Boolean = TODO()

  fun isRowFold(y: Int, x: Int): Boolean = TODO()

  fun isAddedAtRuntime(): Boolean = TODO()
  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  // Columns contains all columns defined by the user
  // accessiblecolumns is a part of columns which contains only visible columns

  // array of column definitions
  lateinit var columns: Array<VReportColumn>

  // array of visible or hide columns
  private val accessiblecolumns: Array<VReportColumn> = TODO()


  // Root is the root of the tree (which is our model to manipulate data)
  private val root // root of grouping tree
          : VGroupRow? = null

  // Baserows contains data give by the request of the user
  // visibleRows contains all data which will be displayed. It's like a buffer. visibleRows
  // is changed when a column move or one or more row are folded
  private val userRows: Vector<VBaseRow>? = null

  // array of base data rows
  private val baseRows: Array<VReportRow> = TODO()

  // array of visible rows
  private val visibleRows: Array<VReportRow>
  private val maxRowCount = 0

  // Sortedcolumn contain the index of the sorted column
  // sortingOrder store the type of sort of the sortedColumn : ascending or descending
  // the table is sorted wrt. to this column
  private val sortedColumn = 0

  // 1: ascending, -1: descending
  private val sortingOrder = 0

  // displayOrder contains index column model in display order
  // reverseOrder is calculate with displayOrder and contains index column display into model order
  // column mapping from display to model
  private val displayOrder: IntArray = TODO()

  // column mapping from model to display
  private val reverseOrder: IntArray

  // The displayLevels variable is a table which contains the level of each column
  // column levels in display order
  private val displayLevels: IntArray

  // List of listeners
  protected var listenerList = EventListenerList()

}
