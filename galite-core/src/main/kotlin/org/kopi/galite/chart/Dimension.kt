/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.domain.Domain

/**
 * Represents a one dimension that contains measures [values] to use in chart.
 *
 * @param domain dimension domain.
 */
open class Dimension<T : Comparable<T>?>(domain: Domain<T>? = null) : Column() {

  /**
   * Dimension values
   */
  val values = mutableListOf<DimensionData<T>>()

  /**
   * Add a dimension value
   *
   * @param value the dimension value
   */
  fun add(value: T, init: (DimensionData<T>.() -> Unit)? = null) {
    val dimensionValue: DimensionData<T> = DimensionData<T>(value)
    if (init != null) {
      dimensionValue.init()
    }
    values.add(dimensionValue)
  }
}
