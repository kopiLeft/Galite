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
import java.awt.Font
import java.io.Serializable

import org.kopi.galite.visual.util.base.InconsistencyException

class ColumnStyle : Serializable {

  var foregroundCode = 0
  var backgroundCode = 0
  var fontName = 0
  var fontStyle = 0
  val state = 0

  /**
   * Gets the background color of this component.
   *
   * @return        color	The color to become this component's background color.
   */
  fun getBackground(): Color = when (backgroundCode) {
    Constants.CLR_WHITE -> Color.white
    Constants.CLR_BLACK -> Color.black
    Constants.CLR_RED -> Color.red
    Constants.CLR_GREEN -> Color.green
    Constants.CLR_BLUE -> Color.blue
    Constants.CLR_YELLOW -> Color.yellow
    Constants.CLR_PINK -> Color.pink
    Constants.CLR_CYAN -> Color.cyan
    Constants.CLR_GRAY -> Color.gray
    else ->
      throw InconsistencyException(
              message = "The background's color doesn't exist within the specified colors scope"
      )
  }

  /**
   * Gets the foreground color of this component.
   *
   * @return        color	The color to become this component's foreground color.
   */
  fun getForeground(): Color = when (foregroundCode) {
    Constants.CLR_WHITE -> Color.white
    Constants.CLR_BLACK -> Color.black
    Constants.CLR_RED -> Color.red
    Constants.CLR_GREEN -> Color.green
    Constants.CLR_BLUE -> Color.blue
    Constants.CLR_YELLOW -> Color.yellow
    Constants.CLR_PINK -> Color.pink
    Constants.CLR_CYAN -> Color.cyan
    Constants.CLR_GRAY -> Color.gray
    else -> throw InconsistencyException(
            message = "The foreground's color doesn't exist within the specified colors scope"
    )
  }

  /**
   * Gets the font of this component.
   *
   * @return        font	The font to become this component's font.
   */
  fun getFont(): Font {
    val font = when (fontName) {
      0 -> org.kopi.galite.visual.visual.Constants.FNT_FIXED_WIDTH
      1 -> "Helvetica"
      2 -> "Geneva"
      3 -> "Courier"
      else -> throw InconsistencyException(
              message = "Font doesn't exist within the specified scope"
      )
    }
    return Font(font, fontStyle, 12)
  }
}
