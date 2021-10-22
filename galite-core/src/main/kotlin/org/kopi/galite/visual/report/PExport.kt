/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.report

import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.Serializable

import org.kopi.galite.visual.report.UReport.UTable

abstract class PExport(val table: UTable,
                       val model: MReport,
                       val printConfig: PConfig,
                       var title: String,
                       val tonerSaveMode: Boolean = false) : Serializable {

  var columnCount = 0
    private set
  private var firstVisibleColumn: Int = -1
  var maxLevel: Int
    private set
  private var minLevel = 0
  private val parameters = Parameters(Color.blue)

  init {
    for (j in 0 until model.getAccessibleColumnCount()) {
      val visibleColumn: Int = table.convertColumnIndexToModel(j)
      val column: VReportColumn? = model.getAccessibleColumn(visibleColumn)

      if (column!!.isVisible && !column.isFolded) {
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
    maxLevel = model.getTree()!!.level
  }

  fun formatColumns() {
    var index = 0

    for (j in 0 until model.getAccessibleColumnCount()) {
      val visibleColumn: Int = table.convertColumnIndexToModel(j)
      val column: VReportColumn? = model.getAccessibleColumn(visibleColumn)

      if (column!!.isVisible && !column.isFolded // if we have a new page for each group, we do not use the first visible column
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
      val column: VReportColumn? = model.getAccessibleColumn(visibleColumn)

      if (column!!.isVisible && !column.isFolded // if we have a new page for each group, we do not use the first visible column
              && (!printConfig.groupFormfeed || j != firstVisibleColumn)) {
        data[index] = column.label
        index += 1
      }
    }
    exportHeader(data)
  }

  protected fun exportData() {
    val group: VGroupRow? = model.getTree()

    if (!printConfig.groupFormfeed) {
      startGroup(null)
      exportHeader()
    }
    minLevel = getMinLevel(group!!)
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
    val restrictedRow = printConfig.visibleRows

    if (row.visible || !restrictedRow) {
      if (printConfig.groupFormfeed && row.level == maxLevel - 1) {
        val column: VReportColumn? = model.getAccessibleColumn(firstVisibleColumn)

        startGroup(column!!.format(row.getValueAt(table.convertColumnIndexToModel(firstVisibleColumn))))
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

  protected open fun exportRow(row: VReportRow, tail: Boolean) {
    exportRow(row, tail, false)
  }

  protected fun exportRow(row: VReportRow, tail: Boolean, lineBreak: Boolean) {
    var index = 0
    val newRow = arrayOfNulls<String>(columnCount)
    val newRowOrig = arrayOfNulls<Any>(columnCount)
    val alignments = IntArray(columnCount)

    for (i in 0 until model.getAccessibleColumnCount()) {
      val visibleColumn: Int = table.convertColumnIndexToModel(i)
      val column: VReportColumn? = model.getAccessibleColumn(visibleColumn)

      if (!column!!.isFolded && column.isVisible // if we have a new page for each group, we do not use the first visible column
              && (!printConfig.groupFormfeed || i != firstVisibleColumn)) {
        if (row.level < model.getDisplayLevels(model.getReverseOrder(visibleColumn))) {
          newRow[index] = null
          newRowOrig[index] = null
        } else {
          newRow[index] = if (lineBreak) {
            column.formatWithLineBreaker(row.getValueAt(visibleColumn))
          } else {
            column.format(row.getValueAt(visibleColumn))
          }
          newRowOrig[index] = row.getValueAt(visibleColumn)
        }
        alignments[index] = column.align
        index += 1
      }
    }
    if (tail && row.parent != null && (row.parent as VReportRow).firstChild === row && row.childCount == 0) {
      // if the sums are at the end, at the the first row of the group
      // the group information
      var child = row
      var parent = row.parent as? VReportRow

      while (parent != null && parent.firstChild === child) {
        index = 0
        for (i in 0 until model.getAccessibleColumnCount()) {
          val visibleColumn: Int = table.convertColumnIndexToModel(i)
          val column: VReportColumn? = model.getAccessibleColumn(visibleColumn)

          if (!column!!.isFolded && column.isVisible // if we have a new page for each group, we do not use the first visible column
                  && (!printConfig.groupFormfeed || i != firstVisibleColumn)) {
            if (row.level < model.getDisplayLevels(model.getReverseOrder(visibleColumn)) &&
                    parent.level >= model.getDisplayLevels(model.getReverseOrder(visibleColumn))) {
              newRow[index] = if (lineBreak) {
                column.formatWithLineBreaker(row.getValueAt(visibleColumn))
              } else {
                column.format(row.getValueAt(visibleColumn))
              }
              newRowOrig[index] = row.getValueAt(visibleColumn)
            }
            index += 1
          }
        }
        child = parent
        parent = parent.parent as? VReportRow
      }
    }
    exportRow(row.level - minLevel, newRow, newRowOrig, alignments)
  }

  fun export(file: File) {
    try {
      export(FileOutputStream(file))
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  protected abstract fun startGroup(subTitle: String?)

  protected abstract fun exportRow(level: Int, data: Array<String?>, orig: Array<Any?>, alignments: IntArray)

  protected abstract fun exportHeader(data: Array<String?>)

  protected abstract fun export(stream: OutputStream)

  internal open fun formatStringColumn(column: VReportColumn, index: Int) {}

  internal open fun formatDateColumn(column: VReportColumn, index: Int) {}

  internal open fun formatMonthColumn(column: VReportColumn, index: Int) {}

  internal open fun formatWeekColumn(column: VReportColumn, index: Int) {}

  internal open fun formatDecimalColumn(column: VReportColumn, index: Int) {}

  internal open fun formatIntegerColumn(column: VReportColumn, index: Int) {}

  internal open fun formatBooleanColumn(column: VReportColumn, index: Int) {}

  internal open fun formatTimeColumn(column: VReportColumn, index: Int) {}

  internal open fun formatTimestampColumn(column: VReportColumn, index: Int) {}

  fun getColumnLabel(column: Int): String = model.getAccessibleColumn(table.convertColumnIndexToModel(column))!!.label

  fun getBackgroundForLevel(level: Int): Color = parameters.getBackground(level)

  /**
   * checks if  we are in a toner saving mode.
   *
   * @return    true if we are in a toner saving mode ?
   */
  fun tonerSaveMode(): Boolean = tonerSaveMode
}
