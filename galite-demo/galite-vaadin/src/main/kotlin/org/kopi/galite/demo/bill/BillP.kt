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
package org.kopi.galite.demo.bill

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Bill
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivotTable.PivotTable

/**
 * Bill Report
 */
class BillP : PivotTable("Bills_Pivot_Table", locale = Locale.UK) {

  val file = menu("File")

  val quit = actor(menu = file, label = "Quit", help = "Close Report.", ident = "quit") {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val cmdQuit = command(item = quit) {
    model.close()
  }

  val numBill = field(INT(25)) {
    label = "Number"
    help = "The bill number"
  }

  val addressBill = field(STRING(25)) {
    label = "Address"
    help = "The bill address"
  }
  val dateBill = field(DATE) {
    label = "Date"
    help = "The bill date"
  }

  val amountWithTaxes = field(DECIMAL(20, 10)) {
    label = "Amount to pay"
    help = "The amount including all taxes to pay"
  }

  val refCmd = field(INT(50)) {
    label = "Command reference"
    help = "The command reference"
  }

  val bills = Bill.selectAll()

  init {
    transaction {
      bills.forEach { result ->
        add {
          this[numBill] = result[Bill.numBill]
          this[addressBill] = result[Bill.addressBill]
          this[dateBill] = result[Bill.dateBill]
          this[amountWithTaxes] = result[Bill.amountWithTaxes]
          this[refCmd] = result[Bill.refCmd]
        }
      }
    }
  }
}
