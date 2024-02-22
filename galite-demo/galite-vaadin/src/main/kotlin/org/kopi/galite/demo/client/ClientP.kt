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

import java.util.Locale

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll

import org.vaadin.addons.componentfactory.PivotTable.Aggregator
import org.vaadin.addons.componentfactory.PivotTable.FunctionName
import org.vaadin.addons.componentfactory.PivotTable.Renderer

import org.kopi.galite.demo.database.Client
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Purchase
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivottable.Dimension.Position
import org.kopi.galite.visual.dsl.pivottable.PivotTable

/**
 * Client Report
 */
class ClientP : PivotTable(title = "Clients_Pivot_Table", locale = Locale.UK) {
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
  val cmdQuit = command(item = quit) { model.close() }
  val helpCmd = command(item = helpForm) { model.showHelp() }

  val firstName = dimension(STRING(25), Position.NONE) {
    label = "First Name"
    help = "The client first name"
  }
  val lastName = dimension(STRING(25), Position.NONE) {
    label = "Last Name"
    help = "The client last name"
  }
  val countryClt = dimension(STRING(50), Position.COLUMN) {
    label = "Country"
    help = "The client country"
  }
  val cityClt = dimension(STRING(50), Position.COLUMN) {
    label = "City"
    help = "The client city"
  }
  val ageClt = dimension(INT(2), Position.COLUMN) {
    label = "Age"
    help = "The client age"
  }
  val zipCodeClt = dimension(INT(2), Position.NONE) {
    label = "Zip code"
    help = "The client zip code"
  }
  val product = dimension(INT(2), Position.ROW) {
    label = "Product"
    help = "The product ID"
  }
  val productDescription = dimension(STRING(50), Position.ROW) {
    label = "Product Description"
    help = "The product Description"
  }
  val quantity = measure(INT(10)) {
    label = "Quantity"
    help = "Product quantity"
  }
  val price = measure(DECIMAL(9,3)) {
    label = "Price"
    help = "Product price"
  }

  val init = trigger(INIT) {
    defaultRenderer = Renderer.TABLE
    aggregator = Pair(Aggregator.SUM, price.label!!)
    customAggregators = mapOf(mapOf(Aggregator.SUM to FunctionName.SUM,Aggregator.MAXIMUM to FunctionName.MAXIMUM) to price.label!!)
  }

  val purchases = Client.join(Purchase, JoinType.LEFT) {
    Purchase.idClt eq Client.idClt
  }.join(Product, JoinType.LEFT) {
    Purchase.idPdt eq Product.idPdt
  }.slice(
    Client.firstNameClt,
    Client.lastNameClt,
    Client.ageClt,
    Client.countryClt,
    Client.cityClt,
    Client.zipCodeClt,
    Product.idPdt,
    Product.description,
    Purchase.quantity,
    Product.price
  ).selectAll()

  init {
    transaction {
      purchases.forEach { result ->
        add {
          this[firstName] = result[Client.firstNameClt]
          this[lastName] = result[Client.lastNameClt]
          this[ageClt] = result[Client.ageClt]
          this[countryClt] = result[Client.countryClt]
          this[cityClt] = result[Client.cityClt]
          this[zipCodeClt] = result[Client.zipCodeClt]
          this[product] = result[Product.idPdt]
          this[productDescription] = result[Product.description]
          this[quantity] = result[Purchase.quantity]
          this[price] = result[Product.price]
        }
      }
    }
  }
}
