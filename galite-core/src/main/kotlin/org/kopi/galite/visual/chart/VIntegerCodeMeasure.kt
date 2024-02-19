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

package org.kopi.galite.visual.chart

/**
 * Represents an Integer code measure.
 *
 * @param ident The measure identifier.
 * @param type The localization type.
 * @param source The localization source.
 * @param idents The codes identifiers.
 * @param codes The integer codes.
 */
class VIntegerCodeMeasure(ident: String,
                          type: String,
                          source: String,
                          idents: Array<String>,
                          private val codes: Array<Int?>)
  : VCodeMeasure(ident, type, source, idents) {
  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  override fun getIndex(value: Any?): Int {
    codes.forEachIndexed { index, code ->
      if ((value as? Int) == code) {
        return index
      }
    }
    return -1
  }

  override fun toNumber(value: Any?): Number? {
    codes.forEach { code ->
      if ((value as? Int) == code) {
        return code
      }
    }
    return null
  }
}
