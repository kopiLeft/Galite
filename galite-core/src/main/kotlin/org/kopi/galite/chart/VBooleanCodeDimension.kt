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
 * Represents a boolean code column.
 *
 * @param ident     The column identifier.
 * @param format    The column format.
 * @param type      The column type.
 * @param source    The column localization source.
 * @param idents    The column displayed labels.
 * @param codes     The boolean codes.
 */
class VBooleanCodeDimension(ident: String,
                            format: VColumnFormat,
                            type: String,
                            source: String,
                            idents: Array<String>,
                            private val codes: BooleanArray)
  : VCodeDimension(ident, format, type, source, idents) {

  init {
    if (codes.size > 2) {
      throw InconsistencyException()
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  protected override fun getIndex(value: Any?): Int {
    return if ((value as? Boolean) == codes[0]) 0 else 1
  }
}
