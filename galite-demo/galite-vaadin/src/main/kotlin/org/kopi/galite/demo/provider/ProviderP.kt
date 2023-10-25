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
package org.kopi.galite.demo.provider

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivotTable.PivotTable

/**
 * Provider Report
 */
class ProviderP : PivotTable(title = "Providers_Report", locale = Locale.UK) {

  val action = menu("Action")

  val nameProvider = field(STRING(50)) {
    label = "Name"
  }

  val tel = field(INT(25)) {
    label = "Phone number"
  }

  val description = field(STRING(255)) {
    label = "Description"
  }

  val address = field(STRING(70)) {
    label = "Address"
  }

  val zipCode = field(INT(50)) {
    label = "Zip code"
  }

  val providers = Provider.selectAll()

  init {
    transaction {
      providers.forEach { result ->
        add {
          this[nameProvider] = result[Provider.nameProvider]
          this[description] = result[Provider.description]
          this[tel] = result[Provider.tel]
          this[zipCode] = result[Provider.zipCode]
          this[address] = result[Provider.address]
        }
      }
    }
  }
}
