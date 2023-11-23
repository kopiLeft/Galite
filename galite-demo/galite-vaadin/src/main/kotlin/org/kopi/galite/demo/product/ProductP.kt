/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
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

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Product
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivottable.PivotTable
import org.kopi.galite.visual.dsl.pivottable.Dimension.Position
import org.kopi.galite.visual.pivottable.VPivotTable

/**
 * Product Report
 */
class ProductP : PivotTable(title = "Products", locale = Locale.UK) {

  val action = menu("Action")
  val file = menu("File")

  val quit = actor(menu = file, label = "Quit", help = "Close Report.", ident = "quit") {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val helpForm = actor(menu = action, label = "Help", help = " Help", ident = "help") {
    key = Key.F1
    icon = Icon.HELP
  }

  val pdf = actor(menu = action, label = "PDF", help = "PDF Format", ident = "pdf") {
    key = Key.F9
    icon = Icon.EXPORT_PDF
  }
  val png = actor(menu = action, label = "PNG", help = "PNG Format", ident = "png") {
    key = Key.F8
    icon = Icon.PRINT
  }
  val jpeg = actor(menu = action, label = "JPEG", help = "JPEG Format", ident = "jpeg") {
    key = Key.F10
    icon = Icon.JPG_ICON
  }

  val cmdQuit = command(item = quit) {
    model.close()
  }
  val helpCmd = command(item = helpForm) {
    model.showHelp()
  }
  val cmdPDF = command(item = pdf) {
    model.export(VPivotTable.TYP_PDF)
  }
  val cmdPNG = command(item = png) {
    model.export(VPivotTable.TYP_PNG)
  }
  val cmdJPEG = command(item = jpeg) {
    model.export(VPivotTable.TYP_JPEG)
  }

  val category = dimension(STRING(10), Position.ROW) {
    label = "Category"
    help = "The product category"
  }

  val department = dimension(STRING(20), Position.COLUMN) {
    label = "Department"
    help = "The product department"
  }

  val supplier = dimension(STRING(20), Position.COLUMN) {
    label = "Supplier"
    help = "The supplier"
  }

  val taxName = dimension(STRING(10), Position.COLUMN) {
    label = "Tax"
    help = "The product tax name"
  }

  val price = measure(DECIMAL(10, 5)) {
    label = "Price"
    help = "The product unit price excluding VAT"
  }

  val products = Product.selectAll()

  init {
    transaction {
      products.forEach { result ->
        add {
          this[department] = result[Product.department]
          this[supplier] = result[Product.supplier]
          this[category] = décoderCategory(result[Product.category])
          this[taxName] = décoderTaxe(result[Product.taxName])
          this[price] = result[Product.price]
        }
      }
    }
  }
  fun décoderCategory(category: Int) : String {
    var result: String
    when (category) {
      1 -> result= "shoes"
      2 -> result = "shirts"
      3 -> result = "glasses"
      4 -> result = "pullovers"
      5 -> result = "jeans"
      else -> result = "inconnu"
    }
    return result
  }

  fun décoderTaxe(taxe: String) : String {
    var result: String
    when (taxe) {
      "tax 0" -> result= "0%"
      "tax 1" -> result= "19%"
      "tax 2" -> result = "9%"
      "tax 3" -> result = "13%"
      "tax 4" -> result = "22%"
      "tax 5" -> result = "11%"
      else -> result = "inconnu"
    }
    return result
  }
}
