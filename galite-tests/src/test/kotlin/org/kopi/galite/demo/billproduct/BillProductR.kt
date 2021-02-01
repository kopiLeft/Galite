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

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.demo.BillProduct
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.VReport
import org.kopi.galite.type.Decimal

/**
 * Products Bill Report
 */
object BillProductR : Report() {

  override val locale = Locale.FRANCE

  override val title = "Bill Product Report"

  val action = menu("Action")

  val greeting = actor(
          ident = "greeting",
          menu = action,
          label = "Greeting",
          help = "Click me to show greeting",
  ) {
    key = Key.F1          // key is optional here
    icon = "ask"  // icon is optional here
  }

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

  val quantity = field(Domain<Int>(25)) {
    label = "Quantity"
    help = "The quantity"
    align = FieldAlignment.LEFT
  }

  val amountHT = field(Domain<Int>(25)) {
    label = "Amount before tax"
    help = "The amount before tax to pay"

  }
  val amountTTC = field(Domain<Decimal>(50)) {
    label = "Amount all taxes included"
    help = "The amount all taxes included to pay"
    align = FieldAlignment.LEFT
  }

  val q = BillProduct.selectAll()

  init {
    transaction {
      q.forEach { result ->
        add {
          this[quantity] = result[BillProduct.quantity]
          this[amountHT] = result[BillProduct.amountHT]
          this[amountTTC] = Decimal(result[BillProduct.amountTTC])
        }
      }
    }
  }
}
