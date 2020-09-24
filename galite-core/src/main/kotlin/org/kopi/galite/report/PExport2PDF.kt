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

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfPTable
import org.kopi.galite.util.PrintJob
import java.io.OutputStream

class PExport2PDF(table: UReport.UTable, model: MReport, printOptions: PConfig, pageTitle: String, firstPageHeader: String, b: Boolean) : PExport(table,
        model, printOptions, pageTitle), Constants {


  fun PExport2PDF(table: UReport.UTable?, model: MReport?, pconfig: PConfig?,
                  title: String?, firstPageHeader: String, tonerSaveMode: Boolean) {
    TODO()
  }

  fun export(): PrintJob = TODO()


  private val datatable: PdfPTable? = null
  private val pages = 0
  private val document: Document? = null
  private val currentSubtitle: String? = null
  private val nextPageSubtitle: String? = null
  private var firstPageHeader: String? = null
  private val firstPage = false

  private val scale = 0.0
  private val widthSum = 0.0
  private lateinit var widths: FloatArray

  private val BORDER_PADDING = 1
  private val BORDER_WIDTH = 1
  override fun exportHeader(data: Array<String?>) {
    TODO("Not yet implemented")
  }

  override fun exportRow(level: Int, data: Array<String?>, orig: Array<Any?>, alignment: IntArray?) {
    TODO("Not yet implemented")
  }

  override fun export(stream: OutputStream) {
    TODO("Not yet implemented")
  }

  override fun startGroup(subTitle: String?) {
    TODO("Not yet implemented")
  }
}
