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
package org.kopi.galite.tests.examples

import java.util.Locale
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.db.transaction
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key

class MultipleBlockForm : DictionaryForm(title = "Training Form", locale = Locale.UK) {
  val page1 = page("page1")
  val page2 = page("page2")
  val action = menu("Action")
  val autoFill = actor(
    menu = action,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )
  val list = actor(
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F2
    icon = Icon.LIST
  }
  val query = actor(
    menu = action,
    label = "query",
    help = "query",
  ) {
    key = Key.F3
    icon = Icon.LIST
  }
  val load = actor(
    menu = action,
    label = "load",
    help = "load",
  ) {
    key = Key.F8
    icon = Icon.LIST
  }
  val changeBlock = actor(
    menu = action,
    label = "change Block",
    help = "change Block",
  ) {
    key = Key.F4
    icon = Icon.REFRESH
  }
  val resetBlock = actor(
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F5
    icon = Icon.BREAK
  }
  val showHideFilter = actor(
    menu = action,
    label = "Show/Hide Filter",
    help = " Show Hide Filter",
  ) {
    key = Key.F6
    icon = Icon.SEARCH_OP
  }
  val add = actor(
    menu = action,
    label = "add",
    help = " add",
  ) {
    key = Key.F10
    icon = Icon.ADD
  }
  val resetForm = actor(
    menu = action,
    label = "resetForm",
    help = "Reset Form",
  ) {
    key = Key.F7
    icon = Icon.BREAK
  }
  val resetFormCmd = command(item = resetForm) {
    resetForm()
  }

  val block = page1.insertBlock(Traineeship()) {
    trigger(POSTQRY) {
      block2.trainingId[0] = trainingID.value
      block2.load()
    }

    command(item = list) {
      recursiveQuery()
    }
    command(item = query) {
      queryMove()
    }
    command(item = load) {
      transaction {
        load()
      }
    }
    command(item = changeBlock) {
      changeBlock()
    }
  }

  val block2 = page1.insertBlock(Centers()) {
    command(item = resetBlock) {
      resetBlock()
    }
    command(item = showHideFilter) {
      showHideFilter()
    }
    command(item = add) {
      insertLine()
    }
  }
  val block3 = page2.insertBlock(SimpleBlock()) {
    command(resetBlock)  {

    }
  }

  class Centers : Block("Centers", 20, 20) {
    val c = table(Center)
    val t = table(Training)

    val unique = index(message = "ID should be unique")

    val CenterId = hidden(domain = INT(20)) {
      label = "center id"
      help = "The Center id"
      columns(c.id) {
        index = unique
      }
    }
    val trainingId = hidden(domain = INT(20)) {
      label = "training id"
      help = "The training id"
      columns(c.refTraining, t.id) {
        index = unique
      }
    }
    val centerName = visit(domain = STRING(20), position = at(1, 1)) {
      label = "center name"
      help = "center name"
      columns(c.centerName)
    }
    val address = visit(domain = STRING(20), position = at(1, 2)) {
      label = "address"
      help = "address"
      columns(c.address)
    }
    val mail = visit(domain = STRING(20), position = at(1, 3)) {
      label = "mail"
      help = "mail"
      columns(c.mail)
    }
    val country = visit(domain = STRING(20), position = at(1, 4)) {
      label = "country"
      help = "country"
      columns(c.country)
    }
    val city = visit(domain = STRING(20), position = at(1, 5)) {
      label = "city"
      help = "city"
      columns(c.city)
    }
    val zipCode = visit(domain = INT(5), position = at(1, 6)) {
      label = "zipCode"
      help = "zipCode"
      columns(c.zipCode)
    }

    init {
      border = Border.LINE
    }
  }

  class SimpleBlock : Block("Simple block", 1, 1) {
    val contact = visit(domain = STRING(20), position = at(1, 1)) {
      label = "contact"
      help = "The contact"
    }
    val name = visit(domain = STRING(20), position = follow(contact)) {}

    init {
      border = Border.LINE
    }
  }
}

fun main() {
  runForm(formName = MultipleBlockForm()) {
    transaction {
      initData()
    }
  }
}
