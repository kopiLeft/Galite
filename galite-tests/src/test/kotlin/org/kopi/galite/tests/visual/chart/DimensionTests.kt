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
import org.kopi.galite.visual.common.Color
import java.time.Month
import java.time.Year
import kotlin.test.assertEquals

class DimensionTests {

  /**
   * Tests that dimension's values have been added and measures have been registered with their
   * corresponding values and labels to a dimension with Month type
   */
  @Test
  fun monthDimentionTest() {
    val monthDimension = Dimension<Month>()
    val measure1 = Measure<Double>()
    measure1.label = "measure 1"
    measure1.color = Color.RED
    val measure2 = Measure<Double>()
    measure2.label = "measure 2"
    measure2.color = Color.BLUE
    val measure3 = Measure<Double>()
    measure3.label = "measure 3"
    measure3.color = Color.YELLOW
    val dimensionValue1 = monthDimension.add(Month.JANUARY)
    val dimensionValue2 = monthDimension.add(Month.DECEMBER)

    dimensionValue1?.addMeasureList(mutableMapOf(measure1 to 55.22, measure2 to 44.22))
    dimensionValue1?.addMeasure(measure3, 10.01)
    dimensionValue2?.addMeasureList(mutableMapOf(measure1 to 22.22, measure2 to 11.22))

    assertEquals(monthDimension.values[0].measureList[measure1], 55.22)
    assertEquals(monthDimension.values[0].measureList[measure2], 44.22)
    assertEquals(monthDimension.values[0].measureList[measure3], 10.01)
    assertEquals(monthDimension.values[1].measureList[measure1], 22.22)
    assertEquals(monthDimension.values[1].measureList[measure2], 11.22)

    assertEquals(monthDimension.values[0].getMeasureLabels(), listOf("measure 1", "measure 2", "measure 3"))
    assertEquals(monthDimension.values[0].getMeasureValues(), listOf(55.22, 44.22, 10.01))
    assertEquals(monthDimension.values[1].getMeasureLabels(), listOf("measure 1", "measure 2"))
    assertEquals(monthDimension.values[1].getMeasureValues(), listOf(22.22, 11.22))
  }

  /**
   * Tests that dimension values have been added and measures have been registered with their
   * corresponding values to a dimension with Int type
   */
  @Test
  fun intDimensionTest() {
    val intDimension = Dimension<Int>()
    val measure1 = Measure<Int>()
    val measure2 = Measure<Int>()

    val dimensionValue1 = intDimension.add((0..10).random())
    dimensionValue1?.addMeasureList(mutableMapOf(measure1 to 50, measure2 to 40))

    assertEquals(intDimension.values[0].measureList[measure1], 50)
    assertEquals(intDimension.values[0].measureList[measure2], 40)
  }
}
