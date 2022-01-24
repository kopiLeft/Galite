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

import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.visual.VColor

/**
 * Represents a decimal measure.
 *
 * @param ident The measure identifier.
 * @param color The measure color.
 * @param maxScale The max scale to be used.
 */
class VDecimalMeasure(ident: String, color: VColor?, private val maxScale: Int) : VMeasure(ident, color) {

  override fun toNumber(value: Any?): Number? {
    if (value == null) {
      return null
    }
    return when (value) {
      is Decimal -> value
      is Number -> Decimal(value.toLong(), maxScale)
      else -> null
    }
  }
}
