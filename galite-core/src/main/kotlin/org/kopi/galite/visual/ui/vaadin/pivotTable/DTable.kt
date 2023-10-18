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
package org.kopi.galite.visual.ui.vaadin.pivotTable

import org.kopi.galite.visual.pivotTable.Constants
import org.kopi.galite.visual.pivotTable.UPivotTable.UTable
import org.kopi.galite.visual.pivotTable.VDecimalColumn
import org.kopi.galite.visual.pivotTable.VIntegerColumn
import org.kopi.galite.visual.pivotTable.VReportColumn
import org.kopi.galite.visual.pivotTable.VSeparatorColumn

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.Page
import com.vaadin.flow.function.ValueProvider
import com.vaadin.flow.shared.Registration

import elemental.json.JsonObject
import elemental.json.JsonValue

/**
 * The `DTable` is a table implementing the [UTable]
 * specifications.
 *
 * @param model The table model.
 */

@CssImport.Container(value = [
  CssImport("./styles/galite/report.css"),
  CssImport(value = "./styles/galite/report.css", themeFor = "vaadin-grid")
])
class DTable(val model: VTable) : Grid<DPivotTable.ReportModelItem>(), UTable {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * The table selected row.
   */
  val selectedRow: Int get() = asSingleSelect().value?.rowIndex ?: -1

  /**
   * The selected column.
   */
  var selectedColumn = -1

  /**
   * The indexes of the columns in the grid view
   */
  var viewColumns = mutableListOf<Int>()

  val columnToHeaderMap = mutableMapOf<Column<*>, VerticalLayout>()

  lateinit var cellStyler: ReportCellStyler
  lateinit var browserWindowResizeListener: Registration

  init {
    buildColumns()
    addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS)
    themeNames.add("report")
    classNames.add("small")
    classNames.add("borderless")
    classNames.add("report")
    setWidthFull()
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  override fun onAttach(attachEvent: AttachEvent) {
    val page = attachEvent.ui.page

    page.setWindowHeight()

    browserWindowResizeListener = page.addBrowserWindowResizeListener { event ->
      height = (event.height - 200).toString() + "px"
    }
  }

  private fun Page.setWindowHeight() {
    val js = "return Vaadin.Flow.getBrowserDetailsParameters();"
    executeJs(js).then {
      height = ((it as JsonObject).get<JsonValue>("v-wh").asNumber() - 200).toString() + "px"
    }
  }

  override fun onDetach(detachEvent: DetachEvent) {
    browserWindowResizeListener.remove()
  }

  /**
   * Builds the grid columns.
   */
  private fun buildColumns() {
    model.accessibleColumns.forEachIndexed { index, column ->
      val gridColumn = addColumn(index, column!!)

      gridColumn
        .setHeader(getColumnNameComponent(column, gridColumn))

      gridColumn.flexGrow = 0
    }
  }

  /**
   * Returns a component containing the column name of a given column.
   *
   * @param column The report column.
   * @return The column name container.
   */
  fun getColumnNameComponent(column: VReportColumn, gridColumn: Column<DPivotTable.ReportModelItem>): Component =
          VerticalLayout(Span(column.label))
            .also {
              it.element.setProperty("title", column.help)
              columnToHeaderMap[gridColumn] = it
            }

  /**
   * Maps the index of the column in the grid at [viewColumnIndex] to the index of the column in the table model.
   */
  override fun convertColumnIndexToModel(viewColumnIndex: Int): Int = viewColumns[viewColumnIndex]

  /**
   * Maps the index of the column in the table model at [modelColumnIndex] to the index of the column in the grid.
   */
  override fun convertColumnIndexToView(modelColumnIndex: Int): Int = viewColumns.indexOf(modelColumnIndex)

  /**
   * Adds a new text column to this table with a column value provider and a key for the column.
   *
   * @param key                   the key of the column provider
   * @param column                the report column model
   * @return the created column
   */
  fun addColumn(key: Int, column: VReportColumn = model.accessibleColumns[key]!!): Column<DPivotTable.ReportModelItem> {
    val provider = ColumnValueProvider(key, column)

    viewColumns.add(key)

    return addColumn(provider).also {
      provider.column = it
      it.setKey(key.toString())
        .setResizable(true)
        .setClassNameGenerator(ColumnStyleGenerator(model.model, column))
        .setSortable(false)
    }
  }

  override fun removeColumnByKey(columnKey: String) {
    viewColumns.remove(columnKey.toInt())
    super.removeColumnByKey(columnKey)
  }

  override fun removeColumn(column: Column<DPivotTable.ReportModelItem>) {
    viewColumns.remove(column.key.toInt())
    super.removeColumn(column)
  }

  /**
   * Returns the column count.
   * @return the column count.
   */
  fun getColumnCount(): Int = model.getColumnCount()

  /**
   * Reset all columns widths.
   */
  fun resetWidth() {
    for (i in 0 until model.getColumnCount()) {
      resetColumnSize(i)
    }
  }

  /**
   * Resets the column size at a given position.
   * @param pos The column position.
   */
  private fun resetColumnSize(pos: Int) {
    val column = model.model.getAccessibleColumn(convertColumnIndexToModel(pos))
    var width: Int

    if (column!!.isFolded && column !is VSeparatorColumn) {
      width = 1
    } else if (column is VDecimalColumn || column is VIntegerColumn) {
      width = column.label.length.coerceAtLeast(column.width)
      // Integer and Fixed column can contain , data generated by operations like sum, multiplication
      // --> compute column width occording to data.
      width = width.coerceAtLeast(model.model.computeColumnWidth(convertColumnIndexToModel(pos)))
    } else {
      width = column.label.length.coerceAtLeast(column.width)
    }

    if (width != 0) {
      width = width * 9 + 2
    }

    columns[pos].width = "${width + 12}px"
  }

  /**
   * Resets the table cached information.
   */
  fun resetCachedInfos() {
    selectedColumn = -1
    select(null)
  }

  /**
   * Provides the value for the column with index [columnIndex]
   *
   * @param columnIndex the index of the column
   */
  inner class ColumnValueProvider(
    private val columnIndex: Int,
    private val columnModel: VReportColumn
  ) : ValueProvider<DPivotTable.ReportModelItem, String> {
    var column: Column<DPivotTable.ReportModelItem>? = null

    override fun apply(source: DPivotTable.ReportModelItem): String {
      column?.textAlign = if (columnModel.align == Constants.ALG_RIGHT) {
        ColumnTextAlign.END
      } else {
        ColumnTextAlign.START
      }

      cellStyler.updateStyles(source.rowIndex, columnIndex)

      return source.getValueAt(columnIndex)
    }
  }
}
