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

import org.joda.time.DateTime

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.Client
import org.kopi.galite.demo.Command
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import org.kopi.galite.tests.form.FormWithList

object CommandForm : ReportSelectionForm() {
  override val locale = Locale.FRANCE
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
    icon = "preview"  // icon is optional here
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
  val tb1 = insertBlock(BlockCommand, page) {
    command(item = report) {
      action = {
        createReport(BlockCommand)
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
  }

  override fun createReport(): Report {
    return CommandR
  }
}

object BlockCommand : FormBlock(1, 10, "Commands") {
  val u = table(Command)
  val v = table(Client)

  val numCmd = hidden(domain = Domain<Int>(20)) {
    label = "Number"
    help = "The command number"
    columns(u.numCmd)
  }

  val idClt = visit(domain = Domain<Int>(25), position = at(1, 1)) {
    label = "Client ID"
    help = "The client ID"
    columns(u.idClt, v.idClt) {
      priority = 1
    }
  }
  val paymentMethod = visit(domain = Payment, position = at(3, 1)) {
    label = "Payment method"
    help = "The payment method"
    columns(u.paymentMethod) {
      priority = 1
    }
  }
  val statusCmd = visit(domain = CommandStatus, position = at(4, 1)) {
    label = "Command status"
    help = "The command status"
    columns(u.statusCmd) {
      priority = 1
    }
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
  Application.runForm(formName = CommandForm)
}
