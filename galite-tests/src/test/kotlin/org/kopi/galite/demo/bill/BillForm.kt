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
package org.kopi.galite.demo.bill

import java.util.Locale

import org.kopi.galite.demo.Bill
import org.kopi.galite.demo.Command
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.type.Decimal

object BillForm : Form() {
  override val locale = Locale.FRANCE
  override val title = "bill form"
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
  val tb1 = insertBlock(BlockBill, page)
}

object BlockBill : FormBlock(1, 1, "block bill") {
  val u = table(Bill)
  val v = table(Command)

  val numBill = hidden(domain = Domain<Int>(20)) {
    label = "bill number"
    help = "The bill number"
    columns(u.numBill)
  }
  val addressBill = mustFill(domain = Domain<String>(30), position = at(2, 1)) {
    label = "bill address"
    help = "The bill address"
    columns(u.addressBill)
  }
  val dateBill = mustFill(domain = Domain<String>(25), position = at(1, 1)) {
    label = "client id"
    help = "The client id"
    columns(u.dateBill)
  }
  val amountTTC = visit(domain = Domain<Decimal>(20), position = at(4, 1)) {
    label = "bill amount to pay"
    help = "bill amount to pay"
    columns(u.amountTTC)
  }
  val refCmd = visit(domain = Domain<Int>(20), position = at(5, 1)) {
    label = "command reference"
    help = "The command reference"
    columns(u.refCmd,v.numCmd)
  }
}

fun main() {
  Application.runForm(formName = BillForm)
}
