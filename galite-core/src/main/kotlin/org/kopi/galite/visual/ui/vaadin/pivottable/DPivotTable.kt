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
package org.kopi.galite.visual.ui.vaadin.pivottable

import java.awt.Color

import org.kopi.galite.visual.pivottable.PivotTable
import org.kopi.galite.visual.pivottable.UPivotTable
import org.kopi.galite.visual.report.Parameters
import org.kopi.galite.visual.report.Point
import org.kopi.galite.visual.report.UReport
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.accessAndPush
import org.kopi.galite.visual.ui.vaadin.visual.DWindow

import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * The `DPivotTable` is the visual part of the [PivotTable] model.
 *
 * The `DPivotTable` ensure the implementation of the [UPivotTable]
 * specifications.
 *
 * @param pivottable The report model.
 */
class DPivotTable(private val pivottable: PivotTable) : DWindow(pivottable), UPivotTable {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val model = pivottable.model // report model
  private lateinit var table: DTable
  private var parameters: Parameters? = null

  init {
    getModel()!!.setDisplay(this)
    setSizeFull()
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun run() {
    pivottable.initPivotTable()
    table.focus()
    setInfoTable()
  }

  override fun build() {
    // load personal configuration
    parameters = Parameters(Color(71, 184, 221))
    table = DTable(VTable(model, buildRows()))
    table.isColumnReorderingAllowed = true
    // 200 px is approximately the header window size + the actor pane size
    ui.ifPresent { ui ->
      ui.page.retrieveExtendedClientDetails {
        table.setHeight(it.windowInnerHeight.toFloat() - 200, Unit.PIXELS)
      }
    }
    setContent(table)
    resetWidth()
    addTableListeners()
  }

  override fun redisplay() {
    contentChanged()
  }

  override fun removeColumn(position: Int) {

  }

  override fun addColumn(position: Int) {

  }

  override fun addColumn() {

  }

  override fun getTable(): UReport.UTable {
    return table
  }

  override fun contentChanged() {
    if (this::table.isInitialized) {
      accessAndPush(currentUI) {
        table.setItems(buildRows())
        table.model.fireContentChanged()
      }
    }
  }

  override fun columnMoved(pos: IntArray) {

  }

  override fun resetWidth() {
    access(currentUI) {
      table.resetWidth()
    }
  }

  override fun getSelectedColumn(): Int {
    return table.selectedColumn
  }

  override fun getSelectedCell(): Point = Point(table.selectedColumn, table.selectedRow)

  override fun setColumnLabel(column: Int, label: String) {
    access(currentUI) {
      table.getColumnByKey(column.toString()).setHeader(label)
    }
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

  /**
   * Returns the number of columns displayed in the table
   * @return The number of columns displayed
   */
  val columnCount: Int
    get() = table.getColumnCount()

  /**
   * Add listeners to the report table.
   */
  private fun addTableListeners() {

  }

  private fun addHeaderListeners(gridColumn: Grid.Column<*>, header: VerticalLayout) {

  }

  fun getSelectedColumnIndex(gridColumn: Grid.Column<*>): Int = gridColumn.key.toInt()

  /**
   * Display table information in the footer of the table
   */
  private fun setInfoTable() {

  }

  /**
   * Builds the grid rows.
   */
  private fun buildRows(): List<ReportModelItem> {
    val rows = mutableListOf<ReportModelItem>()
    for (i in 0 until model.getRowCount()) {
      rows.add(ReportModelItem(i))
    }
    return rows
  }

  //---------------------------------------------------
  // TABLE MODEL ITEM
  //---------------------------------------------------
  /**
   * The `TableModelItem` is the report table
   * data model.
   *
   * @param rowIndex The row index.
   */
  inner class ReportModelItem(val rowIndex: Int) {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    fun getValueAt(columnIndex: Int): String {
      return model.getValueAt(rowIndex, columnIndex)
    }
  }
}
