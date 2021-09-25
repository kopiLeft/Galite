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
package org.kopi.galite.demo.bill

import java.util.Locale

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.Bill
import org.kopi.galite.demo.Command
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.form.dsl.FormBlock
import org.kopi.galite.visual.form.dsl.Key
import org.kopi.galite.visual.form.dsl.ReportSelectionForm
import org.kopi.galite.visual.report.Report

class BillForm : ReportSelectionForm() {
  override val locale = Locale.UK
  override val title = "Bills"
  val page = page("Bill")
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
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )

  val tb1 = insertBlock(BlockBill(), page) {
    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }
  }

  override fun createReport(): Report {
    return BillR()
  }
}

class BlockBill : FormBlock(1, 1, "Bills") {
  val u = table(Bill)
  val v = table(Command)

  val numBill = hidden(domain = INT(20)) {
    label = "Number"
    help = "The bill number"
    columns(u.numBill)
  }
  val addressBill = mustFill(domain = STRING(30), position = at(1, 1)) {
    label = "Address"
    help = "The bill address"
    columns(u.addressBill)
  }
  val dateBill = mustFill(domain = DATE, position = at(2, 1)) {
    label = "Date"
    help = "The bill date"
    columns(u.dateBill)
  }
  val amountWithTaxes = visit(domain = DECIMAL(20, 10), position = at(3, 1)) {
    label = "Amount to pay"
    help = "The bill amount to pay"
    columns(u.amountWithTaxes)
  }
  val refCmd = visit(domain = INT(20), position = at(4, 1)) {
    label = "Command reference"
    help = "The command reference"
    columns(u.refCmd, v.numCmd)
  }
}

fun main() {
  Application.runForm(formName = BillForm())
}
