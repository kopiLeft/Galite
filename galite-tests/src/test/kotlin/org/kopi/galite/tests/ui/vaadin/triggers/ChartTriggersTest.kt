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
package org.kopi.galite.tests.ui.vaadin.triggers

import java.util.Locale

import org.kopi.galite.visual.chart.VChartType
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.Icon

object ChartTriggersTest : Chart(locale = Locale.UK, title = "Chart to test triggers") {
  val action = menu("Action")

  val quit = actor(
    menu = action,
    label = "Quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val quitCmd = command(item = quit) {
    model.close()
  }

  val population = measure(INT(10)) {
    label = "population"
  }

  val city = dimension(STRING(10)) {
    label = "dimension"
  }

  // trigger INIT
  val init = trigger(INITCHART) {
    FormTriggersTests.list.add("INITCHART CHART Trigger")
  }

  // trigger PRECHART
  val preChart = trigger(PRECHART) {
    FormTriggersTests.list.add("PRECHART CHART Trigger")
  }

  // trigger POSTCHART
  val postChart = trigger(POSTCHART) {
    FormTriggersTests.list.add("POSTCHART CHART Trigger")
  }

  // trigger CHARTTYPE
  val type = trigger(CHARTTYPE) {
    FormTriggersTests.list.add("CHARTTYPE CHART Trigger")
    VChartType.PIE
  }

  init {
    city.add("Tunis") {
      this[population] = 1056247
    }

    city.add("Kasserine") {
      this[population] = 439243
    }
  }
}
