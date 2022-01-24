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

package org.kopi.galite.visual.report

import org.kopi.galite.visual.visual.UWindow

/**
 * `UReport` is the top-level interface that must be implemented
 * by all dynamic reports. It is the visual component of the [VReport] model.
 */
interface UReport : UWindow, ReportListener {
  /**
   * Builds the report;
   */
  fun build()

  /**
   * Redisplays the report
   */
  fun redisplay()

  /**
   * Fired when report columns has moved.
   * @param pos The new columns positions
   */
  fun columnMoved(pos: IntArray)

  /**
   * Removes a column having the position `position`
   * @param position The column position
   */
  fun removeColumn(position: Int)

  /**
   * Adds a column at the position `position`
   * @param position The column position
   */
  fun addColumn(position: Int)

  /**
   * Adds a column at the end of the report
   */
  fun addColumn()

  /**
   * Returns the report table.
   */
  fun getTable(): UTable

  /**
   * Reset columns width
   */
  fun resetWidth()

  /**
   * Returns the selected column
   */
  fun getSelectedColumn(): Int

  /**
   * Returns the coordinate of the selected cell
   * The index of the column is relative to the model
   */
  fun getSelectedCell(): Point

  /**
   * Sets the column label.
   * @param column The column number.
   * @param label The column label
   */
  fun setColumnLabel(column: Int, label: String)

  /**
   * [UTable] is a report table ensuring conversion between
   * visible indexes and model indexes
   */
  interface UTable {
    /**
     * Maps the index of the column in the view at [viewColumnIndex] to the index of the column in the table model.
     */
    fun convertColumnIndexToModel(viewColumnIndex: Int): Int

    /**
     * Maps the index of the column in the table model at [modelColumnIndex] to the index of the column in the view.
     */
    fun convertColumnIndexToView(modelColumnIndex: Int): Int
  }
}
