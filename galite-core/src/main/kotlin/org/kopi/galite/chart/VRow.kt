/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import java.io.Serializable

/**
 * The chart model row. This will contain row values including
 * dimension and measure values.
 *
 * @param dimensions The dimension values.
 * @param measures The measures values.
 */
class VRow(private val dimensions: Array<Any?>, private val measures: Array<Any?>) : Serializable {
  /**
   * Returns the dimensions value of the given index.
   * @param index The desired index.
   * @return The measure value of the given index.
   */
  fun getDimensionAt(index: Int): Any? = dimensions[index]

  /**
   * Returns the measure value of the given index.
   * @param index The desired index.
   * @return The measure value of the given index.
   */
  fun getMeasureAt(index: Int): Any? = measures[index]

  /**
   * Sets the dimension value of the given index.
   * @param index the desired index.
   * @param dimension The dimension value to be set.
   */
  fun setDimensionAt(index: Int, dimension: Any) {
    dimensions[index] = dimension
  }

  /**
   * Sets the measure value of the given index.
   * @param index the desired index.
   * @param measure The measure value to be set.
   */
  fun setMeasureAt(index: Int, measure: Any) {
    measures[index] = measure
  }

  /**
   * Returns the measures count.
   * @return The measures count.
   */
  fun getDimensionsCount(): Int {
    return dimensions.size
  }

  /**
   * Returns the measures count.
   * @return The measures count.
   */
  fun getMeasuresCount(): Int {
    return measures.size
  }
}
