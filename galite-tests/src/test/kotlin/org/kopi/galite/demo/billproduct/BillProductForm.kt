/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.demo.Bill
import org.kopi.galite.demo.BillProduct
import org.kopi.galite.demo.Product
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import org.kopi.galite.type.Decimal

object BillProductForm : ReportSelectionForm() {
  override val locale = Locale.FRANCE
  override val title = "bill product form"
  val page = page("page")
  val action = menu("act")
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "preview"  // icon is optional here
  }
  val tb1 = insertBlock(BlockBillProduct, page) {
    command(item = report) {
      action = {
        createReport(BlockBillProduct)
      }
    }
  }

  override fun createReport(): Report {
    return BillProductR
  }
}

object BlockBillProduct : FormBlock(1, 1, "block bill product") {
  val u = table(BillProduct)
  val v = table(Product)
  val w = table(Bill)

  val idBPdt = hidden(domain = Domain<Int>(20)) {
    label = "bill product id"
    help = "The bill product id"
    columns(u.idBPdt, v.idPdt)
  }
  val quantity = mustFill(domain = Domain<Int>(30), position = at(1, 1)) {
    label = "quantity"
    help = "quantity"
    columns(u.quantity)
  }
  val amountHT = visit(domain = Domain<Int>(20), position = at(2, 1)) {
    label = "amount HT"
    help = "amount HT"
    columns(u.amountHT)
  }
  val amountTTC = visit(domain = Domain<Decimal>(20), position = at(3, 1)) {
    label = "amount TTC"
    help = "amount TTC"
    columns(u.amountTTC, w.amountTTC)
  }
}

fun main() {
  Application.runForm(formName = BillProductForm)
}
