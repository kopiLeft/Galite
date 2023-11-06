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

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Client
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivotTable.PivotTable
import org.kopi.galite.visual.dsl.pivotTable.Position
import org.kopi.galite.visual.pivotTable.Constants

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


  val firstName = dimension(STRING(25), Position.DIMENSION_ROW) {
    label = "First Name"
    help = "The client first name"
  }

  val lastName = dimension(STRING(25), Position.DIMENSION_ROW) {
    label = "Last Name"
    help = "The client last name"
  }

  val addressClt = dimension(STRING(50), Position.DIMENSION_ROW) {
    label = "Address"
    help = "The client address"
  }

  val ageClt = dimension(INT(2), Position.DIMENSION_COL) {
    label = "Age"
    help = "The client age"
  }

  val countryClt = dimension(STRING(50), Position.DIMENSION_ROW) {
    label = "Country"
    help = "The client country"
  }

  val cityClt = dimension(STRING(50), Position.MEASURE) {
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

  val init = trigger(INITPIVOTTABLE) {
    pivotTableType = Constants.DEFAULT_RENDERER
    aggregator = Pair(Constants.SAMPLE_VARIANCE, Constants.DEFAULT_AGGREGATE_COLUMN)
    disabledRerenders = mutableListOf(Constants.SCATTER_CHART, Constants.LINE_CHART, Constants.HORIZONTAL_BAR_CHART, Constants.HORIZONTAL_STACKED_BAR_CHART)
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
