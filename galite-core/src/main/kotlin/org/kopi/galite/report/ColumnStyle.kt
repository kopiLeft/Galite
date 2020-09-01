/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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

import org.kopi.galite.exceptions.InconsistencyException
import java.awt.Color
import java.awt.Font

class ColumnStyle {

  var foreground = 0
  var background = 0
  var fontName = 0
  var fontStyle = 0
  val state = 0

  /**
   * Gets the background color of this component.
   *
   * @return        color	The color to become this component's background color.
   */
  fun getBackground(): Color? {
    when (background) {
      Constants.CLR_WHITE -> return Color.white
      Constants.CLR_BLACK -> return Color.black
      Constants.CLR_RED -> return Color.red
      Constants.CLR_GREEN -> return Color.green
      Constants.CLR_BLUE -> return Color.blue
      Constants.CLR_YELLOW -> return Color.yellow
      Constants.CLR_PINK -> return Color.pink
      Constants.CLR_CYAN -> return Color.cyan
      Constants.CLR_GRAY -> return Color.gray
    }
    throw InconsistencyException()
  }

  /**
   * Gets the foreground color of this component.
   *
   * @return        color	The color to become this component's foreground color.
   */
  fun getForeground(): Color? {
    when (foreground) {
      Constants.CLR_WHITE -> return Color.white
      Constants.CLR_BLACK -> return Color.black
      Constants.CLR_RED -> return Color.red
      Constants.CLR_GREEN -> return Color.green
      Constants.CLR_BLUE -> return Color.blue
      Constants.CLR_YELLOW -> return Color.yellow
      Constants.CLR_PINK -> return Color.pink
      Constants.CLR_CYAN -> return Color.cyan
      Constants.CLR_GRAY -> return Color.gray
    }
    throw InconsistencyException()
  }

  /**
   * Gets the font of this component.
   *
   * @return        font	The font to become this component's font.
   */
  fun getFont(): Font? {
    val font: String
    font = when (fontName) {
      0 -> org.kopi.galite.ui.common.Constants.FNT_FIXED_WIDTH
      1 -> "Helvetica"
      2 -> "Geneva"
      3 -> "Courier"
      else -> throw InconsistencyException()
    }
    return Font(font, fontStyle, 12)
  }
}
