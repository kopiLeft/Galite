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
package org.kopi.galite.ui.vaadin.report

import org.kopi.galite.report.MReport
import org.kopi.galite.report.Point
import org.kopi.galite.report.UReport
import org.kopi.galite.report.VReport
import org.kopi.galite.ui.vaadin.visual.DWindow

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
    TODO()
  }

  override fun build() {
    TODO()
  }

  override fun redisplay() {
    TODO()
  }

  override fun removeColumn(position: Int) {
    TODO()
  }

  override fun addColumn(position: Int) {
    TODO("Not yet implemented")
  }

  override fun addColumn() {
    TODO("Not yet implemented")
  }

  override fun getTable(): UReport.UTable {
    TODO("Not yet implemented")
  }

  override fun contentChanged() {
    TODO("Not yet implemented")
  }

  override fun columnMoved(pos: IntArray) {
    TODO("Not yet implemented")
  }

  override fun resetWidth() {
    TODO("Not yet implemented")
  }

  override fun getSelectedColumn(): Int {
    TODO("Not yet implemented")
  }

  override fun getSelectedCell(): Point {
    TODO("Not yet implemented")
  }

  override fun setColumnLabel(column: Int, label: String) {
    TODO("Not yet implemented")
  }

  /**
   * Return the columns display order.
   * @return The columns display order.
   */
  val displayOrder: IntArray
    get() {
      val displayOrder = IntArray(model.getColumnCount())
      for (i in 0 until model.getColumnCount()) {
        displayOrder[i] = table!!.convertColumnIndexToModel(i)
      }
      return displayOrder
    }

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
  private val model: MReport = report.model // report model
  private var table: DTable? = null
}
