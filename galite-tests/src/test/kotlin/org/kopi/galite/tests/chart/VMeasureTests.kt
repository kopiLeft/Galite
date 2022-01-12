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

import java.util.Locale

import org.junit.Test
import org.kopi.galite.visual.chart.VChartType
import org.kopi.galite.visual.chart.VDecimalCodeMeasure
import org.kopi.galite.visual.chart.VDecimalMeasure
import org.kopi.galite.visual.chart.VIntegerCodeMeasure
import org.kopi.galite.visual.chart.VIntegerMeasure
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.type.Decimal

class VMeasureTests {
  val chart = TestChart()

  @Test
  fun vDecimalCodeMeasure() {
    val vDecimalCodeMeasure = chart.decimalCodeMeasure.model as VDecimalCodeMeasure

    assertEquals(chart.decimalCodeMeasure.label, vDecimalCodeMeasure.label)
  }

  @Test
  fun vDecimalMeasure() {
    val vDecimalMeasure = chart.decimalMeasure.model as VDecimalMeasure

    assertEquals(chart.decimalMeasure.label, vDecimalMeasure.label)
  }

  @Test
  fun vIntegerCodeMeasure() {
    val vIntegerCodeMeasure = chart.integerCodeMeasure.model as VIntegerCodeMeasure

    assertEquals(chart.integerCodeMeasure.label, vIntegerCodeMeasure.label)
  }

  @Test
  fun vIntegerMeasure() {
    val vIntegerMeasure = chart.integerMeasure.model as VIntegerMeasure

    assertEquals(chart.integerMeasure.label, vIntegerMeasure.label)
  }
}

object IntegerCode : CodeDomain<Int>() {
  init {
    "OK" keyOf 1
    "NO" keyOf 0
  }
}

object DecimalCode : CodeDomain<Decimal>() {
  init {
    "OK" keyOf Decimal(1.0)
    "NO" keyOf Decimal(0.0)
  }
}

class TestChart : Chart(
  locale = Locale.UK,
  title = "Test Chart for measures",
  help = "This chart presents a test chart"
) {

  val decimalMeasure = measure(DECIMAL(width = 10, scale = 5)) {
    label = "Decimal measure"
  }

  val integerMeasure = measure(INT(10)) {
    label = "Integer measure"
  }

  val decimalCodeMeasure = measure(DecimalCode) {
    label = "Decimal code measure"
  }

  val integerCodeMeasure = measure(IntegerCode) {
    label = "Integer code measure"
  }

  val integerDimension = dimension(STRING(10)) {
    label = "Integer dimension"
  }

  val decimalDimension = dimension(STRING(10)) {
    label = "Decimal dimension"
  }

  val type = trigger(CHARTTYPE) {
    VChartType.BAR
  }
}
