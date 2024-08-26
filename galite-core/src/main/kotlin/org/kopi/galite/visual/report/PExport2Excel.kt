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

package org.kopi.galite.visual.report

import java.awt.Color
import java.io.OutputStream
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.GregorianCalendar

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormat
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.PrintSetup
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.kopi.galite.visual.report.UReport.UTable
import org.kopi.galite.type.Month
import org.kopi.galite.type.Week
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.VlibProperties

abstract class PExport2Excel(table: UTable, model: MReport, printConfig: PConfig, title: String)
  : PExport(table, model, printConfig, title), Constants {

  //-----------------------------------------------------------
  // DATA MEMBERS
  //-----------------------------------------------------------
  private var rowNumber = 0
  protected var workbook: Workbook? = null
    private set
  private var sheet: Sheet? = null
  private var format: DataFormat? = null
  private val datatype = arrayOfNulls<CellType>(columnCount)
  private val dataformats: ShortArray = ShortArray(columnCount)
  private val widths: ShortArray = ShortArray(columnCount)
  private var sheetIndex = 0

  // cell style cache
  private val cellStyleCacheManager: CellStyleCacheManager = CellStyleCacheManager()

  companion object {
    /**
     * Set the value of the cell to the specified date value.
     */
    protected fun setCellValue(cell: Cell, value: LocalDate) {
      val cal = GregorianCalendar()
      cal.clear()
      cal[Calendar.YEAR] = value.year
      cal[Calendar.MONTH] = value.monthValue - 1
      cal[Calendar.DAY_OF_MONTH] = value.dayOfMonth
      cell.setCellValue(cal)
    }
  }

  override fun export(stream: OutputStream) {
    rowNumber = 0
    sheetIndex = 0
    try {
      workbook = createWorkbook()
      format = workbook!!.createDataFormat()
      formatColumns()
      exportData()
      workbook!!.write(stream)
      stream.close()
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      if (workbook is SXSSFWorkbook) {
        (workbook as SXSSFWorkbook).dispose()
      }
    }
  }

  override fun startGroup(subTitle: String?) {
    var subtitle = subTitle

    if (subtitle == null) {
      subtitle = title
    }

    // Sheet name cannot be blank, greater than 31 chars,
    // or contain any of /\*?[]
    subtitle = subtitle.replace("/|\\\\|\\*|\\?|\\[|\\]".toRegex(), "")
    if (subtitle.length > 31) {
      subtitle = subtitle.substring(0, 28) + "..."
    } else if (subtitle.isEmpty()) {
      subtitle = " "
    }
    rowNumber = 0
    sheet = try {
      workbook!!.createSheet(subtitle)
    } catch (e: IllegalArgumentException) {
      workbook!!.createSheet("" + subtitle.hashCode())
    }
    for (i in 0 until columnCount) {
      sheet!!.setColumnWidth(i, widths[i].toInt())
    }

    sheet?.repeatingColumns = CellRangeAddress(-1, -1, 0, columnCount - 1)
    sheet?.repeatingRows = CellRangeAddress(0, 0, -1, -1)

    val footer = sheet!!.footer
    val header = sheet!!.header

    header.left = title + "  " + getColumnLabel(0) + " : " + subtitle

    footer.left = title + " - " + VlibProperties.getString("print-page") + " &P / &N "
    footer.right = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
            " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    sheetIndex += 1
    val ps = sheet!!.printSetup

    sheet!!.autobreaks = true
    ps.fitWidth = 1.toShort()
    ps.fitHeight = 999.toShort()
    ps.landscape = printConfig.paperlayout == "Landscape"
    ps.paperSize = PrintSetup.A4_PAPERSIZE /// !!! no always A4
  }

  override fun exportHeader(data: Array<String?>) {
    val titleRow = sheet!!.createRow(0)
    var cellPos = 0

    data.forEach {
      titleRow.createCell(cellPos++).setCellValue(it)
    }
  }

  override fun exportRow(level: Int, data: Array<String?>, orig: Array<Any?>, alignments: IntArray) {
    val row = sheet!!.createRow(rowNumber + 1)
    val color = getBackgroundForLevel(level)
    var cellPos = 0

    data.forEachIndexed { index, element ->
      val cell: Cell = row.createCell(cellPos)
      val cellStyle: CellStyle = cellStyleCacheManager.getStyle(this,
                                                                workbook!!,
                                                                getAlignment(alignments, index),
                                                                getDataFormat(index),
                                                                color)

      setCellValue(cell, index, element, orig[index])
      cell.cellStyle = cellStyle
      cellPos++
    }
    rowNumber += 1
  }

  protected fun setCellValue(cell: Cell, cellPos: Int, data: String?, orig: Any?) {
    if (data != null && orig != null) {
      if (datatype[cellPos] == CellType.STRING) {
        cell.setCellValue(data.replace('\n', ' '))
      } else {
        if (orig is BigDecimal) {
          cell.setCellValue(orig.toDouble())
        } else if (orig is Int) {
          if (datatype[cellPos] == CellType.BOOLEAN) {
            cell.setCellValue(orig.toDouble() == 1.0)
          } else {
            cell.setCellValue(orig.toDouble())
          }
        } else if (orig is Boolean) {
          cell.setCellValue(orig)
        } else if (orig is LocalDate) {
          setCellValue(cell, orig)
        } else if (orig is Instant || orig is java.sql.Timestamp) {
          // date columns can be returned as a timestamp by the jdbc driver.
          cell.setCellValue(data)
          datatype[cellPos] = CellType.STRING
        } else if (orig is Month) {
          setCellValue(cell, orig.getFirstDay())
        } else if (orig is Week) {
          setCellValue(cell, orig.getFirstDay())
        } else if (orig is String && orig.isBlank()) {
          // maybe reportIdenticalValue Trigger used
          // nothing
        } else {
          throw InconsistencyException("Type not supported: datatype=" + datatype[cellPos]
                                               + "  " + " CellNumber= " + cellPos
                                               + " " + orig.javaClass + " of " + orig)
        }
      }
      cell.cellType = datatype[cellPos]
    } else {
      cell.cellType = CellType.BLANK
    }
  }

  protected fun getAlignment(alignments: IntArray, cellPos: Int): Short {
    return when (alignments[cellPos]) {
      Constants.ALG_DEFAULT, Constants.ALG_LEFT -> HorizontalAlignment.LEFT.code
      Constants.ALG_CENTER -> HorizontalAlignment.CENTER.code
      Constants.ALG_RIGHT -> HorizontalAlignment.RIGHT.code
      else -> throw InconsistencyException("Unknown alignment")
    }
  }

  protected fun getDataFormat(cellPos: Int): Short {
    return if (datatype[cellPos] != CellType.STRING) {
      dataformats[cellPos]
    } else {
      -1
    }
  }

  private fun computeColumnWidth(column: VReportColumn): Int {
    return if (column.label.length < column.width) column.width else column.label.length + 2
  }

  override fun formatStringColumn(column: VReportColumn, index: Int) {
    dataformats[index] = 0
    datatype[index] = CellType.STRING
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  override fun formatWeekColumn(column: VReportColumn, index: Int) {
    dataformats[index] = 0
    datatype[index] = CellType.STRING
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  override fun formatDateColumn(column: VReportColumn, index: Int) {
    dataformats[index] = format!!.getFormat("dd.mm.yyyy")
    datatype[index] = CellType.NUMERIC
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  override fun formatMonthColumn(column: VReportColumn, index: Int) {
    dataformats[index] = format!!.getFormat("mm.yyyy")
    datatype[index] = CellType.NUMERIC
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  override fun formatDecimalColumn(column: VReportColumn, index: Int) {
    var decimalFormat = "#,##0"
    for (i in 0 until (column as VDecimalColumn).maxScale) {
      decimalFormat += if (i == 0) ".0" else "0"
    }
    dataformats[index] = format!!.getFormat(decimalFormat)
    datatype[index] = CellType.NUMERIC
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  override fun formatIntegerColumn(column: VReportColumn, index: Int) {
    dataformats[index] = format!!.getFormat("0")
    datatype[index] = CellType.NUMERIC
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  override fun formatBooleanColumn(column: VReportColumn, index: Int) {
    dataformats[index] = 0 // General type
    datatype[index] = CellType.BOOLEAN
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  override fun formatTimeColumn(column: VReportColumn, index: Int) {
    dataformats[index] = 0
    datatype[index] = CellType.STRING
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  override fun formatTimestampColumn(column: VReportColumn, index: Int) {
    dataformats[index] = 0
    datatype[index] = CellType.STRING
    widths[index] = (256 * computeColumnWidth(column)).toShort()
  }

  protected abstract fun createWorkbook(): Workbook?

  abstract fun createFillForegroundColor(color: Color): org.apache.poi.ss.usermodel.Color?
}
