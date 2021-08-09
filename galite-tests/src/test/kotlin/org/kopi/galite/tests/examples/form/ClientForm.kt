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
package org.kopi.galite.tests.examples.form

import java.util.Locale

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.type.Decimal

class ClientForm : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Clients"
  val action = menu("Action")
  val quit = actor(
    ident = "quit",
    menu = action,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = "quit"
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

  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F1
    icon = "list"
  }

  val saveBlock = actor(
          ident = "saveBlock",
          menu = action,
          label = "Save Block",
          help = " Save Block",
  ) {
    key = Key.F9
    icon = "save"
  }

  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )

  val helpForm = actor(
          ident = "helpForm",
          menu = action,
          label = "Help",
          help = " Help"
  ) {
    key = Key.F1
    icon = "help"
  }

  val helpCmd = command(item = helpForm) {
    action = {
      showHelp()
    }
  }
  val quitCmd = command(item = quit) {
    action = {
      quitForm()
    }
  }

  val clientsPage= page("Clients")
  val contactsPage= page("Contacts")
  val detailsPage= page("Details")

  val block = insertBlock(Clients(), clientsPage) {

    command(item = list) {
      action = {
        recursiveQuery()
      }
    }
    command(item = saveBlock) {
      action = {
        saveBlock()
      }
    }
  }

  val salesBlock = insertBlock(sales(), clientsPage) {

    command(item = showHideFilter) {
      action = {
        showHideFilter()
      }
    }

    command(item = list) {
      action = {
        recursiveQuery()
      }
    }
    command(item = saveBlock) {
      action = {
        saveBlock()
      }
    }
  }
}

class Clients : FormBlock(1, 1, "Clients") {

  val idClt = visit(domain = Domain<Int>(30), position = at(1, 1..2)) {
    label = "ID"
    help = "The client id"
  }
  val fstnameClt = visit(domain = Domain<String>(25), position = at(2, 1)) {
    label = "First Name"
    help = "The client first name"
  }
  val nameClt = visit(domain = Domain<String>(25), position = at(2, 2)) {
    label = "Last name"
    help = "The client last name"
  }
  val ageClt = visit(domain = Domain<Int>(3), position = at(2, 3)) {
    label = "Age"
    help = "The client age"
  }
  val email = visit(domain = Domain<String>(25), position = at(3, 1)) {
    label = "Email"
    help = "The mail adress"
  }
  val addressClt = visit(domain = Domain<String>(20), position = at(3, 2)) {
    label = "Address"
    help = "The client address"
  }
  val countryClt = visit(domain = Domain<String>(12), position = at(4, 1)) {
    label = "Country"
    help = "The client country"
  }
  val cityClt = visit(domain = Domain<String>(12), position = at(4, 2)) {
    label = "City"
    help = "The client city"
  }
  val zipCodeClt = visit(domain = Domain<Int>(12), position = follow(cityClt)) {
    label = "Zip code"
    help = "The client zip code"
  }
  val active = visit(domain = Domain<Boolean>(), position = at(5, 1)) {
    label = "Active ?"
    help = "Is the user active?"
  }

  init {
    nameClt[0] = "test"
  }
}

class sales : FormBlock(10, 10, "Sales") {

  val idClt = visit(domain = Domain<String>(5), position = at(1, 1..2)) {
    label = "ID"
    help = "The item id"
  }
  val description = visit(domain = Domain<String>(25), position = at(2, 1)) {
    label = "Description"
    help = "The item description"
  }
  val quantity = visit(domain = Domain<Int>(7), position = at(2, 2)) {
    label = "Quantity"
    help = "The number of items"
  }
  val price = visit(domain = Domain<Decimal>(10, 5), position = at(2, 2)) {
    label = "Price"
    help = "The item price"
  }

  init {
    border = VConstants.BRD_LINE
  }
}
