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

import java.math.BigDecimal

/**
 * Represents a decimal measure.
 *
 * @param ident The measure identifier.
 * @param maxScale The max scale to be used.
 */
class VDecimalMeasure(ident: String, private val maxScale: Int) : VMeasure(ident) {

  override fun toNumber(value: Any?): Number? {
    if (value == null) {
      return null
    }
    return when (value) {
      is BigDecimal -> value
      is Number -> BigDecimal.valueOf(value.toLong(), maxScale)
      else -> null
    }
  }
}
