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

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.VReport
import org.kopi.galite.type.Decimal
import java.math.BigDecimal

object BillR : Report() {
  override val locale = Locale.FRANCE

  override val title = "ClientReport"

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
    label = "bill number"
    help = "The bill number"
    align = FieldAlignment.LEFT
  }

  val addressBill = field(Domain<String>(25)) {
    label = "bill address"
    help = "The bill address"
    align = FieldAlignment.LEFT
  }
  val dateBill = field(Domain<String>(25)) {
    label = "bill date"
    help = "The bill date"
    align = FieldAlignment.LEFT
  }

  val amountTTC = field(Domain<Decimal>(2)) {
    label = "AMOUNT TTC TO PAY"
    help = "The amount TTC to pay"
    align = FieldAlignment.LEFT
  }

  val refCmd = field(Domain<Int>(50)) {
    label = "command reference city"
    help = "The command reference"
    align = FieldAlignment.LEFT
  }

  init {
    add {
      this[numBill] = 0
      this[addressBill] = "addresse facture 0"
      this[dateBill] = "13/09/20018"
      this[amountTTC] = Decimal(Decimal.valueOf("3129.7").toDouble())
      this[refCmd] = 0
    }
    add {
      this[numBill] = 1
      this[addressBill] = "addresse facture 1"
      this[dateBill] = "16/02/2020"
      this[amountTTC] =Decimal(Decimal.valueOf("1149.24").toDouble())
      this[refCmd] = 1
    }
    add {
      this[numBill] = 2
      this[addressBill] = "addresse facture 2"
      this[dateBill] = "13/05/2019"
      this[amountTTC] = Decimal(Decimal.valueOf("219.6").toDouble())
      this[refCmd] = 2
    }
    add {
      this[numBill] = 3
      this[addressBill] = "addresse facture 3"
      this[dateBill] = "10,Rue du Lac"
      this[amountTTC] = Decimal(Decimal.valueOf("146.9").toDouble())
      this[refCmd] = 3
    }
  }
}
