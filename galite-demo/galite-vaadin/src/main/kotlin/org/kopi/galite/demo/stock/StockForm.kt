/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.demo.database.Stock
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm

class StockForm : ReportSelectionForm() {
  override val locale = Locale.UK
  override val title = "Stocks"
  val page = page("Stock")
  val action = menu("Action")
  val edit = menu("Edit")
  val autoFill = actor(
          ident = "Autofill",
          menu = edit,
          label = "Autofill",
          help = "Autofill",
  )
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8
    icon = Icon.REPORT
  }

  val block = page.insertBlock(StockBlock()) {
    command(item = report) {
      createReport(StockR())
    }
  }
}

class StockBlock : Block(1, 1, "Stock") {
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
  runForm(formName = StockForm())
}
