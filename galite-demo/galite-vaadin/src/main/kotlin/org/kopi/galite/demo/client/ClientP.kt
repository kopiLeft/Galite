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

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.alias
import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Client
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivotTable.Dimension.Position
import org.kopi.galite.visual.dsl.pivotTable.PivotTable
import org.vaadin.addons.componentfactory.PivotTable.Aggregator
import org.vaadin.addons.componentfactory.PivotTable.Renderer

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

  val cmdQuit = command(item = quit) {
    model.close()
  }
  val helpCmd = command(item = helpForm) {
    model.showHelp()
  }


  val firstName = dimension(STRING(25), Position.ROW) {
    label = "First Name"
    help = "The client first name"
  }

  val lastName = dimension(STRING(25), Position.ROW) {
    label = "Last Name"
    help = "The client last name"
  }

  val addressClt = dimension(STRING(50), Position.ROW) {
    label = "Address"
    help = "The client address"
  }

  val ageClt = dimension(INT(2), Position.COLUMN) {
    label = "Age"
    help = "The client age"
  }

  val countryClt = dimension(STRING(50), Position.ROW) {
    label = "Country"
    help = "The client country"
  }

  val cityClt = dimension(STRING(50), Position.NONE) {
    label = "City"
    help = "The client city"
  }

  val zipCodeClt = measure(INT(2)) {
    label = "Zip code"
    help = "The client zip code"
  }

  val activeClt = measure(BOOL) {
    label = "Status"
    help = "Is the client active?"
  }

  val init = trigger(INIT) {
    defaultRenderer = Renderer.TABLE
    aggregator = Pair(Aggregator.SAMPLE_VARIANCE, "")
    disabledRerenders = mutableListOf(Renderer.SCATTER_CHART, Renderer.LINE_CHART, Renderer.HORIZONTAL_BAR_CHART, Renderer.HORIZONTAL_STACKED_BAR_CHART)
    //interactive = Constants.MODE_NONINTERACTIVE
  }

  val clients = Client.selectAll()

  init {
    transaction {
      clients.forEach { result ->
        add {
          this[firstName] = result[Client.firstNameClt]
          this[lastName] = result[Client.lastNameClt]
          this[addressClt] = result[Client.addressClt]
          this[ageClt] = result[Client.ageClt]
          this[countryClt] = result[Client.countryClt]
          this[cityClt] = result[Client.cityClt]
          this[zipCodeClt] = result[Client.zipCodeClt]
          this[activeClt] = result[Client.activeClt]
        }
      }
    }
  }
}
