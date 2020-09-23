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
package org.kopi.galite.report

import org.kopi.galite.report.UReport.UTable
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.Serializable

abstract class PExport(val table: UTable,
                       val model: MReport,
                       val printConfig: PConfig,
                       var title: String,
                       val tonerSaveMode: Boolean = false) : Serializable {

  fun formatColumns() {
    var index = 0
    for (j in 0 until model.getAccessibleColumnCount()) {
      val visibleColumn: Int = table.convertColumnIndexToModel(j)
      val column: VReportColumn = model.getAccessibleColumn(visibleColumn)
      if (column.isVisible() && column.isFolded() == false // if we have a new page for each group, we do not use the first
              // visible column
              && (!printConfig.groupFormfeed || j != firstVisibleColumn)) {
        column.formatColumn(this, index)
        index += 1
      }
    }
  }

  protected fun exportHeader() {
    val data = arrayOfNulls<String>(columnCount)
    var index = 0
    for (j in 0 until model.getAccessibleColumnCount()) {
      val visibleColumn: Int = table.convertColumnIndexToModel(j)
      val column: VReportColumn = model.getAccessibleColumn(visibleColumn)
      if (column.isVisible() && column.isFolded() == false // if we have a new page for each group, we do not use the first
              // visible column
              && (!printConfig.groupFormfeed || j != firstVisibleColumn)) {
        data[index] = column.getLabel()
        index += 1
      }
    }
    exportHeader(data)
  }

  protected fun exportData() {
    val group: VGroupRow = model.getTree()
    if (!printConfig.groupFormfeed) {
      startGroup(null)
      exportHeader()
    }
    minLevel = getMinLevel(group)
    addTree(group)
  }

  /**
   * Gets the minimal visible level
   */
  private fun getMinLevel(row: VReportRow): Int {
    return if (row.visible || !printConfig.visibleRows) {
      var curr = row.level
      for (i in 0 until row.childCount) {
        val childMinLevel = getMinLevel(row.getChildAt(i) as VReportRow)
        if (childMinLevel < curr) {
          curr = childMinLevel
        }
      }
      curr
    } else {
      maxLevel
    }
  }

  private fun addTree(row: VReportRow) {
    val restrictedrow = printConfig.visibleRows
    if (row.visible || !restrictedrow) {
      if (printConfig.groupFormfeed && row.level == maxLevel - 1) {
        val column: VReportColumn = model.getAccessibleColumn(firstVisibleColumn)
        startGroup(column.format(row.getValueAt(table.convertColumnIndexToModel(firstVisibleColumn))))
        exportHeader()
      }
      if (printConfig.order != Constants.SUM_AT_TAIL) {
        // show sum first
        exportRow(row, false)
      }
      for (i in 0 until row.childCount) {
        addTree(row.getChildAt(i) as VReportRow)
      }
      if (printConfig.order == Constants.SUM_AT_TAIL) {
        // show sum at the end
        exportRow(row, true)
      }
    }
  }

  protected fun exportRow(row: VReportRow, tail: Boolean) {
    exportRow(row, tail, false)
  }

  protected fun exportRow(row: VReportRow, tail: Boolean, lineBreak: Boolean) {
    var index = 0
    val newrow = arrayOfNulls<String>(columnCount)
    val newrowOrig = arrayOfNulls<Any>(columnCount)
    val alignments = IntArray(columnCount)
    for (i in 0 until model.getAccessibleColumnCount()) {
      val visibleColumn: Int = table.convertColumnIndexToModel(i)
      val column: VReportColumn = model.getAccessibleColumn(visibleColumn)
      if (column.isFolded() == false && column.isVisible() // if we have a new page for each group, we do not use the first
              // visible column
              && (!printConfig.groupFormfeed || i != firstVisibleColumn)) {
        if (row.level < model.getDisplayLevels(model.getReverseOrder(visibleColumn))) {
          newrow[index] = null
          newrowOrig[index] = null
        } else {
          newrow[index] = if (lineBreak) column.formatWithLineBreaker(row.getValueAt(visibleColumn)) else column.format(row.getValueAt(visibleColumn))
          newrowOrig[index] = row.getValueAt(visibleColumn)
        }
        alignments[index] = column.getAlign()
        index += 1
      }
    }
    if (tail && row.parent != null && (row.parent as VReportRow).firstChild === row && row.childCount == 0) {
      // if the sums are at the end, at the the first row of the group
      // the group information
      var child = row
      var parent = row.parent as VReportRow
      while (parent.firstChild === child) {
        index = 0
        for (i in 0 until model.getAccessibleColumnCount()) {
          val visibleColumn: Int = table.convertColumnIndexToModel(i)
          val column: VReportColumn = model.getAccessibleColumn(visibleColumn)
          if (column.isFolded() == false && column.isVisible() // if we have a new page for each group, we do not use the first
                  // visible column
                  && (!printConfig.groupFormfeed || i != firstVisibleColumn)) {
            if (row.level < model.getDisplayLevels(model.getReverseOrder(visibleColumn)) &&
                    parent.level >= model.getDisplayLevels(model.getReverseOrder(visibleColumn))) {
              newrow[index] = if (lineBreak) column.formatWithLineBreaker(row.getValueAt(visibleColumn)) else column.format(row.getValueAt(visibleColumn))
              newrowOrig[index] = row.getValueAt(visibleColumn)
            }
            index += 1
          }
        }
        child = parent
        parent = parent.parent as VReportRow
      }
    }
    exportRow(row.level - minLevel, newrow, newrowOrig, alignments)
  }

  fun export(file: File) {
    try {
      export(FileOutputStream(file))
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  protected abstract fun startGroup(subTitle: String?)
  protected abstract fun exportRow(level: Int, data: Array<String?>, orig: Array<Any?>, alignment: IntArray?)
  protected abstract fun exportHeader(data: Array<String?>)
  protected abstract fun export(stream: OutputStream)
  protected fun formatStringColumn(column: VReportColumn, index: Int) {}
  protected fun formatDateColumn(column: VReportColumn, index: Int) {}
  protected fun formatMonthColumn(column: VReportColumn, index: Int) {}
  protected fun formatWeekColumn(column: VReportColumn, index: Int) {}
  protected fun formatFixedColumn(column: VReportColumn, index: Int) {}
  protected fun formatIntegerColumn(column: VReportColumn, index: Int) {}
  protected fun formatBooleanColumn(column: VReportColumn, index: Int) {}
  protected fun formatTimeColumn(column: VReportColumn, index: Int) {}
  protected fun formatTimestampColumn(column: VReportColumn, index: Int) {}

  fun getColumnLabel(column: Int): String {
    return model.getAccessibleColumn(table.convertColumnIndexToModel(column)).getLabel()
  }

  fun getBackgroundForLevel(level: Int): Color {
    return parameters.getBackground(level)
  }

  /**
   * checks if  we are in a toner saving mode.
   *
   * @return    true if we are in a toner saving mode ?
   */
  fun tonerSaveMode(): Boolean {
    return tonerSaveMode
  }

  var columnCount = 0
    private set
  private var firstVisibleColumn: Int
  var maxLevel: Int
    private set
  private var minLevel = 0
  private val parameters = Parameters(Color.blue)

  init {
    firstVisibleColumn = -1
    for (j in 0 until model.getAccessibleColumnCount()) {
      val visibleColumn: Int = table.convertColumnIndexToModel(j)
      val column: VReportColumn = model.getAccessibleColumn(visibleColumn)
      if (column.isVisible() && column.isFolded() == false) {
        if (firstVisibleColumn == -1) {
          firstVisibleColumn = j
        }
        columnCount += 1
      }
    }
    if (printConfig.groupFormfeed) {
      // each group an own page:
      // first column is not shown because its the
      // same for all -> it is added to the "title"
      columnCount -= 1
    }
    maxLevel = model.getTree().level
  }
}
