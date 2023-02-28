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

import org.kopi.galite.demo.common.FormDefault
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.demo.database.Stock
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.form.Block

class StockForm : FormDefault(title = "Stocks", locale = Locale.UK) {

  val page = page("Stock")

  init {
    insertMenus()
    insertCommands()
  }

  val block = page.insertBlock(StockBlock()) {
    command(item = report) {
      createReport {
        StockR()
      }
    }
  }
}

class StockBlock : Block("Stock", 1, 1) {
  val u = table(Stock)
  val v = table(Product)
  val w = table(Provider)

  val idStckPdt = hidden(domain = INT(20)) {
    label = "Product_ID"
    help = "The product ID"
    columns(u.idStckPdt, v.idPdt)
  }
  val idStckProv = hidden(domain = INT(20)) {
    label = "Provider_ID"
    help = "The provider id"
    columns(u.idStckProv, w.idProvider)
  }
  val minAlert = mustFill(domain = INT(20), position = at(1, 1)) {
    label = "Min Alert"
    help = "The stock's min alert"
    // columns(u.idStckProv, w.idProvider)
  }
}

fun main() {
  runForm(form = StockForm::class)
}
