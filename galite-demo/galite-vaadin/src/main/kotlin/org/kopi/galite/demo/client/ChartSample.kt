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
package org.kopi.galite.demo.client

import java.math.BigDecimal

import java.util.Locale

import org.kopi.galite.visual.chart.VChartType
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key

class ChartSample : Chart(
  locale = Locale.UK,
  title = "Area/population per city",
  help = "This chart presents the area/population per city"
) {

  val action = menu("Action")
  val file = menu("File")

  val quit = actor(menu = file, label = "Quit", help = "Close Chart.", ident = "quit") {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val helpForm = actor(menu = action, label = "Help", help = " Help", ident = "help") {
    key = Key.F1
    icon = Icon.HELP
  }

  val bar = actor(menu = action, label = "Bar", help = " Bar", ident = "Bar view") {
    key = Key.F6
    icon = Icon.BAR_CHART
  }

  val area_chart = actor(menu = action, label = "Area", help = " Area", ident = "Area view") {
    key = Key.F8
    icon = Icon.AREA_CHART
  }

  val line = actor(menu = action, label = "Line", help = " line", ident = "Line view") {
    key = Key.F7
    icon = Icon.LINE_CHART
  }

  val cmdQuit = command(item = quit) {
    model.close()
  }

  val helpCmd = command(item = helpForm) {
    model.showHelp()
  }

  val barCmd = command(item = bar) {
    model.setType(VChartType.BAR)
  }

  val areaCmd = command(item = area_chart) {
    model.setType(VChartType.AREA)
  }

  val lineCmd = command(item = line) {
    model.setType(VChartType.LINE)
  }

  val area = measure(DECIMAL(width = 10, scale = 5)) {
    label = "area (ha)"
  }

  val population = measure(INT(10)) {
    label = "population"
  }

  val city = dimension(STRING(10)) {
    label = "city"

    format { value ->
      value?.uppercase()
    }
  }

  // You can either change the chart type in INIT or CHARTTYPE trigger
  val init = trigger(INITCHART) {
    chartType = VChartType.BAR
  }

  init {
    city.add("Tunis") {
      this[area] = BigDecimal("34600")
      this[population] = 1056247
    }

    city.add("Kasserine") {
      this[area] = BigDecimal("806600")
      this[population] = 439243
    }

    city.add("Bizerte") {
      this[area] = BigDecimal("568219")
      this[population] = 368500
    }
  }
}
