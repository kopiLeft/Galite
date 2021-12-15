/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.common

import org.kopi.galite.visual.ui.vaadin.label.Label
import org.vaadin.stefan.table.Table
import org.vaadin.stefan.table.TableCell

import com.vaadin.flow.component.Component

open class VTable(rowsNumber: Int, colsNumber: Int) : Table() {

  init {
    for (i in 0 until rowsNumber) {
      val tr = addRow()
      for (j in 0 until colsNumber) {
        tr.addDataCell()
      }
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the amount of spacing to be added around all cells.
   *
   * @param spacing the cell spacing, in pixels
   */
  fun setCellSpacing(spacing: Int) {
    element.setProperty("cellSpacing", spacing.toDouble())
  }

  /**
   * Sets the amount of padding to be added around all cells.
   *
   * @param padding the cell padding, in pixels
   */
  fun setCellPadding(padding: Int) {
    element.setProperty("cellPadding", padding.toDouble())
  }

  /**
   * Sets the width of the table's border. This border is displayed around all
   * cells in the table.
   *
   * @param width the width of the border, in pixels
   */
  fun setBorderWidth(width: Int) {
    element.setProperty("border", "" + width)
  }

  /**
   * Adds the components to this table in a specific cell identified by the row and column number.
   */
  fun add(row: Int, column: Int, vararg components: Component) {
    val cell = getCellAt(row, column)

    components.forEach {
      cell.add(it)

      // FIXME: styling temporary workaround
      if(it is Label) {
        cell.style["padding-top"] = "5px"
      }
    }
  }

  /**
   * Sets the alignment of a cell.
   *
   * @param row the cell's row.
   * @param column the cell's column.
   * @param right Is it right aligned ?
   */
  fun setAlignment(row: Int, column: Int, right: Boolean) {
    getCellAt(row, column)
      .element
      .setProperty("align", if (right) "right" else "left")
  }

  /**
   * Sets the column span for the given cell identified by row and column number.
   *
   * @param row The cell's row.
   * @param row The cell's column.
   * @param colSpan the cell's column span.
   */
  fun setColSpan(row: Int, column: Int, colSpan: Int) {
    getCellAt(row, column).colSpan = colSpan
  }


  /**
   * Sets the row span for the given cell identified by row and column number.
   *
   * @param row the cell's row.
   * @param column the cell's column.
   * @param rowSpan the cell's column span.
   */
  fun setRowSpan(row: Int, column: Int, rowSpan: Int) {
    getCellAt(row, column).rowSpan = rowSpan
  }

  /**
   * Returns the table cell identified by the row and column number.
   *
   * @param row the cell's row.
   * @param column the cell's column.
   */
  fun getCellAt(row: Int, column: Int): TableCell {
    val tableRow = getRow(row).orElseGet {
      throw IndexOutOfBoundsException("Row index out of range: $row")
    }

    val tableCell = tableRow.getCell(column).orElseGet {
      throw IndexOutOfBoundsException("Column index out of range: $column")
    }

    return tableCell
  }

  fun addInNewRow(component: Component) {
    val tr = addRow()

    tr.addDataCell().add(component)
  }

  /**
   * Returns the table cell identified by the row and column number.
   *
   * @param row the cell's row.
   * @param column the cell's column.
   */
  fun getCellAtOrNull(row: Int, column: Int): TableCell? {
    val tableRow = getRow(row).orElseGet(null)

    return tableRow?.getCell(column)?.orElseGet(null)
  }

  val rowCount: Int get() = streamRows().count().toInt()
}
