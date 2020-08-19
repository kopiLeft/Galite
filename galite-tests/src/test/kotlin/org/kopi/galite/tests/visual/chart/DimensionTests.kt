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

package org.kopi.galite.tests.visual.chart

import org.junit.Test
import org.kopi.galite.visual.chart.Dimension
import org.kopi.galite.visual.chart.DimensionValue
import org.kopi.galite.visual.chart.Measure
import java.time.Month
import java.time.Year

class DimensionTests {

  /**
   * Tests Dimension's measure list for Months dimensions
   */
  @Test
  fun monthDimentionTest() {
    val monthDimension = Dimension<Month>()
    val dimensionValue1 = DimensionValue<Month>(Month.JANUARY)
    val dimensionValue2 = DimensionValue<Month>(Month.DECEMBER)
    val measure1 = Measure<Double>()
    val measure2 = Measure<Double>()

    monthDimension.add(dimensionValue1)
    monthDimension.add(dimensionValue2)
    dimensionValue1.addMeasureList(mutableMapOf(measure1 to 55.22, measure2 to 44.22))
    dimensionValue1.addMeasureList(mutableMapOf(measure1 to 22.22, measure2 to 11.22))
  }

  /**
   * Tests Dimension's measure list for Int dimensions
   */
  @Test
  fun intDimensionTest() {
    val intDimension = Dimension<Int>()
    val dimensionValue1 = DimensionValue<Int>((0..10).random())
    val measure1 = Measure<Int>()
    val measure2 = Measure<Int>()

    intDimension.add(dimensionValue1)
    dimensionValue1.addMeasureList(mutableMapOf(measure1 to 50, measure2 to 40))
  }
}
