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

import org.kopi.galite.common.Window
import org.kopi.galite.domain.Domain
import org.kopi.galite.visual.VWindow

/**
 * Represents a chart that contains a [dimension] and a list of [measures].
 *
 * @param name the name of the chart. It represents the title
 */
abstract class Chart() : Window() {
  /** The chart's dimension */
  lateinit var dimension: Dimension<*>

  /** The chart's measures */
  val measures = mutableListOf<Measure<*>>()

  /**
   * Creates a chart dimension, with the specified [domain], used to store values of type [T] and measures values.
   *
   * @param domain the dimension domain.
   * @param init   used to initialize the domain with measures values.
   */
  fun <T : Comparable<T>?> dimension(domain: Domain<T>, init: Dimension<T>.() -> Unit): Dimension<T> {
    val chartDimension = Dimension(domain)
    chartDimension.init()
    dimension = chartDimension
    return chartDimension
  }

  /**
   * Creates a chart measure, with the specified [domain], used to store values of measure values.
   *
   * @param domain the dimension domain.
   * @param init   used to initialize the measure.
   */
  fun <T> measure(domain: Domain<T>, init: Measure<T>.() -> Unit): Measure<T> where T : Comparable<T>?, T : Number {
    val chartMeasure = Measure(domain)
    chartMeasure.init()
    this.measures.add(chartMeasure)
    return chartMeasure
  }

  override val model: VWindow by lazy {
    object : VChart() {
      override fun init() {
        TODO("Not yet implemented")
      }

      override fun add() {
        TODO("Not yet implemented")
      }

    }
  }
}
