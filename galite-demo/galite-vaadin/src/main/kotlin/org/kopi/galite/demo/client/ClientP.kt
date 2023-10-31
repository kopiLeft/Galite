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


  val firstName = field(STRING(25)) {
    label = "First Name"
  }

  val lastName = field(STRING(25)) {
    label = "Last Name"
  }

  val addressClt = field(STRING(50)) {
    label = "Address"
  }

  val ageClt = field(INT(2)) {
    label = "Age"
  }

  val countryClt = field(STRING(50)) {
    label = "Country"
  }

  val cityClt = field(STRING(50)) {
    label = "City"
  }

  val zipCodeClt = field(INT(2)) {
    label = "Zip code"
  }

  val activeClt = field(BOOL) {
    label = "Status"
  }

  val init = trigger(INITPIVOTTABLE) {
    pivotTableType = Constants.TABLE_BARCHART
    aggregator = Pair(Constants.AVERAGE, ageClt.label!!)
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
