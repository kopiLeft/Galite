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
import org.kopi.galite.visual.dsl.pivottable.Dimension.Position
import org.kopi.galite.visual.dsl.pivottable.PivotTable

/**
 * Product Report
 */
class ProductP : PivotTable(title = "Products", locale = Locale.UK) {

  val action = menu("Action")

  val quit = actor(menu = action, label = "Quit", help = "Quit", ident = "quit") {
    key = Key.F1
    icon = Icon.QUIT
  }
  val cmdQuit = command(item = quit) { model.close() }

  val product = dimension(STRING(50), Position.NONE) {
    label = "Product"
    help = "The product description"
  }
  val department = dimension(STRING(20), Position.COLUMN) {
    label = "Department"
    help = "The product department"
  }
  val supplier = dimension(STRING(20), Position.ROW) {
    label = "Supplier"
    help = "The supplier"
  }
  val tax = dimension(Tax, Position.ROW) {
    label = "Tax"
    help = "The product tax name"
  }
  val category = dimension(Category, Position.ROW) {
    label = "Category"
    help = "The product category"
  }
  val price = measure(DECIMAL(10, 5)) {
    label = "Price"
    help = "The product unit price excluding VAT"
  }

  init {
    val products = Product.selectAll()

    transaction {
      products.forEach { result ->
        add {
          this[product] = result[Product.description]
          this[department] = result[Product.department].orEmpty()
          this[supplier] = result[Product.supplier].orEmpty()
          this[category] = result[Product.category]
          this[tax] = result[Product.taxName]
          this[price] = result[Product.price]
        }
      }
    }
  }
}
