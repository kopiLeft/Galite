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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key

class ComplexForm : DictionaryForm() {
  override val locale = Locale.UK

  override val title = "Complex Form"
  val page1 = page("page1")
  val page2 = page("page2")
  val action = menu("Action")
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
  val query = actor(
    ident = "query",
    menu = action,
    label = "query",
    help = "query",
  ) {
    key = Key.F2   // key is optional here
    icon = "list"  // icon is optional here
  }
  val changeBlock = actor(
    ident = "change Block",
    menu = action,
    label = "change Block",
    help = "change Block",
  ) {
    key = Key.F2   // key is optional here
    icon = "refresh"  // icon is optional here
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
  val showHideFilter = actor(
    ident = "ShowHideFilter",
    menu = action,
    label = "ShowHideFilter",
    help = " Show Hide Filter",
  ) {
    key = Key.F4
    icon = "searchop"
  }

  val block = insertBlock(Common.Traineeship(), page1) {
    trigger(POSTQRY) {
      block2.trainingId[0] = trainingID.value
      block2.load()
    }

    command(item = list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }
    command(item = query) {
      action = {
        queryMove()
      }
    }
    command(item = changeBlock) {
      action = {
        changeBlock()
      }
    }
  }

  val block2 = insertBlock(Centers(), page1) {
    command(item = resetBlock) {
      action = {
        resetBlock()
      }
    }
    command(item = showHideFilter) {
      action = {
        showHideFilter()
      }
    }
  }
  val block3 = insertBlock(SimpleBlock(), page2)

  class Centers : FormBlock(20, 20, "Centers") {
    val c = table(Center)
    val t = table(Training)

    val unique = index(message = "ID should be unique")

    val CenterId = hidden(domain = Domain<Int>(20)) {
      label = "center id"
      help = "The Center id"
      columns(c.id) {
        index = unique
      }
    }
    val trainingId = hidden(domain = Domain<Int>(20)) {
      label = "training id"
      help = "The training id"
      columns(c.refTraining, t.id) {
        index = unique
      }
    }
    val centerName = visit(domain = Domain<String>(20), position = at(1, 1)) {
      label = "center name"
      help = "center name"
      columns(c.centerName)
    }
    val address = visit(domain = Domain<String>(20), position = at(1, 2)) {
      label = "address"
      help = "address"
      columns(c.address)
    }
    val mail = visit(domain = Domain<String>(20), position = at(1, 3)) {
      label = "mail"
      help = "mail"
      columns(c.mail)
    }
    val country = visit(domain = Domain<String>(20), position = at(1, 4)) {
      label = "country"
      help = "country"
      columns(c.country)
    }
    val city = visit(domain = Domain<String>(20), position = at(1, 5)) {
      label = "city"
      help = "city"
      columns(c.city)
    }
    val zipCode = visit(domain = Domain<Int>(5), position = at(1, 6)) {
      label = "zipCode"
      help = "zipCode"
      columns(c.zipCode)
    }

    init {
      border = VConstants.BRD_LINE
    }
  }

  class SimpleBlock : FormBlock(1, 1, "Simple block") {
    val contact = visit(domain = Domain<String>(20), position = at(1, 1)) {
      label = "contact"
      help = "The contact"
    }
    val name = visit(domain = Domain<String>(20), position = follow(contact)) {

    }
  }
  init {
    transaction {
      SchemaUtils.create(Training, Center)
      addTrainings()
      addCenters()
    }
  }
}

fun main() {
  Application.runForm(formName = ComplexForm())
}
