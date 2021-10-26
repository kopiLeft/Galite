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

import org.kopi.galite.visual.chart.CConstants
import org.kopi.galite.visual.chart.VColumnFormat
import org.kopi.galite.visual.chart.VDimension
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.ChartTrigger
import org.kopi.galite.visual.dsl.common.Trigger

/**
 * Represents a one dimension that contains measures [values] to use in chart.
 *
 * @param domain dimension domain.
 */
open class ChartDimension<T : Comparable<T>?>(domain: Domain<T>, val source: String? = null) : ChartField<T>(domain) {

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

  // TODO add Decimal types
  val model: VDimension
    get() {
      val format: VColumnFormat? = if (formatTrigger != null) {
        formatTrigger!!.action.method() as VColumnFormat
      } else {
        null
      }

      return domain.buildDimensionModel(this, format).also {
        if (ident != "") {
          it.label = label
          it.help = help
        }
      }
    }
}
