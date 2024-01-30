package org.kopi.galite.demo.product

import org.jetbrains.exposed.sql.select
import java.util.Locale

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
  title = "number per Category ",
  help = "This chart presents the number per Category"
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

  val number = measure(LONG(10)) {
    label = "number"
    help = "The number of Category "
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
      val shoes = Product.select { Product.category eq 1 }.count()
      val shirts = Product.select { Product.category eq 2 }.count()
      val glasses = Product.select { Product.category eq 3 }.count()
      val pullovers = Product.select { Product.category eq 4 }.count()
      val jeans = Product.select { Product.category eq 5 }.count()


      category.add("shoes") {
        this[number] = shoes
      }
      category.add("shirts") {
        this[number] = shirts

      }
      category.add("glasses") {
        this[number] = glasses
      }
      category.add("pullovers") {
        this[number] = pullovers
      }
      category.add("jeans") {
        this[number] = jeans
      }
    }
  }
}