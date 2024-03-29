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
package org.kopi.galite.tests.dsl

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.chart.VChartType
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.chart.Chart

class ChartDSLTests : VApplicationTestBase() {

  @Test
  fun `test generated model from a basic chart`() {
    val chart = BasicChart()
    val model = chart.model

    assertEquals(chart.locale, model.locale)
    assertEquals(chart.title, model.getTitle())
    assertEquals(chart.help, model.help)
  }

  @Test
  fun `test chart measure`() {
    val chart = BasicChart()
    val model = chart.model
    val areaMeasure = model.getMeasure(0)
    val populationMeasure = model.getMeasure(1)

    assertEquals(chart.area.label, areaMeasure.label)
    assertEquals(chart.area.help, areaMeasure.help)
    assertEquals(chart.area.ident, areaMeasure.ident)

    assertEquals(chart.population.label, populationMeasure.label)
    assertEquals(chart.population.help, populationMeasure.help)
    assertEquals(chart.population.ident, populationMeasure.ident)
  }

  @Test
  fun `test chart dimension`() {
    val chart = BasicChart()
    val model = chart.model
    val dimension = model.getDimension(0)

    assertEquals(chart.city.label, dimension.label)
    assertEquals(chart.city.help, dimension.help)
    assertEquals(chart.city.ident, dimension.ident)
    assertEquals(2, chart.city.values.size)
  }

  @Test
  fun `test chart dimension data`() {
    val chart = BasicChart()

    assertEquals(listOf("population"), chart.city.values[0].getMeasureLabels())
    assertEquals(listOf("population"), chart.city.values[1].getMeasureLabels())
    assertEquals(listOf(638845), chart.city.values[0].getMeasureValues())
    assertEquals(listOf(271428), chart.city.values[1].getMeasureValues())
  }

  @Test
  fun `test chart contains trigger CHARTTYPE`() {
    val chart = BasicChart()
    val model = chart.model

    assertEquals(true, model.hasFixedType())
  }

  @Test
  fun `test chart type`() {
    val chart = BasicChart()
    val model = chart.model

    assertEquals(VChartType.BAR, model.getFixedType())
  }
}

class BasicChart : Chart(
  title = "Area/population per city",
  help = "This chart presents the area/population per city",
  locale = Locale.UK
) {

  val area = measure(DECIMAL(width = 10, scale = 5)) {
    label = "area (ha)"
  }

  val population = measure(INT(10)) {
    label = "population"
  }

  val city = dimension(STRING(10)) {
    label = "dimension"
  }

  val type = trigger(CHARTTYPE) {
    VChartType.BAR
  }

  init{
    city.add("Tunis") {
      this[population] = 638845
    }
    city.add("Sousse") {
      this[population] = 271428
    }
  }
}
