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

/**
 * Represents the value of a dimension
 */
class DimensionData<T : Comparable<T>?>(val value: T) {

  /**
   * Measures with corresponding values in a dimension value
   */
  var measureList = mutableMapOf<ChartMeasure<*>, Number?>()

  /**
   * Add a measure with it's value to a dimension value
   *
   * @param measure the measure to add
   * @param measureValue the corresponding value
   */
  operator fun <V> set(measure: ChartMeasure<V>, measureValue: V) where V : Comparable<V>?, V : Number? {
    measureList.putIfAbsent(measure, measureValue)
  }

  /**
   * Get list of measures label's
   * @return list of labels
   */
  fun getMeasureLabels(): List<String> {
    val labelsList = mutableListOf<String>()
    measureList.map {
      labelsList.add(it.key.label!!)
    }
    return labelsList
  }

  /**
   * Get list of measure values
   * @return list of values
   */
  fun getMeasureValues(): List<Number?> = measureList.map {
    it.value
  }
}
