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

import org.kopi.galite.type.NotNullFixed

/**
 * Represents a fixed chart column.
 *
 * @param ident       The column identifier.
 * @param format      The dimension format ?
 * @param maxScale    The max scale to be used for the fixed value.
 * @param exactScale  Should we use the max scale column for fixed values having a minor scale ?
 */
class VFixnumDimension(ident: String,
                       format: VColumnFormat,
                       private val maxScale: Int,
                       private val exactScale: Boolean)
  : VDimension(ident, format) {
  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  override fun toString(value: Any?): String {
    return if (value == null) {
      CConstants.EMPTY_TEXT
    } else (value as? Int)?.toString()
            ?: if (value is NotNullFixed) {
              if (value.scale > maxScale || exactScale) value.setScale(maxScale).toString()
              else value.toString()
            } else {
              value.toString()
            }
  }
}
