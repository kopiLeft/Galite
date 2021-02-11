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

  init {
    buildColumns()
    buildRows()
    addThemeVariants(GridVariant.LUMO_COMPACT)
    classNames.add("small")
    classNames.add("borderless")
    classNames.add("report")
    width = "100%"
    addItemClickListener(this)
  }

  /**
   * Builds the grid columns.
   */
  private fun buildColumns() {
    model.accessibleColumns.forEachIndexed { index, vReportColumn ->
      addColumn(ColumnValueProvider(index))
              .setKey(index.toString())
              .setHeader(getColumnNameComponent(vReportColumn!!))
    }
  }

  /**
   * Returns a component containing the column name of a given column.
   * @param column The report column.
   * @return The column name container.
   */
  private fun getColumnNameComponent(column: VReportColumn): Component =
          Div().also {
            it.text = column.label
            it.element.setProperty("title", column.help)
          }

  private fun buildRows() {
    setItems(model.getRows().toList())
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun convertColumnIndexToModel(viewColumnIndex: Int): Int {
    TODO()
  }

  override fun convertColumnIndexToView(modelColumnIndex: Int): Int {
    TODO()
  }

  override fun onComponentEvent(event: ItemClickEvent<VReportRow>?) {
    //TODO("Not yet implemented")
  }

  inner class ColumnValueProvider(private val columnIndex: Int): ValueProvider<VReportRow, Any> {
    override fun apply(source: VReportRow): Any =
            model.getAccessibleColumn(columnIndex)!!.format(source.getValueAt(columnIndex))
  }
}
