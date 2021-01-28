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
package org.kopi.galite.demo.command

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.demo.Command
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.VReport

/**
 * Command Report
 */
object CommandR : Report() {
  override val locale = Locale.FRANCE

  override val title = "CommandReport"

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

  val numCmd = field(Domain<Int>(25)) {
    label = "command number"
    help = "The command number"
    align = FieldAlignment.LEFT

  }

  val idClt = field(Domain<Int>(25)) {
    label = "command client id"
    help = "The command client id"
    align = FieldAlignment.LEFT

  }
  val dateCmd = field(Domain<String>(50)) {
    label = "command date"
    help = "The command date"
    align = FieldAlignment.LEFT
  }

  val paymentMethod = field(Payment) {
    label = "payment method"
    help = "The payment method"
    align = FieldAlignment.LEFT
  }

  val statusCmd = field(CommandStatus) {
    label = "command status"
    help = "The command status"
    align = FieldAlignment.LEFT
  }

  object Payment : CodeDomain<String>() {
    init {
      "cash" keyOf "cash"
      "check" keyOf "check"
      "bank card" keyOf "bank card"
    }
  }

  object CommandStatus : CodeDomain<String>() {
    init {
      "In preparation" keyOf "in preparation"
      "available" keyOf "available"
      "delivered" keyOf "delivered"
      "canceled" keyOf "canceled"
    }
  }

  val q = Command.selectAll()

  init {
    transaction {
      q.forEach { result ->
        add {
          this[numCmd] = result[Command.numCmd]
          this[idClt] = result[Command.idClt]
          this[dateCmd] = result[Command.dateCmd]
          this[paymentMethod] = result[Command.paymentMethod]
          this[statusCmd] = result[Command.statusCmd]
        }
      }
    }
  }
}
