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

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.Client
import org.kopi.galite.demo.Command
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.INT
import org.kopi.galite.form.dsl.Access
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.Modes
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report

class CommandForm : ReportSelectionForm() {
  override val locale = Locale.UK
  override val title = "Commands"
  val page = page("Command")
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

  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F1   // key is optional here
    icon = "list"  // icon is optional here
  }

  val resetBlock = actor(
          ident = "reset",
          menu = action,
          label = "break",
          help = "Reset Block",
  ) {
    key = Key.F3   // key is optional here
    icon = "break"  // icon is optional here
  }

  val serialQuery = actor(
          ident = "serialQuery",
          menu = action,
          label = "serialQuery",
          help = "serial query",
  ) {
    key = Key.F6   // key is optional here
    icon = "serialquery"  // icon is optional here
  }

  val dynamicReport = actor(
          ident = "dynamicReport",
          menu = action,
          label = "DynamicReport",
          help = " Create Dynamic Report",
  ) {
    key = Key.F8          // key is optional here
    icon = "report"  // icon is optional here
  }

  val tb1 = insertBlock(BlockCommand(), page) {
    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }

    command(item = list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }
    command(item = resetBlock) {
      action = {
        resetBlock()
      }
    }
    command(item = serialQuery) {
      action = {
        serialQuery()
      }
    }

    command(item = dynamicReport) {
      action = {
        createDynamicReport()
      }
    }
  }

  override fun createReport(): Report {
    return CommandR()
  }
}

class BlockCommand : FormBlock(1, 10, "Commands") {
  val u = table(Command)
  val v = table(Client)

  val numCmd = hidden(domain = INT(20)) {
    label = "Number"
    help = "The command number"
    columns(u.numCmd)
  }

  val idClt = mustFill(domain = INT(25), position = at(1, 1)) {
    label = "Client ID"
    help = "The client ID"
    columns(u.idClt, v.idClt) {
      priority = 1
    }
  }
  val paymentMethod = mustFill(domain = Payment, position = at(3, 1)) {
    label = "Payment method"
    help = "The payment method"
    columns(u.paymentMethod) {
      priority = 1
    }
  }
  val statusCmd = mustFill(domain = CommandStatus, position = at(4, 1)) {
    label = "Command status"
    help = "The command status"
    columns(u.statusCmd) {
      priority = 1
    }
  }

  init {
    blockVisibility(Access.VISIT, Modes.QUERY)
  }
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
    "in preparation" keyOf "in preparation"
    "available" keyOf "available"
    "delivered" keyOf "delivered"
    "canceled" keyOf "canceled"
  }
}

fun main() {
  Application.runForm(formName = CommandForm())
}
