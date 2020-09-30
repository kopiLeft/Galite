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

package org.kopi.galite.ui.report

import org.kopi.galite.report.*
import org.kopi.galite.report.UReport.UTable
import org.kopi.galite.report.VReport
import org.kopi.galite.ui.visual.DWindow
import java.awt.Color


/**
 * The `DReport` is the visual part of the [VReport] model.
 *
 * The `DReport` ensure the implementation of the [UReport]
 * specifications.
 *
 * @param report The report model.
 */
class DReport(val report: VReport) : DWindow(report), UReport {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  override fun run() {
    report.initReport()
    table.focus()
  }

  override fun build() {
    // load personal configuration
    parameters = Parameters(Color(71, 184, 221))
    table = DTable(VTable(model))
    table.isColumnReorderingAllowed = true
    //table.setItems(model.visibleRows!!.toList())
    repeat(model.getColumnCount()) { columnIndex ->
      table.addColumn {
        it.data[columnIndex]
      }.setHeader(model.getColumnName(columnIndex))
    }
    setContent(table)
  }

  override fun redisplay() {
    TODO()
  }

  /**
   * Reorders the report columns.
   * @param newOrder The new columns order.
   */
  fun reorder(newOrder: IntArray) {
    TODO()
  }

  override fun removeColumn(position: Int) {
    model.removeColumn(position)
    model.initializeAfterRemovingColumn(table.convertColumnIndexToView(position))

    // set new order.
    val pos = IntArray(model.getAccessibleColumnCount())
    for (i in 0 until model.getAccessibleColumnCount()) {
      pos[i] = if (model.getDisplayOrder(i) > position) model.getDisplayOrder(i) - 1 else model.getDisplayOrder(i)
    }
    table.fireStructureChanged()
    report.columnMoved(pos)
  }

  override fun addColumn(position: Int) {
    var position = position
    position = table.convertColumnIndexToView(position)
    position += 1
    val headerLabel = "col" + model.getColumnCount()
    model.addColumn(headerLabel, position)
    // move last column to position.
    val pos = IntArray(model.getAccessibleColumnCount())
    for (i in 0 until position) {
      pos[i] = model.getDisplayOrder(i)
    }
    for (i in position + 1 until model.getAccessibleColumnCount()) {
      pos[i] = model.getDisplayOrder(i - 1)
    }
    pos[position] = model.getDisplayOrder(model.getAccessibleColumnCount() - 1)
    table.fireStructureChanged()
    report.columnMoved(pos)
  }

  override fun addColumn() {
    TODO()
  }

  override fun getTable(): UTable {
    return table
  }

  override fun contentChanged() {
    TODO()
  }

  override fun columnMoved(pos: IntArray) {
    reorder(pos)
    model.columnMoved(pos)
    redisplay()
  }

  override fun resetWidth() {
    TODO()
  }

  override fun getSelectedColumn(): Int {
    TODO("Not yet implemented")
  }

  override fun getSelectedCell(): Point {
    TODO("Not yet implemented")
  }

  override fun setColumnLabel(column: Int, label: String) {
    TODO()
  }

  /**
   * Notify the report table that the report content has been
   * change in order to update the table content.
   */
  fun fireContentChanged() {
    TODO()
  }

  /**
   * Return the columns display order.
   * @return The columns display order.
   */
  val displayOrder: IntArray
    get() {
      val displayOrder = IntArray(model.getColumnCount())
      for (i in 0 until model.getColumnCount()) {
        displayOrder[i] = table.convertColumnIndexToModel(i)
      }
      return displayOrder
    }

  override fun setTitle(title: String) {
    super.setTitle(title)
  }

  /**
   * Returns the number of columns displayed in the table
   * @return tThe number or columns displayed
   */
  fun getColumnCount(): Int =
          TODO()

  /**
   * Add listeners to the report table.
   */
  private fun addTableListeners() {
    TODO()
  }

  /**
   * Display table informations in the footer of the table
   */
  private fun setInfoTable() {
    TODO()
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var model: MReport = report.model // report model
  lateinit var table: DTable
  private var parameters: Parameters? = null
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new `DReport` instance.
   */
  init {
    model.addReportListener(this)
    getModel().setDisplay(this)
  }
}
