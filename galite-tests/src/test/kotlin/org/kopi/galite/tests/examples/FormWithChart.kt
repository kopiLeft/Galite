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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.visual.chart.VChartType
import org.kopi.galite.visual.chart.VColumnFormat
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.insertBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.visual.VColor

object FormWithChart: Form() {
  override val locale = Locale.UK
  override val title = "form for test"

  val action = menu("Action")

  val graph = actor (
          ident =  "graph",
          menu =   action,
          label =  "Graph for test",
          help =   "show graph values" ,
  ) {
    key  =  Key.F9          // key is optional here
    icon =  "column_chart"  // icon is optional here
  }

  val p1 = page("test page")
  val b = p1.insertBlock(Traineeship())

  val cmd = command(item = graph) {
    mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
    action = {
      showChart(ChartSample())
    }
  }
}

class ChartSample: Chart() {
  override val locale = Locale.UK
  override val title = "Area/population per city"
  override val help = "This chart presents the area/population per city"

  val action = menu("Action")

  val greeting = actor(
          ident = "greeting",
          menu = action,
          label = "Greeting",
          help = "Click me to show greeting",
  ) {
    key  =  Key.F1          // key is optional here
    icon =  "ask"  // icon is optional here
  }

  val cmd = command(item = greeting) {
    action = {
      println("----------- Hello Galite ----------------")
    }
  }

  val area = measure(DECIMAL(width = 10, scale = 5)) {
    label = "area (ha)"

    color {
      VColor.GREEN
    }
  }

  val population = measure(INT(10)) {
    label = "population"
  }

  val city = dimension(STRING(10)) {
    label = "dimension"

    format {
      object : VColumnFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  // You can either change the chart type in INIT or CHARTTYPE trigger
  val init = trigger(INITCHART) {
    chartType = VChartType.BAR
  }

  // This is the type that will be taken because CHARTTYPE is executed after INIT
  val type = trigger(CHARTTYPE) {
    VChartType.BAR
  }

  init {
    city.add("Tunis") {
      this[area] = Decimal("34600")
      this[population] = 1056247
    }

    city.add("Kasserine") {
      this[area] = Decimal("806600")
      this[population] = 439243
    }

    city.add("Bizerte") {
      this[area] = Decimal("568219")
      this[population] = 368500
    }
  }
}

fun main() {
  Application.runForm(formName = FormWithChart)
}
