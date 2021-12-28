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
package org.kopi.galite.demo.command

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Command
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport

/**
 * Command Report
 */
class CommandR : Report() {
  override val locale = Locale.UK

  override val title = "Commands_Report [2020/2021]"

  val action = menu("Action")

  val csv = actor(
          ident = "CSV",
          menu = action,
          label = "CSV",
          help = "CSV Format",
  ) {
    key = Key.F8
    icon = Icon.EXPORT_CSV
  }

  val xls = actor(
          ident = "XLS",
          menu = action,
          label = "XLS",
          help = "Excel (XLS) Format",
  ) {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT_XLSX
  }

  val xlsx = actor(
          ident = "XLSX",
          menu = action,
          label = "XLSX",
          help = "Excel (XLSX) Format",
  ) {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT
  }

  val pdf = actor(
          ident = "PDF",
          menu = action,
          label = "PDF",
          help = "PDF Format",
  ) {
    key = Key.F9
    icon = Icon.EXPORT_PDF
  }

  val cmdCSV = command(item = csv) {
    model.export(VReport.TYP_CSV)
  }

  val cmdPDF = command(item = pdf) {
    model.export(VReport.TYP_PDF)
  }

  val cmdXLS = command(item = xls) {
    model.export(VReport.TYP_XLS)
  }

  val cmdXLSX = command(item = xlsx) {
    model.export(VReport.TYP_XLSX)
  }

  val numCmd = field(INT(25)) {
    label = "Command number"
    help = "The command number"
    align = FieldAlignment.LEFT

  }

  val idClt = field(INT(25)) {
    label = "Client ID"
    help = "The command client ID"
    align = FieldAlignment.LEFT

  }
  val dateCmd = field(DATE) {
    label = "Command date"
    help = "The command date"
    align = FieldAlignment.LEFT
  }

  val paymentMethod = field(Payment) {
    label = "Payment method"
    help = "The payment method"
    align = FieldAlignment.LEFT
  }

  val statusCmd = field(CommandStatus) {
    label = "Command status"
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

  val commands = Command.selectAll()

  init {
    transaction {
      commands.forEach { result ->
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
