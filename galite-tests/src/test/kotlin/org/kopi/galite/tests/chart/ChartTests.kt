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

import org.kopi.galite.tests.common.TestBase
import org.kopi.galite.visual.chart.Formatter
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.exceptions.MissingMeasureException

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChartTests : TestBase() {

  /**
   * Tests creating dimension and one measure and fill them with data
   */
  @Test
  fun dimensionOneMeasureTest() {
    withChart {
      val dimension = dimension(StringTestType(10)) {
      }

      val measure1 = measure(IntTestType(10)) {
      }

      dimension.add("abc") {
        this[measure1] = 20
      }

      assertEquals(1, dimension.values.size)
      assertEquals("abc", dimension.values.first().value)
      assertEquals(20, dimension.values.first().measureList[measure1])
    }
  }

  /**
   * Tests creating dimension and multiple measures and fill them with data
   * Defines measures before the dimension
   */
  @Test
  fun dimensionMultipleMeasureTest() {
    withChart {
      val measure1 = measure(IntTestType(10)) {
      }

      val measure2 = measure(IntTestType(10)) {
      }

      val dimension = dimension(StringTestType(10)) {
      }

      dimension.add("abc") {
        this[measure1] = 20
        this[measure2] = 90
      }

      dimension.add("def") {
        this[measure1] = 30
        this[measure2] = 100
      }

      dimension.add("ghi") {
        this[measure1] = 40
        this[measure2] = 110
      }

      assertEquals(3, dimension.values.size)
      assertEquals("abc", dimension.values[0].value)
      assertEquals("def", dimension.values[1].value)
      assertEquals("ghi", dimension.values[2].value)
      assertEquals(20, dimension.values[0].measureList[measure1])
      assertEquals(90, dimension.values[0].measureList[measure2])
      assertEquals(30, dimension.values[1].measureList[measure1])
      assertEquals(100, dimension.values[1].measureList[measure2])
      assertEquals(40, dimension.values[2].measureList[measure1])
      assertEquals(110, dimension.values[2].measureList[measure2])
    }
  }

  /**
   * Tests chart data formatter
   */
  @Test
  fun chartDataEncodeFormatterTest1() {
    withChart {
      val measure1 = measure(IntTestType(10)) {
        label = "measure1"
      }

      val measure2 = measure(IntTestType(10)) {
        label = "measure2"
      }

      val dimension = dimension(StringTestType(10)) {
        label = "dimension"
      }

      dimension.add("Tunis") {
        this[measure1] = 12
        this[measure2] = 20
      }

      dimension.add("Bizerte") {
        this[measure2] = 33
        this[measure1] = 45
      }

      val formattedData = Formatter.encode(this)

      assertEquals("[[\"dimension\",\"measure1\",\"measure2\"],[\"Tunis\",12,20],[\"Bizerte\",45,33]]",
              formattedData)
    }
  }

  /**
   * Tests chart data formatter that fails when there is a measure not set.
   */
  @Test
  fun chartDataEncodeFormatterTest2() {
    withChart {
      val measure1 = measure(IntTestType(10)) {
        label = "measure1"
      }

      val measure2 = measure(IntTestType(10)) {
        label = "measure2"
      }

      val dimension = dimension(StringTestType(10)) {
        label = "dimension"
      }

      dimension.add("Tunis") {
        this[measure1] = 12
        this[measure2] = 20
      }

      dimension.add("Bizerte") {
        this[measure1] = 45
      }

      assertFailsWith<MissingMeasureException> {
        Formatter.encode(this)
      }
    }
  }
}

class StringTestType(val param: Int) : CodeDomain<String>() {
  init {
    "cde1" keyOf "1"
  }
}

class IntTestType(val param: Int) : CodeDomain<Int>() {
  init {
    "cde1" keyOf 1
  }
}
