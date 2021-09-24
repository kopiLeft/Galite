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

package org.kopi.galite.report

import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

import kotlin.math.max

import com.lowagie.text.Chunk
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.ExceptionConverter
import com.lowagie.text.FontFactory
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.PdfStamper
import com.lowagie.text.pdf.PdfWriter

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.base.Utils
import org.kopi.galite.report.UReport.UTable
import org.kopi.galite.util.PPaperType
import org.kopi.galite.util.PrintJob
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.type.Date
import org.kopi.galite.type.Time

class PExport2PDF(
        table: UTable,
        model: MReport,
        printConfig: PConfig,
        title: String,
        private val firstPageHeader: String,
        tonerSaveMode: Boolean = false,
) : PExport(table, model, printConfig, title, tonerSaveMode), Constants {

  fun export(): PrintJob {
    try {
      val printJob: PrintJob
      val file: File = Utils.getTempFile("galite", "pdf")

      export(file)
      val page: Rectangle = document.pageSize

      printJob = PrintJob(file, true, Rectangle(page.width, page.height))
      printJob.dataType = PrintJob.DAT_PDF
      printJob.title = title
      printJob.numberOfPages = pages
      return printJob
    } catch (e: Exception) {
      throw InconsistencyException(e)
    }
  }

  override fun export(out: OutputStream) {
    try {
      val paper: PPaperType = PPaperType.getPaperTypeFromCode(printConfig.papertype)
      val paperSize: Rectangle

      paperSize = if (printConfig.paperlayout == "Landscape") {
        Rectangle(paper.height.toFloat(), paper.width.toFloat())
      } else {
        Rectangle(paper.width.toFloat(), paper.height.toFloat())
      }
      firstPage = true
      val head = createHeader()
      val firstPageHead = createFirstPageHeader()
      val foot = createFooter(0, 0)

      if (firstPageHeader != null && firstPageHeader != "") {
        firstPageHead.totalWidth = paperSize.width - printConfig.leftmargin - printConfig.rightmargin
      }
      head.totalWidth = paperSize.width - printConfig.leftmargin - printConfig.rightmargin
      document = Document(paperSize,
                          printConfig.leftmargin.toFloat(),
                          printConfig.rightmargin.toFloat(),
                          printConfig.topmargin + head.totalHeight + printConfig.headermargin + firstPageHead.totalHeight,
                          printConfig.bottommargin + foot.totalHeight + printConfig.footermargin + 2)
      // 2 to be sure to print the border
      if (printConfig.reportScale == PConfig.MIN_REPORT_SCALE) {
        scale = getScale(PConfig.MIN_REPORT_SCALE, PConfig.MAX_REPORT_SCALE, 0.1)
      } else {
        scale = printConfig.reportScale
        formatColumns()
      }
      val tempFile = Utils.getTempFile("kopiexport", "pdf")
      val writer = PdfWriter.getInstance(document, FileOutputStream(tempFile))

      writer.pageEvent = object : PdfPageEventHelper() {
        override fun onEndPage(writer: PdfWriter, document: Document) {
          try {
            val page: Rectangle = document.pageSize
            val head: PdfPTable = createHeader()

            head.totalWidth = (page.width - document.leftMargin() - document.rightMargin())
            head.writeSelectedRows(0,
                                   -1,
                                   document.leftMargin(),
                                   (page.height - document.topMargin()) + head.totalHeight + printConfig.headermargin,
                                   writer.directContent)
          } catch (e: Exception) {
            throw ExceptionConverter(e)
          }
        }
      }
      document.open()
      if (firstPageHeader != null && firstPageHeader != "") {
        try {
          val page = document.pageSize

          firstPageHead.writeSelectedRows(0,
                                          -1,
                                          document.leftMargin(),
                                          page.height - document.topMargin() + head.totalHeight + printConfig.headermargin + firstPageHead.totalHeight,
                                          writer.directContent)
          document.setMargins(document.leftMargin(),
                              document.rightMargin(),
                              document.topMargin() - firstPageHead.totalHeight,
                              document.bottomMargin())
        } catch (e: Exception) {
          throw ExceptionConverter(e)
        }
      }
      exportData()
      document.add(datatable)
      document.close()
      addFooter(tempFile, out)
    } catch (e: Exception) {
      throw InconsistencyException(e)
    }
  }

  private fun addFooter(tempFile: File, out: OutputStream) {
    // write footer;
    try {
      val reader = PdfReader(FileInputStream(tempFile))
      val stamper = PdfStamper(reader, out)
      val page = document.pageSize

      pages = reader.numberOfPages
      for (i in 1..pages) {
        val foot = createFooter(i, pages)
        val cb = stamper.getOverContent(i)

        foot.totalWidth = page.width - document.leftMargin() - document.rightMargin()
        foot.writeSelectedRows(0, -1, document.leftMargin(), printConfig.bottommargin + foot.totalHeight, cb)
      }
      stamper.close()
    } catch (e: Exception) {
      throw InconsistencyException(e)
    }
  }

  private fun createFirstPageHeader(): PdfPTable {
    val head = PdfPTable(1)

    head.addCell(createCell(firstPageHeader, 14.0,
                            Color.black,
                            Color.white,
                            Constants.ALG_LEFT,
                            false))
    return head
  }

  private fun createHeader(): PdfPTable {
    val head = PdfPTable(1)
    val text = if ((currentSubtitle == null)) {
      title
    } else {
      title + "  " + getColumnLabel(0) + " : " + currentSubtitle
    }

    head.addCell(createCell(text,
                            14.0,
                            Color.black,
                            Color.white,
                            Constants.ALG_LEFT,
                            false))
    currentSubtitle = nextPageSubtitle
    return head
  }

  private fun createFooter(page: Int, allPages: Int): PdfPTable {
    val foot = PdfPTable(2)

    foot.addCell(createCell(title + " - " + VlibProperties.getString("print-page") + " " + page + "/" + allPages,
                            7.0,
                            Color.black,
                            Color.white,
                            Constants.ALG_LEFT, false))
    foot.addCell(createCell(Date.now().format("dd.MM.yyyy") + " " + Time.now().format("HH:mm"), 7.0,
                            Color.black,
                            Color.white,
                            Constants.ALG_RIGHT,
                            false))
    return foot
  }

  override fun startGroup(subTitle: String?) {
    try {
      if (datatable != null) {
        document.add(datatable)
      }
      currentSubtitle = nextPageSubtitle
      nextPageSubtitle = subTitle
      datatable = PdfPTable(columnCount)
      datatable!!.widthPercentage = 100f
      val defaultCell = datatable!!.defaultCell

      defaultCell.borderWidth = BORDER_WIDTH.toFloat()
      defaultCell.horizontalAlignment = Element.ALIGN_LEFT
      if (!firstPage) {
        document.newPage()
      } else {
        for (i in widths.indices) {
          widths[i] += datatable!!.defaultCell.paddingLeft - BORDER_WIDTH + datatable!!.defaultCell.paddingRight - BORDER_WIDTH
        }
      }
      datatable!!.setWidths(widths)
      firstPage = false
    } catch (e: Exception) {
      throw InconsistencyException(e)
    }
  }

  override fun exportHeader(data: Array<String?>) {
    data.forEach {
      datatable!!.addCell(createCell(it!!,
                                     scale,
                                     Color.white,
                                     Color.black,
                                     Constants.ALG_CENTER,
                                     true))
    }
    datatable!!.headerRows = 1
  }

  override fun exportRow(row: VReportRow, tail: Boolean) {
    exportRow(row, tail, true)
  }

  override fun exportRow(level: Int, data: Array<String?>, orig: Array<Any?>, alignments: IntArray) {
    var cell = 0

    datatable!!.defaultCell.borderWidth = BORDER_WIDTH.toFloat()
    datatable!!.defaultCell.backgroundColor = Color.white
    data.forEachIndexed { index, element ->
      if (element != null) {
        datatable!!.addCell(createCell(element,
                                       scale,
                                       Color.black,
                                       getBackgroundForLevel(level),
                                       alignments[index],
                                       true))
      } else {
        datatable!!.addCell(createCell(" ",
                                       scale,
                                       Color.black,
                                       getBackgroundForLevel(level),
                                       alignments[index],
                                       true))
      }
      cell += 1
    }
  }

  private fun createCell(text: String,
                         size: Double,
                         textColor: Color,
                         background: Color,
                         alignment: Int,
                         border: Boolean): PdfPCell {
    val cell: PdfPCell
    val font = FontFactory.getFont(FontFactory.HELVETICA, size.toFloat(),
                                   0,
                                   if (tonerSaveMode()) Color.black else textColor)

    cell = PdfPCell(Paragraph(Chunk(text, font)))
    cell.borderWidth = 1f
    cell.paddingLeft = BORDER_PADDING.toFloat()
    cell.paddingRight = BORDER_PADDING.toFloat()
    cell.isNoWrap = true
    cell.isUseDescender = true
    cell.verticalAlignment = Element.ALIGN_TOP
    when (alignment) {
      Constants.ALG_DEFAULT, Constants.ALG_LEFT -> cell.horizontalAlignment = Element.ALIGN_LEFT
      Constants.ALG_CENTER -> cell.horizontalAlignment = Element.ALIGN_CENTER
      Constants.ALG_RIGHT -> cell.horizontalAlignment = Element.ALIGN_RIGHT
      else -> throw InconsistencyException("Unknown alignment")
    }
    cell.backgroundColor = if (tonerSaveMode()) Color.white else background
    if (!border) {
      cell.border = 0
    }
    return cell
  }

  /**
   * Gets the scale to be used for this report
   */
  private fun getScale(min: Double, max: Double, precision: Double): Double {
    val width: Int
    // setting format parameters
    val paper: PPaperType = PPaperType.getPaperTypeFromCode(printConfig.papertype)

    width = if (printConfig.paperlayout == "Landscape") {
      paper.height
    } else {
      paper.width
    }

    widthSum = 0.0
    scale = max
    formatColumns()
    val widthSumMax: Double = widthSum + (columnCount * 2 * BORDER_PADDING) + (columnCount * 1)

    if ((widthSumMax <= (width - document.leftMargin() - document.rightMargin() - (2 * printConfig.border)))) {
      return max
    }
    if (max - min <= precision) {
      return min
    }
    widthSum = 0.0
    scale = min
    formatColumns()
    val widthSumMin: Double = widthSum + (columnCount * 2 * BORDER_PADDING) + (columnCount * 1)

    widthSum = 0.0
    scale = (min + (max - min) / 2)
    formatColumns()
    widthSum += (columnCount * 2 * BORDER_PADDING) + (columnCount * 1)
    return if ((widthSumMin <= (width - document.leftMargin() - document.rightMargin() - (2 * printConfig.border))
                    && widthSum >= (width - document.leftMargin() - document.rightMargin() - (2 * printConfig.border)))) {
      getScale(min, min + (max - min) / 2, precision)
    } else {
      getScale(max - (max - min) / 2, max, precision)
    }
  }

  override fun formatStringColumn(column: VReportColumn, index: Int) {
    // maximum of length of title AND width of column
    widths[index] = max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                        Chunk("X", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint * column.width)
    widthSum += widths[index]
  }

  override fun formatWeekColumn(column: VReportColumn, index: Int) {
    widths[index] = max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                        Chunk("00.0000", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint)
    widthSum += widths[index]
  }

  override fun formatDateColumn(column: VReportColumn, index: Int) {
    widths[index] = max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                        Chunk("00.00.0000", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint)
    widthSum += widths[index]
  }

  override fun formatMonthColumn(column: VReportColumn, index: Int) {
    widths[index] = 4 + max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                            Chunk("00.0000", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint)
    widthSum += widths[index]
  }

  override fun formatDecimalColumn(column: VReportColumn, index: Int) {
    widths[index] = max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                        Chunk("0", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint * column.width)
    widthSum += widths[index]
  }

  override fun formatIntegerColumn(column: VReportColumn, index: Int) {
    widths[index] = max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                        Chunk("0", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint * column.width)
    widthSum += widths[index]
  }

  override fun formatBooleanColumn(column: VReportColumn, index: Int) {
    widths[index] = max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                        Chunk("false", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint)
    widthSum += widths[index]
  }

  override fun formatTimeColumn(column: VReportColumn, index: Int) {
    widths[index] = max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                        Chunk("00:00", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint)
    widthSum += widths[index]
  }

  override fun formatTimestampColumn(column: VReportColumn, index: Int) {
    widths[index] = max(Chunk(column.label, FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint,
                        Chunk("00.00.0000 00:00.0000", FontFactory.getFont(FontFactory.HELVETICA, scale.toFloat())).widthPoint)
    widthSum += widths[index]
  }

  private var datatable: PdfPTable? = null
  private var pages = 0
  private lateinit var document: Document
  private var currentSubtitle: String? = null
  private var nextPageSubtitle: String? = null
  private var firstPage = false
  private var scale = 0.0
  private var widthSum = 0.0
  private val widths: FloatArray = FloatArray(columnCount)

  companion object {
    private const val BORDER_PADDING = 1
    private const val BORDER_WIDTH = 1
  }
}
