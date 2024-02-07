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

package org.kopi.galite.tests.chart

import java.math.BigDecimal

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.MONTH
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.chart.ChartDimension
import org.kopi.galite.visual.dsl.chart.ChartMeasure
import org.kopi.galite.type.Month

class DimensionTests: VApplicationTestBase() {
  val chart = object : Chart("") {}

  /**
   * Tests that dimension's values have been added and measures have been registered with their
   * corresponding values and labels to a dimension with Month type
   */
  @Test
  fun monthDimensionTest() {
    val monthDimension = ChartDimension(MONTH, chart=chart)
    val measure1 = ChartMeasure(DECIMAL(20, 10))
    measure1.label = "measure 1"
    monthDimension.add(Month(2021, 1)) {
      this[measure1] = BigDecimal("10.01")
    }
    monthDimension.add(Month(2021, 12)) {
      this[measure1] = BigDecimal("22.22")
    }

    assertEquals(BigDecimal("22.22"), monthDimension.values[1].measureList[measure1])

    assertEquals(monthDimension.values[0].getMeasureLabels(), listOf("measure 1"))
    assertEquals(monthDimension.values[0].getMeasureValues(), listOf(BigDecimal("10.01")))
    assertEquals(monthDimension.values[1].getMeasureLabels(), listOf("measure 1"))
    assertEquals(monthDimension.values[1].getMeasureValues(), listOf(BigDecimal("22.22")))
  }

  /**
   * Tests that dimension values have been added and measures have been registered with their
   * corresponding values to a dimension with Int type
   */
  @Test
  fun intDimensionTest() {
    val intDimension = ChartDimension(INT(10), chart=chart)
    val measure1 = ChartMeasure(INT(10))
    val measure2 = ChartMeasure(INT(10))

    intDimension.add((0..10).random()) {
      this[measure1] = 50
      this[measure2] = 40
    }

    assertEquals(intDimension.values[0].measureList[measure1], 50)
    assertEquals(intDimension.values[0].measureList[measure2], 40)
  }
}
