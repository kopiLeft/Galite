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
import java.io.Serializable

import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor

/**
 * Manage the CellStyle cache.
 * In order to not have the 4000 style limit, cell styles are cached base on their
 * hash codes within a [Set] and reused when it two or more cells have the same
 * style.
 * Use [.setCellStyle] for caching functions.
 */
class CellStyleCacheManager : Serializable {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val stylesCache: MutableMap<StyleKey, CellStyle> = HashMap()

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Returns the cell style corresponding to the given alignment, data format and color.
   * If the style is not cached yet, it will be created and added to the cache.
   * @param exporter The excel exporter instance.
   * @param wb The workbook instance.
   * @param alignment The style alignment.
   * @param dataFormat The style data format.
   * @param color The style background color.
   * @return The style instance.
   */
  fun getStyle(
    exporter: PExport2Excel,
    wb: Workbook,
    alignment: Short,
    dataFormat: Short,
    color: Color,
  ): CellStyle {
    val key = StyleKey(alignment, dataFormat, color)
    if (!stylesCache.containsKey(key)) {
      val style: CellStyle = wb.createCellStyle()

      style.verticalAlignment = VerticalAlignment.TOP
      style.borderBottom = BorderStyle.THIN
      style.bottomBorderColor = IndexedColors.BLACK.getIndex()
      style.borderLeft = BorderStyle.THIN
      style.leftBorderColor = IndexedColors.BLACK.getIndex()
      style.borderRight = BorderStyle.THIN
      style.rightBorderColor = IndexedColors.BLACK.getIndex()
      style.borderTop = BorderStyle.THIN
      style.topBorderColor = IndexedColors.BLACK.getIndex()
      style.fillPattern = FillPatternType.SOLID_FOREGROUND
      style.wrapText = true
      style.alignment = HorizontalAlignment.forInt(alignment.toInt())
      if (dataFormat.toInt() != -1) {
        style.dataFormat = dataFormat
      }
      if (style is XSSFCellStyle) {
        style.setFillForegroundColor(exporter.createFillForegroundColor(color) as XSSFColor)
      } else {
        style.fillForegroundColor = (exporter.createFillForegroundColor(color) as HSSFColor).index
      }
      stylesCache[key] = style
    }
    return stylesCache[key]!!
  }

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  inner class StyleKey(private val alignment: Short, private val dataFormat: Short, private val color: Color) {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun equals(other: Any?): Boolean =
            if (other is StyleKey) {
              alignment == other.alignment && dataFormat == other.dataFormat && color == other.color
            } else {
              super.equals(other)
            }

    override fun hashCode(): Int = alignment + dataFormat + color.hashCode()
  }
}
