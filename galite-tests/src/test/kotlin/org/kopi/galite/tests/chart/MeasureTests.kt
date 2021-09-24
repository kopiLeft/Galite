/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.chart.ChartMeasure
import org.kopi.galite.domain.INT
import org.kopi.galite.visual.Color
import kotlin.test.assertEquals

class MeasureTests {

  /**
   * Test measure class
   */
  @Test
  fun testMeasure() {
    val measure1 = ChartMeasure(INT(10))
    measure1.label = "measure 1"
    measure1.color = Color.RED
    assertEquals(measure1.color.toString(), "RED")
  }
}
