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
package org.kopi.galite.ui.vaadin.report

import java.awt.Color

import org.kopi.galite.report.ColumnStyle
import org.kopi.galite.report.MReport
import org.kopi.galite.report.Parameters
import org.kopi.galite.report.VSeparatorColumn

/**
 * The `ReportCellStyler` is the dynamic report styler
 *
 * @param model The report model.
 * @param parameters The style parameters.
 */
class ReportCellStyler(private val model: MReport, private val parameters: Parameters, val table: DTable) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Updates the cell styles.
   *
   * @param i           The row index of the cell.
   * @param j           The column index of the cell.
   */
  fun updateStyles(i: Int, j: Int) {
    val column = model.getAccessibleColumn(j)

    if (column is VSeparatorColumn) {
      return
    }
    val style = column!!.getStyles()[0]

    updateStyle(style, model.getRow(i)!!.level)
  }

  /**
   * Updates the CSS style of a given [ColumnStyle].
   *
   * @param columnStyle The [ColumnStyle].
   * @param level The column level.
   *
   */
  private fun updateStyle(columnStyle: ColumnStyle, level: Int) {
    val background = if (columnStyle.getBackground() != columnStyle.getBackground()) {
      columnStyle.getBackground()
    } else {
      parameters.getBackground(level)
    }
    val foreground = if (columnStyle.getForeground() != columnStyle.getForeground()) {
      columnStyle.getForeground()
    } else {
      parameters.getForeground(level)
    }
    val fontSize = if (columnStyle.getFont().size != columnStyle.getFont().size) {
      columnStyle.getFont().size
    } else {
      parameters.font.size
    }
    val fontFamily = if (columnStyle.getFont().name != columnStyle.getFont().name) {
      columnStyle.getFont().name
    } else {
      parameters.font.name
    }
    val isBold = if (columnStyle.getFont().isBold != columnStyle.getFont().isBold) {
      columnStyle.getFont().isBold
    } else {
      parameters.font.isBold
    }
    val isItalic = if (columnStyle.getFont().isItalic != columnStyle.getFont().isItalic) {
      columnStyle.getFont().isItalic
    } else {
      parameters.font.isItalic
    }

    setStyle(level, background, foreground,fontSize, fontFamily, fontWeight(isBold), fontStyle(isItalic))
  }

  /**
   * Returns the encapsulated CSS style.
   */
  private fun setStyle(
    level: Int,
    background: Color,
    foreground: Color,
    fontSize: Int,
    fontFamily: String,
    fontWeight: String,
    fontStyle: String,
  ) {
    table.style["--level-$level-background"] = getCSSColor(background)
    table.style["--level-$level-color"] = getCSSColor(foreground)
    table.style["--level-$level-font-size"] = fontSize.toString()
    table.style["--level-$level-font-family"] = fontFamily
    table.style["--level-$level-font-weight"] = fontWeight
    table.style["--level-$level-font-style"] = fontStyle
  }

  /**
   * Returns the corresponding CSS color of a given [Color].
   * @param color The AWT color.
   * @return The CSS color.
   */
  private fun getCSSColor(color: Color): String {
    return "rgb(" + color.red + "," + color.green + "," + color.blue + ")"
  }

  /**
   * Returns the bold style.
   * @param isBold The bold style.
   */
  private fun fontWeight(isBold: Boolean): String = if(isBold) "bold" else "normal"

  /**
   * Returns the italic style.
   * @param isItalic The italic style.
   */
  private fun fontStyle(isItalic: Boolean) : String = if (isItalic) "italic" else "normal"
}
