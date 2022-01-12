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

import org.kopi.galite.visual.chart.VChartType
import org.kopi.galite.visual.chart.VColumnFormat
import org.kopi.galite.visual.db.transaction
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.MONTH
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.visual.VColor
import org.jetbrains.exposed.sql.insert
import org.kopi.galite.visual.dsl.common.Icon

/**
 * test locale, title, help for chart
 * test menu
 * test actor
 * test command
 * create measure & dimension
 * test trigger field : color & format ( upperCase, lowerCase)
 * test chart triggers : init, preChar, postChart, type
 * test data initialization
 */
class DocumentationChartC :  Chart(
  locale = Locale.UK,
  title = "Chart title",
  help = "chart help"
) {

  //chart menu
  val action = menu("Action")

  //chart actor
  val chartActor = actor(
    ident = "chart",
    menu = action,
    label = "chart cmd",
    help = "chart cmd",
  ) {
    key = Key.F8
    icon = Icon.LIST
  }

  val quit = actor(
    ident = "Quit",
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

  // chart command
  val chartCmd = command(item = chartActor) {
    model.notice("chart command")
  }

  val area = measure(DECIMAL(width = 10, scale = 5)) {
    label = "area (ha)"

    // test color trigger !!
    color {
      VColor.GREEN
    }
  }

  /** Creating Charts dimensions and measures **/
  // chart measure
  val population = measure(INT(10)) {
    //test Label measure
    label = "population"
    //test help measure
    help = "measure help"
  }

  // char dimension
  val city = dimension(STRING(10)) {
    //test Label dimension
    label = "dimension"
    //test help measure
    help = "dimension help"

    // test format trigger try to upperCase then to lowerCase
    // to test it remove month dimension and see the result
    format {
      object : VColumnFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
          //return (value as String).toLowerCase()
        }
      }
    }
  }

  val month = dimension(MONTH) {
    label = "Month"
  }

/** Chart Triggers Declaration **/
  // trigger INIT
  val init = trigger(INITCHART) {
  transaction {
    TestTriggers.insert {
      it[id] = 5
      it[INS] = "INITCHART Trigger"
    }
  }
  }

  // trigger PRECHART
  val preChart = trigger(PRECHART) {
    transaction {
      TestTriggers.insert {
        it[id] = 6
        it[INS] = "PRECHART Trigger"
      }
    }
  }

  // trigger POSTCHART
  val postChart = trigger(POSTCHART) {
    transaction {
      TestTriggers.insert {
        it[id] = 7
        it[INS] = "POSTCHART Trigger"
      }
    }
  }

  // // trigger type : change chart type
  val type = trigger(CHARTTYPE) {
    /*// BAR
    VChartType.BAR
    // AREA
    VChartType.AREA
    // COLUMN
    VChartType.COLUMN
    // DEFAULT
    VChartType.DEFAULT
    // LINE
    VChartType.LINE*/
    // PIE
    VChartType.PIE
  }

  /** Chart data initialization **/
  init {
    initDocumentationData()

    month.add(Month(2021, 10)) {
      this[area] = Decimal("34600")
      this[population] = 1056247
    }

    month.add(Month(2021, 12)) {
      this[area] = Decimal("806600")
      this[population] = 439243
    }

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
