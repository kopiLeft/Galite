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

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.visual.chart.*
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.type.Week

class VDimensionTests {

  @Test
  fun personalizedVDimension() {
    class TestDimension(ident: String, format: VColumnFormat?) : VDimension(ident, format) {
      override fun toString(value: Any?): String {
        return value.toString()
      }
    }

    val dimensionWithoutFormat = TestDimension("DimensionWithoutFormat",null)

    assertEquals(dimensionWithoutFormat.format(null), "null")
    assertEquals(dimensionWithoutFormat.format(5), "5")
    assertEquals(dimensionWithoutFormat.format("String"), "String")
    assertEquals(dimensionWithoutFormat.format(1.618), "1.618")

    val dimensionWithFormat = TestDimension("DimensionWithFormat", VColumnFormat())

    assertEquals(dimensionWithFormat.format(null), CConstants.EMPTY_TEXT)
    assertEquals(dimensionWithFormat.format(5), "5")
    assertEquals(dimensionWithFormat.format("String"), "String")
    assertEquals(dimensionWithFormat.format(1.618), "1.618")
  }

  @Test
  fun vWeekDimensionTest() {
    val vWeekDimension = VWeekDimension("VWeekDimension", null)

    assertEquals(vWeekDimension.format(null), CConstants.EMPTY_TEXT)
    assertEquals(vWeekDimension.format(Week(70)), "18.1")
  }

  @Test
  fun vTimestampDimensionTest() {
    val vTimestampDimension = VTimestampDimension("VTimestampDimension", null)

    assertEquals(vTimestampDimension.format(null), CConstants.EMPTY_TEXT)
    assertEquals(vTimestampDimension.format(Timestamp("2021-01-01 00:00:00")), "2021-01-01 00:00:00.000000")
  }

  @Test
  fun vTimeDimensionTest() {
    val vTimeDimension = VTimeDimension("VTimeDimension", null)

    assertEquals(vTimeDimension.format(null), CConstants.EMPTY_TEXT)
    assertEquals(vTimeDimension.format(Time(23, 30, 0)), "23:30")
  }

  @Test
  fun vStringDimensionTest() {
    val vStringDimension = VStringDimension("VStringDimension", null)

    assertEquals(vStringDimension.format(null), CConstants.EMPTY_TEXT)
    assertEquals(vStringDimension.format("String"), "String")
    assertEquals(vStringDimension.format(5), "5")
  }
}
