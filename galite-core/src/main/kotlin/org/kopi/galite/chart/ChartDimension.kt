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
import org.kopi.galite.type.Date
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week
import java.lang.RuntimeException

/**
 * Represents a one dimension that contains measures [values] to use in chart.
 *
 * @param domain dimension domain.
 */
open class ChartDimension<T : Comparable<T>?>(domain: Domain<T>) : ChartField<T>(domain) {

  /**
   * Dimension values
   */
  val values = mutableListOf<DimensionData<T>>()

  /**
   * Add a dimension value
   *
   * @param value the dimension value
   */
  fun add(value: T, init: DimensionData<T>.() -> Unit) {
    val dimensionValue = DimensionData<T>(value)
    if (init != null) {
      dimensionValue.init()
    }
    values.add(dimensionValue)
  }

  // TODO add Fixed types
  val model: VDimension
    get() = when (domain.kClass) {
      Int::class ->
        VIntegerDimension(ident, null)
      String::class ->
        VStringDimension(ident, null)
      Boolean::class ->
        VBooleanDimension(ident, null)
      Date::class, java.util.Date::class ->
        VDateDimension(ident, null)
      Month::class ->
        VMonthDimension(ident, null)
      Week::class ->
        VWeekDimension(ident, null)
      Time::class ->
        VTimeDimension(ident, null)
      Timestamp::class ->
        VTimestampDimension(ident, null)
      else -> throw RuntimeException("Type ${domain.kClass!!.qualifiedName} is not supported")
    }
}
