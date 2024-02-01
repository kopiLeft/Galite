/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.demo.product

import java.util.Locale

import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll
import org.kopi.galite.demo.database.Product
import org.kopi.galite.visual.chart.VChartType
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.domain.LONG
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key

class ProductChart  : Chart(
  locale = Locale.UK,
  title = "Quantity of products per category",
  help = "This chart presents the quantity of product per Category"
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

  val pie = actor(menu = action, label = "Pie", help = " Pie", ident = "Pie view") {
    key = Key.F5
    icon = Icon.PIE_CHART
  }

  val column = actor(menu = action, label = "Column", help = " column", ident = "Column view") {
    key = Key.F9
    icon = Icon.COLUMN_CHART
  }

  val cmdQuit = command(item = quit) {
    model.close()
  }

  val helpCmd = command(item = helpForm) {
    model.showHelp()
  }

  val pieCmd = command(item = pie) {
    model.setType(VChartType.PIE)
  }

  val columnCmd = command(item = column) {
    model.setType(VChartType.COLUMN)
  }

  val quantity = measure(LONG(10)) {
    label = "Product quantity"
    help = "The quantity of product per Category"
  }

  val category = dimension(STRING(50)) {
    label = "Category"
    help = "The product category"

    format { value ->
      value?.uppercase()
    }
  }

  // You can either change the chart type in INIT or CHARTTYPE trigger
  val init = trigger(INITCHART) {
    chartType = VChartType.PIE
  }

  init {
    transaction {
      val products = Product.slice(Product.category, Product.category.count())
        .selectAll()
        .groupBy(Product.category)

      products.forEach { result ->
        category.add(decodeCategory(result[Product.category])) {
          this[quantity] = result[Product.category.count()]
        }
      }
    }
  }
}

/**
 * Decode category code
 * !!! FIXME : Fix pivot table to accept CodeDomain type and automatically convert a code to its value
 */
fun decodeCategory(category: Int) : String {
  return when (category) {
    1 -> "shoes"
    2 -> "shirts"
    3 -> "glasses"
    4 -> "pullovers"
    5 -> "jeans"
    else -> "UNKNOWN"
  }
}