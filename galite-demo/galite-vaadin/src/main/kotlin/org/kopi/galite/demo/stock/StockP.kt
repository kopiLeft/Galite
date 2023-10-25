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
package org.kopi.galite.demo.stock

import java.util.Locale

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.demo.database.Stock
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivotTable.PivotTable

/**
 * Stock Report
 */
class StockP : PivotTable(title = "Stocks", locale = Locale.UK) {

  val action = menu("Action")

  val description = field(STRING(25)) {
    label = "Description"
  }
  val nameProvider = field(STRING(25)) {
    label = "Provider name"
  }
  val minAlert = field(INT(25)) {
    label = "Min Alert"
  }

  val stocks = Stock.join(Provider, JoinType.INNER, Stock.idStckProv, Provider.idProvider)
          .join(Product, JoinType.INNER, Stock.idStckProv, Product.idPdt)
          .slice(Stock.minAlert, Product.description, Provider.nameProvider)
          .selectAll()

  init {
    transaction {
      stocks.forEach { result ->
        add {
          this[minAlert] = result[Stock.minAlert]
          this[description] = result[Product.description]
          this[nameProvider] = result[Provider.nameProvider]
        }
      }
    }
  }
}
