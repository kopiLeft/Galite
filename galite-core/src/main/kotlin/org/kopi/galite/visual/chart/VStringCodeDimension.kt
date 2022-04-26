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

import org.kopi.galite.util.base.InconsistencyException

/**
 * Represents a string code chart column.
 *
 * @param ident The column identifier.
 * @param format The dimension format.
 * @param type the column type.
 * @param source The column localization source.
 * @param idents The columns labels.
 * @param codes The column codes.
 */
class VStringCodeDimension(ident: String,
                           format: VColumnFormat?,
                           type: String,
                           source: String,
                           idents: Array<String>,
                           private val codes: Array<String?>)
  : VCodeDimension(ident, format, type, source, idents) {
  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  override fun getIndex(value: Any?): Int {
    codes.forEachIndexed { index, code ->
      if (value == code) {
        return index
      }
    }
    throw InconsistencyException("Object not found $value, $ident")
  }
}
