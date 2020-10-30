/*
* Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*
* $Id: DReport.java 35180 2017-07-14 16:31:39Z hacheni $
*/

package org.kopi.galite.tests.ui.report

import java.awt.Color

import org.kopi.galite.report.MReport
import org.kopi.galite.report.Parameters
import org.kopi.galite.report.Point
import org.kopi.galite.report.UReport
import org.kopi.galite.report.VReport
import org.kopi.galite.report.VSeparatorColumn
import org.kopi.galite.ui.report.VTable
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VlibProperties

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.grid.Grid
import org.kopi.galite.visual.VWindow
import java.io.File

/**
 * The `DReport` is the visual part of the [VReport] model
 * The `DReport` ensure the implementation of the [UReport]
 * specifications.
 * Creates a new `DReport` instance.
 * @param report The report model.
 */
class DReport(report: VReport) : Grid<VTable>(),DWindow(report), UReport {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun run() {
    report.initReport()
    report.setMenu()
    table.focus()
    setInfoTable()
  }

  override fun getModel(): VWindow {
    TODO("Not yet implemented")
  }

  override fun build() {
    // load personal configuration
    parameters = Parameters(Color(71, 184, 221))
    table = DTable(VTable(model))
    table.setSelectable(true)
    table.setSelectionMode(SelectionMode.SINGLE)
    table.setMultiSelectMode(MultiSelectMode.SIMPLE)
    table.isColumnReorderingAllowed = true
    table.setColumnCollapsingAllowed(true)
    table.setNullSelectionAllowed(false)
    table.setCellStyleGenerator(ReportCellStyleGenerator(model, parameters))
    // 200 px is approximately the header window size + the actor pane size
    table.setHeight(UI.getCurrent().getPage().getBrowserWindowHeight() - 200, Unit.PIXELS)
    setContent(table)
    resetWidth()
    addTableListeners()
  }

  override fun redisplay() {
    BackgroundThreadHandler.access(Runnable {
      (table.getCellStyleGenerator() as ReportCellStyleGenerator).updateStyles()
      table.refreshRowCache()
      table.markAsDirty()
      UI.getCurrent().push()
    })
  }

  /**
   * Reorders the report columns.
   * @param newOrder The new columns order.
   */
  fun reorder(newOrder: IntArray) {
    model.columnMoved(newOrder)
    table.setVisibleColumns(newOrder)
    BackgroundThreadHandler.access(Runnable {
      for (col in 0 until model.getAccessibleColumnCount()) {
        if (model.getAccessibleColumn(col).folded &&
                model.getAccessibleColumn(col) !is VSeparatorColumn) {
          table.setColumnCollapsed(col, true)
        } else {
          table.setColumnCollapsed(col, false)
        }
      }
      UI.getCurrent().push()
    })
  }

  override fun removeColumn(position: Int) {
    model.removeColumn(position)
    model.initializeAfterRemovingColumn(table!!.convertColumnIndexToView(position))

    // set new order.
    val pos = IntArray(model.getAccessibleColumnCount())
    for (i in 0 until model.getAccessibleColumnCount()) {
      pos[i] = if (model.getDisplayOrder(i) > position) model.getDisplayOrder(i) - 1 else model.getDisplayOrder(i)
    }
    table!!.fireStructureChanged()
    report.columnMoved(pos)
  }

  fun addColumn(position: Int = table!!.convertColumnIndexToModel(table!!.getColumnCount() - 1)) {
    var position = position
    position = table!!.convertColumnIndexToView(position)
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
    table!!.fireStructureChanged()
    report.columnMoved(pos)
  }

  override fun addColumn() {
    TODO("Not yet implemented")
  }

  override fun getTable(): UReport.UTable? {
    return table
  }

  override fun contentChanged() {
    if (table != null) {
      (table!!.getModel() as VTable?).fireContentChanged()
      BackgroundThreadHandler.access(Runnable { table.refreshRowCache() })
    }
  }

  override fun columnMoved(pos: IntArray) {
    reorder(pos)
    model.columnMoved(pos)
    redisplay()
  }

  override fun resetWidth() {
    BackgroundThreadHandler.access(Runnable { table!!.resetWidth() })
  }

  override fun getSelectedColumn(): Int {
    return table!!.getSelectedColumn()
  }

  fun getSelectedCell(): Point
   = Point(table!!.getSelectedColumn(), table!!.getSelectedRow())

  override fun setColumnLabel(column: Int, label: String) {
    UI.getCurrent().access(Runnable { table.setColumnHeader(column, label) })
  }

