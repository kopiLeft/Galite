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
package org.kopi.galite.demo.taxRule

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.TaxRule
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.pivottable.PivotTable

/**
 * Tax Rules Report
 */
class TaxRuleP : PivotTable(title = "TaxRules_Report", locale = Locale.UK) {

  val action = menu("Action")

  val taxName = measure(STRING(50)) {
    label = "Name"
  }

  val rate = measure(INT(25)) {
    label = "Rate in %"
  }

  val taxRules = TaxRule.selectAll()

  init {
    transaction {
      taxRules.forEach { result ->
        add {
          this[taxName] = result[TaxRule.taxName]
          this[rate] = result[TaxRule.rate]
        }
      }
    }
  }
}
