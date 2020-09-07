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

package org.kopi.galite.chart

import org.kopi.galite.util.base.InconsistencyException

/**
 * Paper Type
 *
 * @param width The width of the paper in points
 * @param height The height of the paper in points
 * @param name The name of the paper type
 * @param code The code of the paper type, must match with the printOption code
 */
class PPaperType(val width: Int,
                 val height: Int,
                 val name: String,
                 val code: Int) {
  /**
   * Returns a String representation of the object
   */
  override fun toString(): String {
    return name
  }

  companion object {
    /**
     * Conversions
     */
    fun getPaperTypeFromCode(code: Int): PPaperType = when (code) {
      1 -> PPT_LETTER
      2 -> PPT_TABLOID
      3 -> PPT_LEDGER
      4 -> PPT_LEGAL
      5 -> PPT_STATEMENT
      6 -> PPT_EXECUTIVE
      7 -> PPT_A3
      8 -> PPT_A4
      9 -> PPT_A5
      10 -> PPT_B4
      11 -> PPT_B5
      12 -> PPT_FOLIO
      13 -> PPT_QUARTO
      14 -> PPT_10X14
      else ->
        throw InconsistencyException("Undefined paper")
    }

    // --------------------------------------------------------------------
    // PREDEFINED PAPER TYPES
    // --------------------------------------------------------------------
    val PPT_LETTER =    PPaperType(612, 792, "Letter", 1)
    val PPT_TABLOID =   PPaperType(792, 1224, "Tabloid", 2)
    val PPT_LEDGER =    PPaperType(1224, 792, "Ledger", 3)
    val PPT_LEGAL =     PPaperType(612, 1008, "Legal", 4)
    val PPT_STATEMENT = PPaperType(396, 612, "Statement", 5)
    val PPT_EXECUTIVE = PPaperType(540, 720, "Executive", 6)
    val PPT_A3 =        PPaperType(842, 1190, "A3", 7)
    val PPT_A4 =        PPaperType(595, 842, "A4", 8)
    val PPT_A5 =        PPaperType(420, 595, "A5", 9)
    val PPT_B4 =        PPaperType(729, 1032, "B4", 10)
    val PPT_B5 =        PPaperType(516, 729, "B5", 11)
    val PPT_FOLIO =     PPaperType(612, 936, "Folio", 12)
    val PPT_QUARTO =    PPaperType(610, 780, "Quarto", 13)
    val PPT_10X14 =     PPaperType(720, 1008, "10x14", 14)
  }
}
