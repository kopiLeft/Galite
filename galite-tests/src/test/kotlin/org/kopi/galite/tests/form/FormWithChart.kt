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
package org.kopi.galite.tests.form

import java.util.Locale

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.tests.chart.ChartSample

object FormWithChart: Form() {
  override val locale = Locale.FRANCE
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
  val p2 = page("test page2")

  val tb1 = insertBlock(TestBlock(), p1) {
    command(item = graph) {
      mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
      action = {
        println("---------------------------------- Generating a chart ----------------------------------")
        showChart(ChartSample)
      }
    }
  }
}

fun main(){
  Application.runForm(formName = FormWithChart)
}
