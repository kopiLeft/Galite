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
package org.kopi.galite.demo.newCommands

import java.util.Locale

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.TaxRule
import org.kopi.galite.demo.taxRule.TaxRuleBlock
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Access
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.Modes

object NewCommandsForm : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "TaxRules"
  val page = page("TaxRule")
  val page2 = page("page2")
  val page3 = page("page3")
  val action = menu("Action")

  val autoFill = actor(
          ident = "Autofill",
          menu = action,
          label = "Autofill",
          help = "Autofill",
  )

  val listMove = actor(
    ident = "listMove",
    menu = action,
    label = "listMove",
    help = "Display List",
  ) {
    key = Key.F1   // key is optional here
    icon = "list"  // icon is optional here
  }

  val changeBlock = actor(
    ident = "changeBlock",
    menu = action,
    label = "changeBlock",
    help = " change Block",
  ) {
    key = Key.F2
    icon = "block"
  }

  val searchOperator = actor(
    ident = "SearchOperator",
    menu = action,
    label = "SearchOperator",
    help = " Search Operator",
  ) {
    key = Key.F3
    icon = "searchop"
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

  val insertLine = actor(
          ident = "insertline",
          menu = action,
          label = "insertline",
          help = " insertline",
  ) {
    key = Key.F5      // key is optional here
    icon = "insertline"  // icon is optional here
  }

  val insertMode = actor(
          ident = "insertMode",
          menu = action,
          label = "insertMode",
          help = " insertMode",
  ) {
    key = Key.F6     // key is optional here
    icon = "insert"  // icon is optional here
  }

  val save = actor(
          ident = "save",
          menu = action,
          label = "save",
          help = "save",
  ) {
    key = Key.F2   // key is optional here
    icon = "save"  // icon is optional here
  }

  val block = insertBlock(premierBlock(), page) {

    command(item = listMove) {
      action = {
        println("-----------queryMove list-----------------")
        queryMove()
      }
    }

    command(item = changeBlock) {
      action = {
        changeBlock()
      }
    }

    command(item = searchOperator) {
      action = {
        searchOperator()
      }
    }

    command(item = insertMode) {
      action = {
        insertMode()
      }
    }
    command(item = save) {
      action = {
        println("-----------Saving-----------------")
        saveBlock()
      }
    }

  }

  val block2 = insertBlock(deuxiemeBlock(), page2)
  val block3 = insertBlock(troisiemeBlock(), page3) {
    command(item = insertLine) {
      action = {
        insertLine()
      }
    }

    command(item = showHideFilter) {
      action = {
        showHideFilter()
      }
    }
  }
}

class premierBlock : FormBlock(1, 10, "premierBlock") {
  val u = table(TaxRule)

  val idTaxe = hidden(domain = Domain<Int>(20)) {
    label = "ID"
    help = "The tax ID"
    columns(TaxRuleBlock.u.idTaxe)
    command(item = NewCommandsForm.showHideFilter) {
      action = {
        println("IN FIELDDDDDDDD")
      }
    }
  }

  val field1 = mustFill(domain = Domain<String>(20), position = at(1, 1)) {
    label = "field 1"
    help = "field1"
    columns(u.taxName) {
      priority = 1
    }
  }

  val field2 = mustFill(domain = Domain<Int>(25), position = at(2, 1)) {
    label = "field 2"
    help = "field2"
    columns(u.rate) {
      priority = 1
    }
  }

  init {
    blockVisibility(Access.VISIT, Modes.QUERY)
  }


}

class deuxiemeBlock : FormBlock(1, 10, "deuxiemeBlock") {
  val total = visit(domain = Domain<String>(25), position = at(1, 1)) {
    label = "total"
    help = "The total"
  }
}

class troisiemeBlock : FormBlock(6, 6, "troisiemeBlock") {
  val code = visit(domain = Domain<String>(25), position = at(1, 1)) {
    label = "Code"
    help = "The Code"
  }
  val fstnameClt = visit(domain = Domain<String>(25), position = at(2, 1)) {
    label = "First Name"
    help = "The client first name"
  }
  val nameClt = visit(domain = Domain<String>(25), position = at(3, 1)) {
    label = "Last name"
    help = "The client last name"
  }
}

fun main() {
  Application.runForm(formName = NewCommandsForm)
}
