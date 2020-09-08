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

package org.kopi.galite.tests.chart

import org.junit.Test
import org.kopi.galite.chart.Dimension
import org.kopi.galite.chart.Measure
import org.kopi.galite.visual.Color
import java.time.Month
import kotlin.test.assertEquals

class DimensionTests {

  /**
   * Tests that dimension's values have been added and measures have been registered with their
   * corresponding values and labels to a dimension with Month type
   */
  @Test
  fun monthDimensionTest() {
    val monthDimension = Dimension<Month>()
    val measure1 = Measure<Double>()
    measure1.label = "measure 1"
    measure1.color = Color.RED
    monthDimension.add(Month.JANUARY) {
      this[measure1] = 10.01
    }
    monthDimension.add(Month.DECEMBER) {
      this[measure1] = 22.22
    }

    assertEquals(monthDimension.values[1].measureList[measure1], 22.22)

    assertEquals(monthDimension.values[0].getMeasureLabels(), listOf("measure 1"))
    assertEquals(monthDimension.values[0].getMeasureValues(), listOf(10.01))
    assertEquals(monthDimension.values[1].getMeasureLabels(), listOf("measure 1"))
    assertEquals(monthDimension.values[1].getMeasureValues(), listOf(22.22))
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

    intDimension.add((0..10).random()) {
      this[measure1] = 50
      this[measure2] = 40
    }

    assertEquals(intDimension.values[0].measureList[measure1], 50)
    assertEquals(intDimension.values[0].measureList[measure2], 40)
  }
}
