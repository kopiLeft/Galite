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

package org.kopi.galite.visual.util

/**
 * Filters characters according to a conversion table
 */
class ToLatin1 : Filter() {

  /**
   * Do some filtering
   *
   * @param char the character to convert
   */
  override fun convert(char: Char): Char {
    return if (char.code >= 128) conversionTable[char.code - 128] else char
  }

  /**
   * Create a new Filter
   */
  init {
    conversionTable = arrayOf(
            199, 252, 233, 226, 228, 224, 229, 231,
            234, 235, 232, 239, 238, 236, 196, 197,
            201, 230, 198, 244, 246, 242, 251, 249,
            255, 214, 220, 162, 163, 165, 0,   0,
            225, 237, 243, 250, 241, 209, 170, 186,
            191, 0,   172, 189, 188, 161, 171, 187,
            0,   0,   0,   0,   0,   0,   0,   0,
            0,   0,   0,   0,   0,   0,   0,   0,
            0,   0,   0,   0,   169, 0,   0,   0,
            0,   0,   0,   0,   0,   0,   0,   0,
            0,   0,   0,   0,   0,   0,   0,   0,
            0,   0,   0,   0,   0,   0,   0,   0,
            0,   223, 0,   0,   0,   0,   181, 0,
            0,   0,   0,   0,   0,   0,   0,   0,
            0,   177, 0,   0,   0,   0,   247, 0,
            176, 0,   183, 0,   0,   178, 0,   160
    ).map { it.toChar() }.toCharArray()
  }
}
