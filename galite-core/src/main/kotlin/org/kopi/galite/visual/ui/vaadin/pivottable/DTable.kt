///*
// * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
// * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License version 2.1 as published by the Free Software Foundation.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
// */
//package org.kopi.galite.visual.ui.vaadin.pivottable
//
//import org.kopi.galite.visual.pivottable.Span
//import org.kopi.galite.visual.report.UReport.UTable
//
//import com.vaadin.flow.component.AttachEvent
//import com.vaadin.flow.component.DetachEvent
//import com.vaadin.flow.component.dependency.CssImport
//import com.vaadin.flow.component.grid.Grid
//import com.vaadin.flow.component.grid.GridVariant
//import com.vaadin.flow.component.page.Page
//import com.vaadin.flow.function.ValueProvider
//import com.vaadin.flow.shared.Registration
//
//import elemental.json.JsonObject
//import elemental.json.JsonValue
//
///**
// * The `DTable` is a table implementing the [UTable]
// * specifications.
// *
// * @param model The table model.
// */
//
//@CssImport.Container(value = [
//  CssImport("./styles/galite/report.css"),
//  CssImport(value = "./styles/galite/report.css", themeFor = "vaadin-grid")
//])
//class DTable(val model: VTable) : Grid<DPivotTable.ReportModelItem>(), UTable {
//
//  //---------------------------------------------------
//  // DATA MEMBERS
//  //---------------------------------------------------
//
//  /**
//   * The table selected row.
//   */
//  val selectedRow: Int get() = asSingleSelect().value?.rowIndex ?: -1
//
//  /**
//   * The selected column.
//   */
//  var selectedColumn = -1
//
//  /**
//   * The indexes of the columns in the grid view
//   */
//  var viewColumns = mutableListOf<Int>()
//
//  lateinit var browserWindowResizeListener: Registration
//
//  init {
//    setItems(model)
//    buildColumns()
//    addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS)
//    themeNames.add("report")
//    classNames.add("small")
//    classNames.add("borderless")
//    classNames.add("report")
//    setWidthFull()
//  }
//
//  //---------------------------------------------------
//  // IMPLEMENTATIONS
//  //---------------------------------------------------
//
//  override fun onAttach(attachEvent: AttachEvent) {
//    val page = attachEvent.ui.page
//
//    page.setWindowHeight()
//
//    browserWindowResizeListener = page.addBrowserWindowResizeListener { event ->
//      height = (event.height - 200).toString() + "px"
//    }
//  }
//
//  private fun Page.setWindowHeight() {
//    val js = "return Vaadin.Flow.getBrowserDetailsParameters();"
//    executeJs(js).then {
//      height = ((it as JsonObject).get<JsonValue>("v-wh").asNumber() - 200).toString() + "px"
//    }
//  }
//
//  override fun onDetach(detachEvent: DetachEvent) {
//    browserWindowResizeListener.remove()
//  }
//
//  /**
//   * Builds the grid columns.
//   */
//  private fun buildColumns() {
//    repeat(model.getColumnCount()) { index ->
//      val gridColumn = addColumn(index)
//
//      gridColumn.flexGrow = 0
//    }
//  }
//
//  /**
//   * Maps the index of the column in the grid at [viewColumnIndex] to the index of the column in the table model.
//   */
//  override fun convertColumnIndexToModel(viewColumnIndex: Int): Int = viewColumns[viewColumnIndex]
//
//  /**
//   * Maps the index of the column in the table model at [modelColumnIndex] to the index of the column in the grid.
//   */
//  override fun convertColumnIndexToView(modelColumnIndex: Int): Int = viewColumns.indexOf(modelColumnIndex)
//
//  /**
//   * Adds a new text column to this table with a column value provider and a key for the column.
//   *
//   * @param key                   the key of the column provider
//   * @return the created column
//   */
//  fun addColumn(key: Int): Column<DPivotTable.ReportModelItem> {
//    val provider = ColumnValueProvider(key)
//
//    viewColumns.add(key)
//
//    return addColumn(provider).also {
//      provider.column = it
//      it.setKey(key.toString())
//        .setResizable(true)
//        .setSortable(false)
//        .setClassNameGenerator { item ->
//          buildString {
//            val isTitle = model.model.isTitle(item.rowIndex, key)
//            val span = model.model.getSpan(item.rowIndex, key)
//
//            if (isTitle) {
//              append("title ")
//            }
//
//            if (item.rowIndex == model.getRowCount () - 1) {
//              append("last-row ")
//            }
//
//            if (span ==  Span.COL) {
//              append("colspan")
//            } else if (span ==  Span.ROW) {
//              append("rowspan")
//            }
//          }
//        }
//    }
//  }
//
//  override fun removeColumnByKey(columnKey: String) {
//    viewColumns.remove(columnKey.toInt())
//    super.removeColumnByKey(columnKey)
//  }
//
//  override fun removeColumn(column: Column<DPivotTable.ReportModelItem>) {
//    viewColumns.remove(column.key.toInt())
//    super.removeColumn(column)
//  }
//
//  /**
//   * Returns the column count.
//   * @return the column count.
//   */
//  fun getColumnCount(): Int = model.getColumnCount()
//
//  /**
//   * Reset all columns widths.
//   */
//  fun resetWidth() {
//    for (i in 0 until model.getColumnCount()) {
//      resetColumnSize(i)
//    }
//  }
//
//  /**
//   * Resets the column size at a given position.
//   * @param pos The column position.
//   */
//  private fun resetColumnSize(pos: Int) {
//
//  }
//
//  /**
//   * Provides the value for the column with index [columnIndex]
//   *
//   * @param columnIndex the index of the column
//   */
//  inner class ColumnValueProvider(
//    private val columnIndex: Int
//  ) : ValueProvider<DPivotTable.ReportModelItem, String> {
//    var column: Column<DPivotTable.ReportModelItem>? = null
//
//    override fun apply(source: DPivotTable.ReportModelItem): String {
//      return source.getValueAt(columnIndex)
//    }
//  }
//}
