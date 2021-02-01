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

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.demo.Bill
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.VReport
import org.kopi.galite.type.Decimal

/**
 * Bill Report
 */
object BillR : Report() {
  override val locale = Locale.FRANCE

  override val title = "Bills_Report"

  val action = menu("Action")

  val csv = actor(
          ident = "CSV",
          menu = action,
          label = "CSV",
          help = "Obtenir le format CSV",
  ) {
    key = Key.F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val xls = actor(
          ident = "XLS",
          menu = action,
          label = "XLS",
          help = "Obtenir le format Excel (XLS)",
  ) {
    key = Key.SHIFT_F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val xlsx = actor(
          ident = "XLSX",
          menu = action,
          label = "XLSX",
          help = "Obtenir le format Excel (XLSX)",
  ) {
    key = Key.SHIFT_F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val pdf = actor(
          ident = "PDF",
          menu = action,
          label = "PDF",
          help = "Obtenir le format PDF",
  ) {
    key = Key.F9          // key is optional here
    icon = "export"  // icon is optional here
  }

  val cmdCSV = command(item = csv) {
    action = {
      model.export(VReport.TYP_CSV)
    }
  }

  val cmdPDF = command(item = pdf) {
    action = {
      model.export(VReport.TYP_PDF)
    }
  }

  val cmdXLS = command(item = xls) {
    action = {
      model.export(VReport.TYP_XLS)
    }
  }

  val cmdXLSX = command(item = xlsx) {
    action = {
      model.export(VReport.TYP_XLSX)
    }
  }

  val numBill = field(Domain<Int>(25)) {
    label = "Number"
    help = "The bill number"
    align = FieldAlignment.LEFT
  }

  val addressBill = field(Domain<String>(25)) {
    label = "Address"
    help = "The bill address"
    align = FieldAlignment.LEFT
  }
  val dateBill = field(Domain<String>(25)) {
    label = "Date"
    help = "The bill date"
    align = FieldAlignment.LEFT
  }

  val amountTTC = field(Domain<Decimal>(2)) {
    label = "Amount to pay"
    help = "The amount including all taxes to pay"
    align = FieldAlignment.LEFT
  }

  val refCmd = field(Domain<Int>(50)) {
    label = "Command reference"
    help = "The command reference"
    align = FieldAlignment.LEFT
  }

  val q = Bill.selectAll()

  init {
    transaction {
      q.forEach { result ->
        add {
          this[numBill] = result[Bill.numBill]
          this[addressBill] = result[Bill.addressBill]
          this[dateBill] = result[Bill.dateBill]
          this[amountTTC] = Decimal(result[Bill.amountTTC])
          this[refCmd] = result[Bill.refCmd]
        }
      }
    }
  }
}