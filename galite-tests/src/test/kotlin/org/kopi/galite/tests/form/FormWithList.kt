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
package org.kopi.galite.tests.form
import java.util.Locale

import org.kopi.galite.db.Modules
import org.kopi.galite.db.UserRights
import org.kopi.galite.db.Users
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key

object FormWithList : DictionaryForm() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val action = menu("Action")
  val testPage1 = page("test page1")
  val testPage2 = page("test page2")

  val list = actor(
    ident = "list",
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F1   // key is optional here
    icon = "list"  // icon is optional here
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

  val block = insertBlock(BlockWithManyTables, testPage1) {
    command(item = list) {
      this.name = "list"
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = save) {
      this.name = "save"
      action = {
        println("-----------Saving-----------------")
        saveBlock()
      }
    }
  }

  val block2 = insertBlock(BlockSample, testPage2) {
    command(item = list) {
      this.name = "list"
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }
  }
}

object BlockSample : FormBlock(1, 1, "BlockSample", "Test block") {
  val u = table(Users)
  val i = index(message = "ID should be unique")

  val id = hidden(domain = Domain<Int>(20)) {
    label = "id"
    help = "The user id"
    columns(u.id)
  }

  val name = visit(domain = Domain<String>(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name) {
      priority = 1
    }
  }
}

object BlockWithManyTables : FormBlock(1, 20, "BlockWithManyTables", "Test block") {
  val u = table(Users)
  val m = table(Modules)
  val r = table(UserRights)

  val uid = hidden(domain = Domain<Int>(20)) {
    label = "id"
    help = "The user id"
    columns(u.id, r.user)
  }

  val mid = hidden(domain = Domain<Int>(20)) {
    label = "id"
    help = "The module id"
    columns(m.id, r.module)
  }

  val name = visit(domain = Domain<String>(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name) {
      priority = 1
    }
  }
}

fun main() {
  Application.runForm(formName = FormWithList)
}
