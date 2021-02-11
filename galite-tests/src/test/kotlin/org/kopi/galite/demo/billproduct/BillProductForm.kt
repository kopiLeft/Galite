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

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.Bill
import org.kopi.galite.demo.BillProduct
import org.kopi.galite.demo.Product
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import org.kopi.galite.type.Decimal
import java.math.BigDecimal

object BillProductForm : ReportSelectionForm() {
  override val locale = Locale.FRANCE
  override val title = "Bill products"
  val page = page("Bill product")
  val action = menu("Action")
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

object BlockBillProduct : FormBlock(1, 1, "bill product") {
  val u = table(BillProduct)
  val v = table(Product)
  val w = table(Bill)

  val idBPdt = hidden(domain = Domain<Int>(20)) {
    label = "Product ID"
    help = "The bill product ID"
    columns(u.idBPdt, v.idPdt)
  }
  val quantity = mustFill(domain = Domain<Int>(30), position = at(1, 1)) {
    label = "Quantity"
    help = "The quantity"
    columns(u.quantity)
  }
  val amount = visit(domain = Domain<BigDecimal>(20), position = at(2, 1)) {
    label = "Amount before tax"
    help = "The amount before tax to pay"
    columns(u.amount)
  }
  val amountWithTaxes = visit(domain = Domain<Decimal>(20), position = at(3, 1)) {
    label = "Amount all taxes included"
    help = "The amount all taxes included to pay"
    columns(u.amountWithTaxes, w.amountWithTaxes)
  }
}

fun main() {
  Application.runForm(formName = BillProductForm)
}
