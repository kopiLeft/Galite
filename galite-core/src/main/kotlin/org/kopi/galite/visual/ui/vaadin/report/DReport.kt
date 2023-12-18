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
package org.kopi.galite.visual.ui.vaadin.report

import java.awt.Color

import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.report.*
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.accessAndPush
import org.kopi.galite.visual.ui.vaadin.visual.DWindow

import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * The `DReport` is the visual part of the [VReport] model.
 *
 * The `DReport` ensure the implementation of the [UReport]
 * specifications.
 *
 * @param report The report model.
 */
class DReport(private val report: VReport) : DWindow(report), UReport {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val model: MReport = report.model // report model
  private lateinit var table: DTable
  private var parameters: Parameters? = null
  private var columnsSelector: ColumnsSelector = ColumnsSelector()

  init {
    model.addReportListener(this)
    getModel()!!.setDisplay(this)
    setSizeFull()

    add(columnsSelector)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun run() {
    report.initReport()
    report.setMenu()
    table.focus()
    setInfoTable()
  }

  override fun build() {
    // load personal configuration
    parameters = Parameters(Color(71, 184, 221))
    table = DTable(VTable(model, buildRows()))
    table.isColumnReorderingAllowed = true
    table.cellStyler = ReportCellStyler(model, parameters!!, table)
    // 200 px is approximately the header window size + the actor pane size
    ui.ifPresent { ui ->
      ui.page.retrieveExtendedClientDetails {
        table.setHeight(it.windowInnerHeight.toFloat() - 200, Unit.PIXELS)
      }
    }
    setContent(table)
    resetWidth()
    addTableListeners()

    columnsSelector.build(table)
  }

  override fun redisplay() {
    contentChanged()
  }

  /**
   * Reorders the report columns.
   * @param newOrder The new columns order.
   */
  fun reorder(newOrder: IntArray) {
    model.columnMoved(newOrder)
    accessAndPush(currentUI) {
      table.setColumnOrder(
        newOrder.map { table.getColumnByKey(it.toString()) }
      )
      for (col in 0 until model.getAccessibleColumnCount()) {
        table.getColumnByKey(col.toString()).isVisible =
          !model.getAccessibleColumn(col)!!.isFolded
                  || model.getAccessibleColumn(col) is VSeparatorColumn
      }
    }
  }

  override fun removeColumn(position: Int) {
    val indexInView = table.convertColumnIndexToView(position)

    model.removeColumn(position)
    table.removeColumnByKey(position.toString())
    model.initializeAfterRemovingColumn(indexInView)

    // set new order.
    val pos = IntArray(model.getAccessibleColumnCount())
    for (i in 0 until model.getAccessibleColumnCount()) {
      pos[i] = if (model.getDisplayOrder(i) > position) model.getDisplayOrder(i) - 1 else model.getDisplayOrder(i)
    }
    report.columnMoved(pos)
  }

  override fun addColumn(position: Int) {
    accessAndPush(currentUI) {
      var updatePosition = position
      updatePosition = table.convertColumnIndexToView(updatePosition)
      updatePosition += 1
      val headerLabel = "col" + model.getColumnCount()
      val span = VerticalLayout(Span(headerLabel))
      model.addColumn(headerLabel)
      val column = table.addColumn(model.getColumnCount() - 1)
      column.setHeader(span)
      column.flexGrow = 0
      addHeaderListeners(column, span)
      // move last column to Position.
      val pos = IntArray(model.getAccessibleColumnCount())
      for (i in 0 until updatePosition) {
        pos[i] = model.getDisplayOrder(i)
      }
      for (i in updatePosition + 1 until model.getAccessibleColumnCount()) {
        pos[i] = model.getDisplayOrder(i - 1)
      }
      pos[updatePosition] = model.getDisplayOrder(model.getAccessibleColumnCount() - 1)
      report.columnMoved(pos)
    }
  }

  override fun addColumn() {
    addColumn(table.getColumnCount() - 1)
  }

  override fun getTable(): UReport.UTable {
    return table
  }

  override fun contentChanged() {
    if (this::table.isInitialized) {
      accessAndPush(currentUI) {
        table.setItems(buildRows())
        table.model.fireContentChanged()
        columnsSelector.build(table)
      }
    }
  }

  override fun columnMoved(pos: IntArray) {
    reorder(pos)
    model.columnMoved(pos)
    redisplay()
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
   * @return tThe number of columns displayed
   */
  val columnCount: Int
    get() = table.getColumnCount()

  /**
   * Add listeners to the report table.
   */
  private fun addTableListeners() {
    val currentModel: MReport = model

    table.columnToHeaderMap.forEach { (gridColumn, header) ->
      addHeaderListeners(gridColumn, header)
    }

    // Listeners for item click to fold and unfold the row
    table.addItemClickListener { event ->
      val row = event.item.rowIndex
      val col = event.column.key.toInt()
      if (event.button == 0) {
        if (event.clickCount == 2) {
          if (currentModel.isRowLine(row)) {
            getModel()!!.performAsyncAction(object : Action("edit_line") {
              override fun execute() {
                try {
                  report.editLine()
                } catch (ve: VException) {
                  // exception thrown by trigger.
                  throw ve
                }
              }
            })
          } else {
            if (row >= 0) {
              if (currentModel.isRowFold(row, col)) {
                currentModel.unfoldingRow(row, col)
              } else {
                currentModel.foldingRow(row, col)
              }
            }
          }
        } else if (event.isShiftKey && event.isCtrlKey) {
          currentModel.sortColumn(col)
        } else if (event.isCtrlKey) {
          if (row >= 0) {
            if (currentModel.isRowFold(row, col)) {
              currentModel.unfoldingRow(row, col)
            } else {
              currentModel.foldingRow(row, col)
            }
          }
        } else if (event.isShiftKey) {
          if (currentModel.isColumnFold(col)) {
            currentModel.unfoldingColumn(col)
          } else {
            currentModel.foldingColumn(col)
          }
        }
      } else if (event.button == 2) {
        if (row >= 0) {
          if (currentModel.isRowFold(row, col)) {
            currentModel.unfoldingRow(row, col)
          } else {
            currentModel.foldingRow(row, col)
          }
        }
      } else if (event.button == 1) {
        if (currentModel.isColumnFold(col)) {
          currentModel.unfoldingColumn(col)
        } else {
          currentModel.foldingColumn(col)
        }
      }
    }

    // Listener for column reorder
    table.addColumnReorderListener { event ->
      table.viewColumns = event.columns.map { it.key.toInt() }.toMutableList()
      val newColumnOrder = IntArray(model.getColumnCount())
      val visibleColumns = table.viewColumns
      var hiddenColumnsCount = 0
      for (i in newColumnOrder.indices) {
        if (!model.getAccessibleColumn(i)!!.isVisible) {
          hiddenColumnsCount += 1
          newColumnOrder[i] = model.getDisplayOrder(i)
        } else {
          newColumnOrder[i] = visibleColumns[i - hiddenColumnsCount]
        }
      }
      model.columnMoved(newColumnOrder)
    }

    /*table.addListener(object : ColumnCollapseListener() { TODO
      fun columnCollapsed(event: ColumnCollapseEvent) {
        for (i in 0 until model.getAccessibleColumnCount()) {
          model.getAccessibleColumn(i)!!.isFolded = false
        }
        for (propertyId: Any in event.getPropertyIds()) {
          val col: Int = propertyId as String?. toInt () - 1
          model.getAccessibleColumn(col)!!.isFolded = true
        }
        table.dataCommunicator.reset() // TODO
      }
    })*/

    table.addSelectionListener {
      report.setMenu()
    }

    table.dataProvider.addDataProviderListener {
      setInfoTable()
      table.resetCachedInfos()
    }
  }

  // Issue: https://github.com/vaadin/flow-components/issues/1520
  val contextMenuList = mutableListOf<ContextMenu>()

  private fun addHeaderListeners(gridColumn: Grid.Column<*>, header: VerticalLayout) {
    val currentModel: MReport = model
    val labelPopupMenu = ContextMenu()

    contextMenuList.add(labelPopupMenu)

    labelPopupMenu.target = header

    labelPopupMenu.addItem(VlibProperties.getString("set_column_info")) {
      table.selectedColumn = getSelectedColumnIndex(gridColumn)
      getModel()!!.performAsyncAction(object : Action("set_column_info") {
        override fun execute() {
          try {
            report.setColumnInfo()
          } catch (ve: VException) {
            // exception thrown by trigger.
            throw ve
          }
        }
      })
    }
    labelPopupMenu.addItem(VlibProperties.getString("sort_ASC")) {
      currentModel.sortColumn(getSelectedColumnIndex(gridColumn), 1)
    }
    labelPopupMenu.addItem(VlibProperties.getString("sort_DSC")) {
      currentModel.sortColumn(getSelectedColumnIndex(gridColumn), -1)
    }
    labelPopupMenu.addItem(VlibProperties.getString("add_column")) {
      addColumn(getSelectedColumnIndex(gridColumn))
    }
    if (currentModel.getAccessibleColumn(getSelectedColumnIndex(gridColumn))!!.isAddedAtRuntime) {
      labelPopupMenu.addItem(VlibProperties.getString("remove_column")) {
        removeColumn(getSelectedColumnIndex(gridColumn))
      }
      labelPopupMenu.addItem(VlibProperties.getString("set_column_data")) {
        table.selectedColumn = getSelectedColumnIndex(gridColumn)
        getModel()!!.performAsyncAction(object : Action("set_column_data") {
          override fun execute() {
            try {
              report.setColumnData()
            } catch (ve: VException) {
              // exception thrown by the trigger.
              throw ve
            }
          }
        })
      }
    }

    header.addClickListener { event ->
      if (event.button == 0) { // TODO do we need this check?
        if (event.isCtrlKey) {
          if (currentModel.isColumnFold(getSelectedColumnIndex(gridColumn))) {
            currentModel.unfoldingColumn(getSelectedColumnIndex(gridColumn))
          } else {
            currentModel.foldingColumn(getSelectedColumnIndex(gridColumn))
          }
        } else if (event.isShiftKey) {
          currentModel.sortColumn(getSelectedColumnIndex(gridColumn))
        }
      }
    }
  }

  fun getSelectedColumnIndex(gridColumn: Grid.Column<*>): Int = gridColumn.key.toInt()

  /**
   * Display table information in the footer of the table
   */
  private fun setInfoTable() {
    setStatisticsText(
      table.model.model.getRowCount()
        .toString() + "/"
              + model.getBaseRowCount()
              + "/"
              + model.getVisibleRowCount()
    )
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
      return model.accessibleColumns[columnIndex]!!.format(model.getValueAt(rowIndex, columnIndex))
    }

    val reportRow: VReportRow? get() = model.getRow(rowIndex)
  }
}
