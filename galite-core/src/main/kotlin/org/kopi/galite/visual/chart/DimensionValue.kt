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

package org.kopi.galite.visual.chart

import org.kopi.galite.visual.field.Field

class DimensionValue<T : Comparable<T>>(val value: T) : Field<T>() {

  /**
   * Measure list for Dimension
   */
  var measureList = mutableMapOf<Measure<*>, Number>()

  /**
   * Add a measure to dimension
   * @param measure the measure to add
   */
  fun <V> addMeasure(measure: Measure<*>, measureValue: V) where V : Comparable<V>, V : Number {
    measureList.put(measure, measureValue)
  }

  /**
   * Add list of measures to dimension
   * @param measures List of measures
   * @return true if list has been added
   */
  fun <V> addMeasureList(measures: MutableMap<Measure<V>, V>) where V : Comparable<V>, V : Number {
    measureList.putAll(measures)
  }

  /**
   * Get list of measures label's
   * @return list of labels
   */
  fun getMeasureLabels(): List<String> {
    var labelsList = mutableListOf("")
    measureList.map {
      labelsList.add(it.key.label)
    }
    return labelsList
  }

  /**
   * Get list of measure values
   * @return list of values
   */
  fun getMeasureValues(): List<Number> {
    var valuesList = mutableListOf<Number>()
    measureList.map {
      valuesList.add(it.value)
    }
    return valuesList
  }
}
