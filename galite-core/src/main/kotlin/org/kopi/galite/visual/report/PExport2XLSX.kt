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

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.kopi.galite.visual.report.UReport.UTable

class PExport2XLSX(table: UTable,
                   model: MReport,
                   printConfig: PConfig,
                   title: String)
  : PExport2Excel(table, model, printConfig, title), Constants {

  override fun createWorkbook(): Workbook {
    return SXSSFWorkbook(XSSFWorkbook(), 10000, false)
  }

  override fun createFillForegroundColor(color: Color): org.apache.poi.ss.usermodel.Color {
    return XSSFColor(color, SXSSFWorkbook(XSSFWorkbook(), 10000, false).xssfWorkbook.stylesSource.indexedColors)
  }
}
