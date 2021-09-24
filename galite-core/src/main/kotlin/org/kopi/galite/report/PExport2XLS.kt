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
import java.util.Hashtable

import org.apache.poi.hssf.usermodel.HSSFPalette
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Workbook
import org.kopi.galite.report.UReport.UTable

class PExport2XLS(table: UTable,
                  model: MReport,
                  printConfig: PConfig,
                  title: String)
  : PExport2Excel(table,
                  model,
                  printConfig,
                  title), Constants {

  override fun createWorkbook(): Workbook {
    val wb = HSSFWorkbook()

    colorindex = 10
    colorpalete = Hashtable()
    palette = wb.customPalette
    return wb
  }

  override fun createFillForegroundColor(color: Color): org.apache.poi.ss.usermodel.Color? {
    var rowCol = colorpalete!![color]

    return rowCol ?: run {
      palette!!.setColorAtIndex(colorindex, color.red.toByte(), color.green.toByte(), color.blue.toByte())
      rowCol = palette!!.getColor(colorindex)
      colorindex++
      colorpalete!![color] = rowCol
      return rowCol
    }
  }

  //-----------------------------------------------------------
  // DATA MEMBERS
  //-----------------------------------------------------------
  private var palette: HSSFPalette? = null
  private var colorindex: Short = 0
  private var colorpalete: Hashtable<Color, HSSFColor?>? = null
}
