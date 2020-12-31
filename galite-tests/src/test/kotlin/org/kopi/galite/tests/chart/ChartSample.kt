/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

import java.util.Locale

import org.kopi.galite.chart.Chart
import org.kopi.galite.chart.VChartType
import org.kopi.galite.common.INITCHART
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key

object ChartSample: Chart()  {
  override val locale = Locale.FRANCE
  override val title = "area/population per city"

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

  val area = measure(Domain<Int?>(10)) {
    label = "area (ha)"
  }

  val population = measure(Domain<Int?>(10)) {
    label = "population"
  }

  val city = dimension(Domain<String>(10)) {
    label = "dimension"
  }

  val init = trigger(INITCHART) {
    chartType = VChartType.PIE
  }

  init {
    city.add("Tunis") {
      this[area] = 34600
      this[population] = 1056247
    }

    city.add("Kasserine") {
      this[area] = 806600
      this[population] = 439243
    }

    city.add("Bizerte") {
      this[population] = 368500
      this[area] = 568219
    }
  }
}
