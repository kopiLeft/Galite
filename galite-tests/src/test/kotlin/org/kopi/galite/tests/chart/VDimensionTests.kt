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

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.chart.CConstants
import org.kopi.galite.visual.chart.VColumnFormat
import org.kopi.galite.visual.chart.VDimension
import org.kopi.galite.visual.chart.VStringDimension
import org.kopi.galite.visual.chart.VTimeDimension
import org.kopi.galite.visual.chart.VTimestampDimension
import org.kopi.galite.visual.chart.VWeekDimension
import org.kopi.galite.type.Week

class VDimensionTests : VApplicationTestBase() {

  @Test
  fun personalizedVDimension() {
    class TestDimension(ident: String, format: VColumnFormat?) : VDimension(ident, format) {
      override fun toString(value: Any?): String {
        return value.toString()
      }
    }

    val dimensionWithoutFormat = TestDimension("DimensionWithoutFormat",null)

    assertEquals("null", dimensionWithoutFormat.format(null))
    assertEquals("5", dimensionWithoutFormat.format(5))
    assertEquals("String", dimensionWithoutFormat.format("String"))
    assertEquals("1.618", dimensionWithoutFormat.format(1.618))

    val dimensionWithFormat = TestDimension("DimensionWithFormat", VColumnFormat())

    assertEquals(CConstants.EMPTY_TEXT, dimensionWithFormat.format(null))
    assertEquals("5", dimensionWithFormat.format(5))
    assertEquals("String", dimensionWithFormat.format("String"))
    assertEquals("1.618", dimensionWithFormat.format(1.618))
  }

  @Test
  fun vWeekDimensionTest() {
    val vWeekDimension = VWeekDimension("VWeekDimension", null)
    val week = Week(2022, 3)

    assertEquals(CConstants.EMPTY_TEXT, vWeekDimension.format(null))
    assertEquals(week.toString(), vWeekDimension.format(week))
  }

  @Test
  fun vTimestampDimensionTest() {
    val vTimestampDimension = VTimestampDimension("VTimestampDimension", null)

    assertEquals(CConstants.EMPTY_TEXT, vTimestampDimension.format(null))

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    assertEquals("2021-01-01 00:00:00.000000",
                 vTimestampDimension.format(LocalDateTime.parse("2021-01-01 00:00:00", dateTimeFormatter)))
  }

  @Test
  fun vTimeDimensionTest() {
    val vTimeDimension = VTimeDimension("VTimeDimension", null)

    assertEquals(CConstants.EMPTY_TEXT, vTimeDimension.format(null))
    assertEquals("23:30", vTimeDimension.format(LocalTime.of(23, 30, 0)))
  }

  @Test
  fun vStringDimensionTest() {
    val vStringDimension = VStringDimension("VStringDimension", null)

    assertEquals(CConstants.EMPTY_TEXT, vStringDimension.format(null))
    assertEquals("String", vStringDimension.format("String"))
    assertEquals("5", vStringDimension.format(5))
  }
}
