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

package org.kopi.galite.visual.exceptions

import org.kopi.galite.visual.chart.Measure

import java.lang.IllegalArgumentException

/**
 * Thrown to indicate that a [Measure] value has not been provided to a dimension.
 *
 * @param measure        the measure
 * @param dimensionValue the the dimension value
 *
 */
class MissingMeasureException(measure: Measure<*>, dimensionValue: Comparable<*>) : IllegalArgumentException() {
  override val message = "Missing measure ${measure.label} for the dimension $dimensionValue"
}
