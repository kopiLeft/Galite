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

import org.kopi.galite.visual.dsl.exceptions.MissingMeasureException

/**
 * Chart data formatter utility.
 */
object Formatter {
  /**
   * Converts the value passed to a [chart] dimension to the string format known by visual chart.
   *
   * @param chart the chart
   */
  fun encode(chart: Chart) = buildString {
    append("[[")
    append(chart.dimension.label.quoteIfNecessary())
    chart.measures.forEach {
      append(",")
      append(it.label.quoteIfNecessary())
    }
    append("]")
    chart.dimension.values.forEach { dimensionValue ->
      append(",")
      append("[")
      append(dimensionValue.value.quoteIfNecessary())
      chart.measures.forEach { measure ->
        append(",")
        append(dimensionValue.measureList.getOrElse(
                measure,
                { throw MissingMeasureException(measure, dimensionValue.value) }
        ))
      }
      append("]")
    }
    append("]")
  }

  /**
   * Adds quote to receiver if it is not quoted.
   */
  private fun <T> T.quoteIfNecessary(): String {
    val stringRepresentation = this.toString()

    return if (!stringRepresentation.startsWith("\"") && !stringRepresentation.endsWith("\""))
      "\"" + stringRepresentation + "\""
    else
      stringRepresentation
  }
}
