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
package org.kopi.galite.demo.billproduct

import java.util.Locale
import org.kopi.galite.demo.database.Bill
import org.kopi.galite.demo.database.BillProduct
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.desktop.runForm

import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.dsl.report.Report

class BillProductForm : ReportSelectionForm() {
  override val locale = Locale.UK
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
    icon = "report"  // icon is optional here
  }
  val tb1 = insertBlock(BlockBillProduct(), page) {
    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }
  }

  override fun createReport(): Report {
    return BillProductR()
  }
}

class BlockBillProduct : FormBlock(1, 1, "bill product") {
  val u = table(BillProduct)
  val v = table(Product)
  val w = table(Bill)

  val idBPdt = hidden(domain = INT(20)) {
    label = "Product ID"
    help = "The bill product ID"
    columns(u.idBPdt, v.idPdt)
  }
  val quantity = mustFill(domain = INT(30), position = at(1, 1)) {
    label = "Quantity"
    help = "The quantity"
    columns(u.quantity)
  }
  val amount = visit(domain = DECIMAL(20, 10), position = at(2, 1)) {
    label = "Amount before tax"
    help = "The amount before tax to pay"
    columns(u.amount)
  }
  val amountWithTaxes = visit(domain = DECIMAL(20, 10), position = at(3, 1)) {
    label = "Amount all taxes included"
    help = "The amount all taxes included to pay"
    columns(u.amountWithTaxes, w.amountWithTaxes)
  }
}

fun main() {
  runForm(formName = BillProductForm())
}