  /**
   * Notify the report table that the report content has been
   * change in order to update the table content.
   */
  fun fireContentChanged() {
    if (table != null) {
      table!!.getModel().fireContentChanged()
      synchronized(table!!) { report.setMenu() }
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
        displayOrder[i] = table!!.convertColumnIndexToModel(i)
      }
      return displayOrder
    }

  override fun setTitle(title: String) {
    super.setTitle(title)
  }

  override fun setInformationText(text: String) {
    TODO("Not yet implemented")
  }

  override fun setTotalJobs(totalJobs: Int) {
    TODO("Not yet implemented")
  }

  override fun setCurrentJob(currentJob: Int) {
    TODO("Not yet implemented")
  }

  override fun updateWaitDialogMessage(message: String) {
    TODO("Not yet implemented")
  }

  override fun closeWindow() {
    TODO("Not yet implemented")
  }

  override fun setWindowFocusEnabled(enabled: Boolean) {
    TODO("Not yet implemented")
  }

  override fun performBasicAction(action: Action) {
    TODO("Not yet implemented")
  }

  override fun isEnabled(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setEnabled(enabled: Boolean) {
    TODO("Not yet implemented")
  }

  override fun isVisible(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setVisible(visible: Boolean) {
    TODO("Not yet implemented")
  }

  override fun performAsyncAction(action: Action) {
    TODO("Not yet implemented")
  }

  override fun modelClosed(type: Int) {
    TODO("Not yet implemented")
  }

  override fun setWaitDialog(message: String, maxtime: Int) {
    TODO("Not yet implemented")
  }

  override fun unsetWaitDialog() {
    TODO("Not yet implemented")
  }

  override fun setWaitInfo(message: String) {
    TODO("Not yet implemented")
  }

  override fun unsetWaitInfo() {
    TODO("Not yet implemented")
  }

  override fun setProgressDialog(message: String, totalJobs: Int) {
    TODO("Not yet implemented")
  }

  override fun unsetProgressDialog() {
    TODO("Not yet implemented")
  }

  override fun fileProduced(file: File, name: String) {
    TODO("Not yet implemented")
  }

  /**
   * Returns the number of columns displayed in the table
   * @return tThe number or columns displayed
   */
  fun getColumnCount(): Int = table!!.getColumnCount()

  /**
   * Add listeners to the report table.
   */
  private fun addTableListeners() {
    val currentModel: MReport = model
    val labelPopupMenu = ContextMenu()
    labelPopupMenu.addItemClickListener(object : ContextMenuItemClickListener() {
      fun contextMenuItemClicked(event: ContextMenuItemClickEvent) {
        val clickedItem: ContextMenuItem = event.getSource() as ContextMenuItem
        if (clickedItem.getData().equals(VlibProperties.getString("set_column_info"))) {
          table!!.setSelectedColumn(selectedColumn)
          getModel().performAsyncAction(object : Action("set_column_info") {
          override fun execute() {
              try {
                report.setColumnInfo()
              } catch (ve: VException) {
                // exception thrown by trigger.
                throw ve
              }
            }
          })
        } else if (clickedItem.getData().equals(VlibProperties.getString("sort_ASC"))) {
          currentModel.sortColumn(selectedColumn, 1)
          redisplay()
        } else if (clickedItem.getData().equals(VlibProperties.getString("sort_DSC"))) {
          currentModel.sortColumn(selectedColumn, -1)
          redisplay()
        } else if (clickedItem.getData().equals(VlibProperties.getString("add_column"))) {
          addColumn(selectedColumn)
        } else if (clickedItem.getData().equals(VlibProperties.getString("remove_column"))) {
          removeColumn(selectedColumn)
        } else if (clickedItem.getData().equals(VlibProperties.getString("set_column_data"))) {
          table!!.setSelectedColumn(selectedColumn)
          getModel().performAsyncAction(object : Action("set_column_data") {
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
        labelPopupMenu.hide()
      }
    })
    labelPopupMenu.setAsTableContextMenu(table)
    table!!.addItemClickListener(object : ItemClickListener() {
      fun itemClick(event: ItemClickEvent) {
        val row = (event.getItemId() as Int).toInt()
        val col = (event.getPropertyId() as Int).toInt()
        if (event.getButton() === ClickEvent.BUTTON_LEFT) {
          if (event.isDoubleClick()) {
            if (currentModel.isRowLine(row)) {
              getModel().performAsyncAction(object : Action("edit_line") {
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
          } else if (event.isShiftKey() && event.isCtrlKey()) {
            currentModel.sortColumn(col)
          } else if (event.isCtrlKey()) {
            if (row >= 0) {
              if (currentModel.isRowFold(row, col)) {
                currentModel.unfoldingRow(row, col)
              } else {
                currentModel.foldingRow(row, col)
              }
            }
          } else if (event.isShiftKey()) {
            if (currentModel.isColumnFold(col)) {
              currentModel.unfoldingColumn(col)
            } else {
              currentModel.foldingColumn(col)
            }
          } else {
            BackgroundThreadHandler.access(Runnable { table.refreshRowCache() })
            synchronized(table!!) { report.setMenu() }
          }
        } else if (event.getButton() === ClickEvent.BUTTON_RIGHT) {
          // labelPopupMenu.hide();
          if (row >= 0) {
            if (currentModel.isRowFold(row, col)) {
              currentModel.unfoldingRow(row, col)
            } else {
              currentModel.foldingRow(row, col)
            }
          }
        } else if (event.getButton() === ClickEvent.BUTTON_MIDDLE) {
          if (currentModel.isColumnFold(col)) {
            currentModel.unfoldingColumn(col)
          } else {
            currentModel.foldingColumn(col)
          }
        }
      }
    })
    table!!.addColumnReorderListener(object : ColumnReorderListener() {
      fun columnReorder(event: ColumnReorderEvent?) {
        val newColumnOrder = IntArray(model.getColumnCount())
        val visibleColumns: Array<Any> = table.getVisibleColumns()
        var hiddenColumnsCount = 0
        for (i in newColumnOrder.indices) {
          if (!model.getAccessibleColumn(i).isVisible()) {
            hiddenColumnsCount += 1
            newColumnOrder[i] = model.getDisplayOrder(i)
          } else {
            newColumnOrder[i] = (visibleColumns[i - hiddenColumnsCount] as Int).toInt()
          }
        }
        model.columnMoved(newColumnOrder)
      }
    })
    table!!.addListener(object : ColumnCollapseListener() {
      fun columnCollapsed(event: ColumnCollapseEvent) {
        for (i in 0 until model.getAccessibleColumnCount()) {
          model.getAccessibleColumn(i).setFolded(false)
        }
        for (propertyId in event.getPropertyIds()) {
          val col: Int = propertyId as String?. toInt () - 1
          model.getAccessibleColumn(col).setFolded(true)
        }
        table!!.fireStructureChanged()
      }
    })
    table.addHeaderClickListener(object : HeaderClickListener() {
      fun headerClick(event: HeaderClickEvent) {
        val column = (event.getPropertyId() as Int).toInt()
        if (event.getButton() === ClickEvent.BUTTON_LEFT) {
          if (event.isCtrlKey()) {
            if (currentModel.isColumnFold(column)) {
              currentModel.unfoldingColumn(column)
            } else {
              currentModel.foldingColumn(column)
            }
          } else if (event.isShiftKey()) {
            currentModel.sortColumn(column)
            redisplay()
          }
        } else if (event.getButton() === ClickEvent.BUTTON_RIGHT) {
          selectedColumn = column
          labelPopupMenu.removeAllItems()
          labelPopupMenu.addItem(VlibProperties.getString("set_column_info")).setData(VlibProperties.getString("set_column_info"))
          labelPopupMenu.addItem(VlibProperties.getString("sort_ASC")).setData(VlibProperties.getString("sort_ASC"))
          labelPopupMenu.addItem(VlibProperties.getString("sort_DSC")).setData(VlibProperties.getString("sort_DSC"))
          labelPopupMenu.addItem(VlibProperties.getString("add_column")).setData(VlibProperties.getString("add_column"))
          if (currentModel.getAccessibleColumn(selectedColumn).isAddedAtRuntime()) {
            labelPopupMenu.addItem(VlibProperties.getString("remove_column")).setData(VlibProperties.getString("remove_column"))
            labelPopupMenu.addItem(VlibProperties.getString("set_column_data")).setData(VlibProperties.getString("set_column_data"))
          }
        }
      }
    })
    table.addValueChangeListener(object : ValueChangeListener() {
      fun valueChange(event: ValueChangeEvent) {
        if (event.getProperty().getValue() != null) {
          table!!.setSelectedRow((event.getProperty().getValue() as Int).toInt())
        }
        report.setMenu()
      }
    })
    table.addItemSetChangeListener(object : ItemSetChangeListener() {
      fun containerItemSetChange(event: ItemSetChangeEvent?) {
        setInfoTable()
        table!!.resetCachedInfos()
      }
    })
  }

  /**
   * Display table informations in the footer of the table
   */
  private fun setInfoTable() {
    setStatisticsText(table!!.getRowCount().toString() + "/"
                      + model.getBaseRowCount()
                      + "/"
                      + model.getVisibleRowCount())
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private val model  = report.model // report model
  private val report = report
  private lateinit var table: DTable
  private lateinit var parameters: Parameters
  private var selectedColumn = 0

  init {
    model.addReportListener(this)
    getModel().setDisplay(this)
  }
}