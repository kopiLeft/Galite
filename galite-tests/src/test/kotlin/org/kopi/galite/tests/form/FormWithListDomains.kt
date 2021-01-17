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

import org.jetbrains.exposed.sql.select
import org.kopi.galite.db.Users
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.AutoComplete
import org.kopi.galite.domain.ListDomain
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key

object FormWithListDomains: Form() {
  val edit = menu("Edit")
  val autoFill = actor(
          ident = "Autofill",
          menu = edit,
          label = "Autofill",
          help = "Autofill",
  )
  val newItem = actor(
          ident = "NewItem",
          menu = edit,
          label = "NewItem",
          help = "NewItem",
  )
  val editItem = actor(
          ident = "EditItem",
          menu = edit,
          label = "EditItem",
          help = "EditItem",
  )
  override val locale = Locale.FRANCE
  override val title = "form to test list domains"
  val userListBlock = insertBlock(UsersListBlock)
}

object UsersListBlock : FormBlock(1, 1, "UsersListBlock") {
  val user = mustFill(domain = UsersList, position = at(1, 1)) {
    label = "user"
    help = "The user"
  }
}

object UsersList: ListDomain<Int>(20) {

  override val table = query(
          Users.select {
            Users.id greater 0
          }
  )

  override val access = {
    SomeDictionnaryForm
  }

  val autoComplete = complete(AutoComplete.LEFT, 10)

  init {
    this["ID"] = Users.id
    this["UC"] = Users.uc
    this["TS"] = Users.ts
    this["KURZNAME"] = Users.shortName
    this["ZEICHEN"] = Users.character
    this["TELEFON"] = Users.phone
    this["EMAIL"] = Users.email
  }
}

object SomeDictionnaryForm : DictionaryForm() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val action = menu("Action")

  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F1   // key is optional here
    icon = "list"  // icon is optional here
  }

  val block = insertBlock(UsersBlock) {
    command(item = list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }
  }
}

fun main() {
  Application.runForm(formName = FormWithListDomains)
}
