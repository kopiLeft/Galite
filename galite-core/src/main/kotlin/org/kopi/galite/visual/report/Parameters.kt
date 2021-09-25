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
import java.awt.Font
import java.io.Serializable

import org.kopi.galite.visual.visual.Constants

/**
 * Parameters class .
 *
 * @param color        a color defined in Constants
 */
class Parameters(color: Color) : Serializable {
  /**Returns the size of the font*/
  val font = Font(Constants.FNT_FIXED_WIDTH, Font.PLAIN, 12)
  private val bgcolors: Array<Color?> = arrayOfNulls(10)
  private val fgcolors = Array(10) { Color(0, 0, 0) }

  init {
    val reverseColor = Color(255 - color.red, 255 - color.green, 255 - color.blue)
    for (i in bgcolors.indices) {
      bgcolors[i] = Color(255 - i * reverseColor.red / 17,
                          255 - i * reverseColor.green / 17,
                          255 - i * reverseColor.blue / 17)
    }
  }

  /**
   * Returns the background color which corresponds to the level
   */
  fun getBackground(level: Int): Color {
    return if (level >= bgcolors.size) Color.white else bgcolors[level]!!
  }

  /**
   * Returns the foreground color which corresponds to the level
   */
  fun getForeground(level: Int): Color {
    return if (level >= fgcolors.size) Color.black else fgcolors[level]
  }
}
