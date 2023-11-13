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
package org.kopi.galite.demo.billproduct

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.BillProduct
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.pivottable.PivotTable

/**
 * Products Bill Report
 */
class BillProductP : PivotTable("Bill Product Report", Locale.UK) {

  val quantity = measure(INT(25)) {
    label = "Quantity"
  }

  val amount = measure(DECIMAL(25, 10)) {
    label = "Amount before tax"

  }
  val amountWithTaxes = measure(DECIMAL(50, 10)) {
    label = "Amount all taxes included"
  }

  val billProducts = BillProduct.selectAll()

  init {
    transaction {
      billProducts.forEach { result ->
        add {
          this[quantity] = result[BillProduct.quantity]
          this[amount] = result[BillProduct.amount]
          this[amountWithTaxes] = result[BillProduct.amountWithTaxes]
        }
      }
    }
  }
}
