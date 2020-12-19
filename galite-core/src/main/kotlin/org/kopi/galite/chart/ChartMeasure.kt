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

import java.lang.RuntimeException

import org.kopi.galite.domain.Domain
import org.kopi.galite.visual.Color

/**
 * Represents a measure used to store numeric values in chart.
 *
 * @param domain dimension domain.
 */
open class ChartMeasure<T>(domain: Domain<T>) : ChartField<T>(domain) where T : Comparable<T>?, T : Number? {

  /**Measure's color in chart */
  lateinit var color: Color

  // TODO add Fixed types
  val model: VMeasure
    get() = when (domain.kClass) {
      Int::class ->
        VIntegerMeasure(ident, null)
      else -> throw RuntimeException("Type ${domain.kClass!!.qualifiedName} is not supported")
    }
}
