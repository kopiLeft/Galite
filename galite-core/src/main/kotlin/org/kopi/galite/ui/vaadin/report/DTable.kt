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
import org.kopi.galite.report.UReport.UTable
import org.kopi.galite.report.VReportColumn
import org.kopi.galite.report.VReportRow

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.grid.ColumnReorderEvent
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.grid.ItemClickEvent
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.function.ValueProvider

/**
 * The `DTable` is a table implementing the [UTable]
 * specifications.
 *
 * @param model The table model.
 */
class DTable(val model: MReport) : Grid<VReportRow>(), UTable, ComponentEventListener<ItemClickEvent<VReportRow>> {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * The table selected row.
   */
  var selectedRow = -1

  /**
   * The selected column.
   */
  var selectedColumn = -1

  /**
   * The indexes of the columns in the grid view
   */
  var viewColumns: List<Int>? = null

  init {
    buildColumns()
    buildRows()
    addThemeVariants(GridVariant.LUMO_COMPACT)
    classNames.add("small")
    classNames.add("borderless")
    classNames.add("report")
    width = "100%"
    addItemClickListener(this)
    addColumnReorderListener(::onReoder)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Builds the grid columns.
   */
  private fun buildColumns() {
    model.accessibleColumns.forEachIndexed { index, vReportColumn ->
      addColumn(ColumnValueProvider(index), index)
              .setHeader(getColumnNameComponent(vReportColumn!!))
    }
  }

  /**
   * Returns a component containing the column name of a given column.
   *
   * @param column The report column.
   * @return The column name container.
   */
  private fun getColumnNameComponent(column: VReportColumn): Component =
          Div().also {
            it.text = column.label
            it.element.setProperty("title", column.help)
          }

  /**
   * Builds the grid rows.
   */
  private fun buildRows() {
    setItems(model.getRows().toList())
  }

  /**
   * Called when grid columns are reordered.
   *
   * @param event the column reorder event. Provides the list of grid columns with the new order.
   */
  fun onReoder(event: ColumnReorderEvent<VReportRow>) {
    viewColumns = event.columns.map { it.key.toInt() }
  }

  /**
   * Maps the index of the column in the grid at [viewColumnIndex] to the index of the column in the table model.
   */
  override fun convertColumnIndexToModel(viewColumnIndex: Int): Int {
    return viewColumns?.indexOf(viewColumnIndex) ?: viewColumnIndex
  }

  /**
   * Maps the index of the column in the table model at [modelColumnIndex] to the index of the column in the grid.
   */
  override fun convertColumnIndexToView(modelColumnIndex: Int): Int {
    return viewColumns?.get(modelColumnIndex) ?: modelColumnIndex
  }

  override fun onComponentEvent(event: ItemClickEvent<VReportRow>?) {
    //TODO("Not yet implemented")
  }

  /**
   * Adds a new text column to this table with a column value provider and a key for the column.
   *
   * @param columnValueProvider   the value provider
   * @param key                   the key of the column provider
   * @return the created column
   */
  fun addColumn(columnValueProvider: ColumnValueProvider, key: Int): Column<VReportRow> {
    return super.addColumn(columnValueProvider).also {
      it.setKey(key.toString())
    }
  }

  /**
   * Provides the value for the column with index [columnIndex]
   *
   * @param columnIndex the index of the column
   */
  inner class ColumnValueProvider(private val columnIndex: Int): ValueProvider<VReportRow, Any> {
    override fun apply(source: VReportRow): Any =
            model.getAccessibleColumn(columnIndex)!!.format(source.getValueAt(columnIndex))
  }
}
