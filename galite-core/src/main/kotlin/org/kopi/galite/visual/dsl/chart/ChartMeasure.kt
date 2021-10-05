/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.dsl.chart

import java.lang.RuntimeException

import org.kopi.galite.visual.chart.CConstants
import org.kopi.galite.visual.chart.VFixnumMeasure
import org.kopi.galite.visual.chart.VIntegerMeasure
import org.kopi.galite.visual.chart.VMeasure
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.ChartTrigger
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.visual.Color
import org.kopi.galite.visual.visual.VColor

/**
 * Represents a measure used to store numeric values in chart.
 *
 * @param domain dimension domain.
 */
open class ChartMeasure<T>(domain: Domain<T>) : ChartField<T>(domain) where T : Comparable<T>?, T : Number? {

  /**Measure's color in chart */
  lateinit var color: Color

  /** Color trigger */
  internal var colorTrigger: Trigger? = null

  /**
   * Called to specify a measure color. The trigger should return a [VColor] instance.
   *
   * @param method    The method to execute when compute trigger is executed.
   */
  fun color(method: () -> VColor): ChartTrigger {
    val fieldAction = Action(null, method)
    return ChartTrigger(0L or (1L shl CConstants.TRG_COLOR), fieldAction).also {
      colorTrigger = it
    }
  }

  // TODO add Decimal types
  val model: VMeasure
    get() {
      val color: VColor? = if (colorTrigger != null) {
        colorTrigger!!.action.method() as VColor
      } else {
        null
      }

      return when (domain.kClass) {
        Int::class, Long::class -> VIntegerMeasure(ident, color)
        Decimal::class -> VFixnumMeasure(ident, color, domain.height!!)
        else -> throw RuntimeException("Type ${domain.kClass!!.qualifiedName} is not supported")
      }.also {
        if (ident != "") {
          it.label = label
          it.help = help
        }
      }
    }
}
