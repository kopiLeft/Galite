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

import org.kopi.galite.report.Constants
import org.kopi.galite.report.UReport.UTable
import org.kopi.galite.report.VFixnumColumn
import org.kopi.galite.report.VIntegerColumn
import org.kopi.galite.report.VReportColumn
import org.kopi.galite.report.VSeparatorColumn

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.grid.ItemClickEvent
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.function.ValueProvider

/**
 * The `DTable` is a table implementing the [UTable]
 * specifications.
 *
 * @param model The table model.
 */
@CssImport("./styles/galite/report.css")
class DTable(val model: VTable) : Grid<DReport.ReportModelItem>(), UTable, ComponentEventListener<ItemClickEvent<DReport.ReportModelItem>> {

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
  var viewColumns: List<Int>? = null

  val columnToHeaderMap = mutableMapOf<Column<*>, VerticalLayout>()

  init {
    setItems(model)
    buildColumns()
    addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS)
    classNames.add("small")
    classNames.add("borderless")
    classNames.add("report")
    setWidthFull()
    addItemClickListener(this)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Builds the grid columns.
   */
  private fun buildColumns() {
    model.accessibleColumns.forEachIndexed { index, column ->
      val align = if (column!!.align == Constants.ALG_RIGHT) {
        ColumnTextAlign.END
      } else {
        ColumnTextAlign.START
      }

      val gridColumn = addColumn(index, column)

      gridColumn
        .setHeader(getColumnNameComponent(column, gridColumn))
        .setAutoWidth(true)
        .setTextAlign(align)
    }
  }

  /**
   * Returns a component containing the column name of a given column.
   *
   * @param column The report column.
   * @return The column name container.
   */
  fun getColumnNameComponent(column: VReportColumn, gridColumn: Column<DReport.ReportModelItem>): Component =
          VerticalLayout(Span(column.label))
            .also {
              it.element.setProperty("title", column.help)
              columnToHeaderMap[gridColumn] = it
            }

  /**
   * Maps the index of the column in the grid at [viewColumnIndex] to the index of the column in the table model.
   */
  override fun convertColumnIndexToModel(viewColumnIndex: Int): Int {
    return viewColumns?.get(viewColumnIndex) ?: viewColumnIndex
  }

  /**
   * Maps the index of the column in the table model at [modelColumnIndex] to the index of the column in the grid.
   */
  override fun convertColumnIndexToView(modelColumnIndex: Int): Int {
    return viewColumns?.indexOf(modelColumnIndex) ?: modelColumnIndex
  }

  override fun onComponentEvent(event: ItemClickEvent<DReport.ReportModelItem>?) {
    //TODO("Not yet implemented")
  }

  /**
   * Adds a new text column to this table with a column value provider and a key for the column.
   *
   * @param key                   the key of the column provider
   * @param column                the report column model
   * @return the created column
   */
  fun addColumn(key: Int, column: VReportColumn = model.accessibleColumns[key]!!): Column<DReport.ReportModelItem> {
    return super.addColumn(ColumnValueProvider(key)).also {
      it.setKey(key.toString())
        .setClassNameGenerator(ColumnStyleGenerator(model.model, column))
    }
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
    } else if (column is VFixnumColumn || column is VIntegerColumn) {
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

    columns[pos].width = "${width}px"
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
  inner class ColumnValueProvider(private val columnIndex: Int) : ValueProvider<DReport.ReportModelItem, Any> {
    override fun apply(source: DReport.ReportModelItem): Any = source.getValueAt(columnIndex)
  }
}
