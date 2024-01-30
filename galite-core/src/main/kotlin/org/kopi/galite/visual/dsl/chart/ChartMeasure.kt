/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.visual.chart.VMeasure
import org.kopi.galite.visual.domain.Domain

/**
 * Represents a measure used to store numeric values in chart.
 *
 * @param domain dimension domain.
 */
open class ChartMeasure<T>(domain: Domain<T>, override val source: String = "") : ChartField<T>(domain) where T : Comparable<T>?, T : Number? {

  val model: VMeasure
    get() {

      return domain.buildMeasureModel(this).also {
        if (ident != "") {
          it.label = label
          it.help = help
        }
      }
    }
}
