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

import org.kopi.galite.demo.Client
import org.kopi.galite.demo.Command
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key

object CommandForm : Form() {
  override val locale = Locale.FRANCE
  override val title = "command form"
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
  val tb1 = insertBlock(BlockCommand, page)
}

object BlockCommand : FormBlock(1, 1, "block command") {
  val u = table(Command)
  val v = table(Client)

  val numCmd = hidden(domain = Domain<Int>(20)) {
    label = "command number"
    help = "The command number"
    columns(u.numCmd)
  }
  val idClt = mustFill(domain = Domain<Int>(25), position = at(1, 1)) {
    label = "client id"
    help = "The client id"
    columns(u.idClt, v.idClt)
  }
  val dateCmd = mustFill(domain = Domain<String>(25), position = at(2, 1)) {
    label = "command date"
    help = "The command date"
    columns(u.dateCmd)
  }
  val paymentMethod = visit(domain = Payment, position = at(3, 1)) {
    label = "payment method"
    help = "The payment method"
    columns(u.paymentMethod)
  }
  val statusCmd = visit(domain = CommandStatus, position = at(4, 1)) {
    label = "command status"
    help = "The command status"
    columns(u.statusCmd)
  }
}
object Payment: CodeDomain<String>() {
  init {
    "cash" keyOf "cash"
    "check" keyOf "check"
    "bank card" keyOf  "bank card"
  }
}
object CommandStatus: CodeDomain<String>() {
  init {
    "In preparation" keyOf "in preparation"
    "available" keyOf "available"
    "delivered" keyOf  "delivered"
  }
}
fun main() {
  Application.runForm(formName = CommandForm)
}
