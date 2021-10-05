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

package org.kopi.galite.tests.chart

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.common.TestBase
import org.kopi.galite.visual.chart.VRow

class VRowTests : TestBase() {
  /**
   * Tests data in measures and dimensions
   */
  @Test
  fun dataTest() {
    assertEquals(2, vRow.getDimensionsCount())
    assertEquals(4, vRow.getMeasuresCount())
    assertEquals("Tunis", vRow.getDimensionAt(0))
    assertEquals("Bizerte", vRow.getDimensionAt(1))
    assertEquals(20, vRow.getMeasureAt(3))
  }

  private val vRow = VRow(arrayOf("Tunis", "Bizerte"), arrayOf(1, 6, 9, 20))
}
