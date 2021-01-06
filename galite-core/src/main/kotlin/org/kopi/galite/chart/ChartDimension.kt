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

import org.kopi.galite.common.Action
import org.kopi.galite.common.ChartTrigger
import org.kopi.galite.common.Trigger
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.domain.ListDomain
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



  /** format trigger */
  internal var formatTrigger: Trigger? = null

  /**
   * Called for formatting a dimension value. The trigger should return a [VColumnFormat] instance.
   *
   * @param method    The method to execute when compute trigger is executed.
   */
  fun format(method: () -> VColumnFormat): ChartTrigger {
    val fieldAction = Action(null, method)
    return ChartTrigger(0L or (1L shl CConstants.TRG_FORMAT), fieldAction).also {
      formatTrigger = it
    }
  }

  /**
   * Add a dimension value
   *
   * @param value the dimension value
   */
  fun add(value: T, init: DimensionData<T>.() -> Unit) {
    val dimensionValue = DimensionData<T>(value)
    dimensionValue.init()
    values.add(dimensionValue)
  }

  // TODO add Fixed types
  val model: VDimension
    get() {
      val format: VColumnFormat? = if (formatTrigger != null) {
        formatTrigger!!.action.method() as VColumnFormat
      } else {
        null
      }

      return when {
        domain.type == null -> {
          when (domain.kClass) {
            Int::class ->
              VIntegerDimension(ident, format)
            String::class ->
              VStringDimension(ident, format)
            Boolean::class ->
              VBooleanDimension(ident, format)
            Date::class, java.util.Date::class ->
              VDateDimension(ident, format)
            Month::class ->
              VMonthDimension(ident, format)
            Week::class ->
              VWeekDimension(ident, format)
            Time::class ->
              VTimeDimension(ident, format)
            Timestamp::class ->
              VTimestampDimension(ident, format)
            else -> throw RuntimeException("Type ${domain.kClass!!.qualifiedName} is not supported")
          }
        }
        domain.type is CodeDomain -> {
          TODO()
        }
        domain is ListDomain -> {
          TODO()
        }
        else -> {
          TODO()
        }
      }
    }
}
