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

import java.io.File
import java.util.Locale

import org.jetbrains.exposed.sql.select
import org.kopi.galite.db.Modules
import org.kopi.galite.db.Users
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.AutoComplete
import org.kopi.galite.domain.Domain
import org.kopi.galite.domain.ListDomain
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.visual.FileHandler

class FormWithListDomains: Form() {
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
  override val locale = Locale.UK
  override val title = "form to test list domains"
  val userListBlock = insertBlock(UsersListBlock()) {

    val file = visit(domain = Domain<String>(25), position = at(3, 1)) {
      label = "test"
      help = "The test"
      command(item = autoFill) {
        action = {

          val file = FileHandler.fileHandler!!.openFile(model.getDisplay()!!, FileFilter());
          if (file != null) {
            value = file.absolutePath
          }
        }
      }
    }
  }
}

class FileFilter : FileHandler.FileFilter {
  override fun accept(f: File?): Boolean {
    return (f!!.isDirectory
            || f.name.toLowerCase().endsWith(".xls")
            || f.name.toLowerCase().endsWith(".xlsx"))
  }

  override val description: String
    get() = "XLS/XLSX"
}

class UsersListBlock : FormBlock(1, 1, "UsersListBlock") {
  val user = mustFill(domain = UsersList(), position = at(1, 1)) {
    label = "user"
    help = "The user"
  }
  val module = mustFill(domain = Module(), position = at(2, 1)) {
    label = "module"
    help = "The module"
  }
}
class UsersList: ListDomain<Int>(20) {

  override val table = query(
          Users.select {
            Users.id greater 0
          }
  )

  override val access = {
    SomeDictionnaryForm()
  }

  val autoComplete = complete(AutoComplete.LEFT, 1)

  init {
    "ID" keyOf Users.id
    "UC" keyOf Users.uc
    "TS" keyOf Users.ts
    "KURZNAME" keyOf Users.shortName
    "ZEICHEN" keyOf Users.character
    "TELEFON" keyOf Users.phone
    "EMAIL" keyOf Users.email
  }
}

class Module: ListDomain<String>(20) {

  override val table = Modules

  val autoComplete = complete(AutoComplete.LEFT, 2)

  init {
    "KURZNAME" keyOf Modules.shortName
    "UC" keyOf Modules.uc
    "ID" keyOf Modules.id
    "TS" keyOf Modules.ts
    "VATER" keyOf Modules.parent
    "QUELLE" keyOf Modules.sourceName
    "PRIORITAET" keyOf Modules.priority
    "OBJEKT" keyOf Modules.objectName
    "SYMBOL" keyOf Modules.symbol
  }
}
class SomeDictionnaryForm : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "form for test"

  val action = menu("Action")

  val edit = menu("Edit")
  val autoFill = actor(
    ident = "Autofill",
    menu = edit,
    label = "Autofill",
    help = "Autofill",
  )

  val quit = actor(
    ident = "quit",
    menu = action,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = "quit"
  }
  val quitCmd = command(item = quit) {
    action = {
      quitForm()
    }
  }
  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F1   // key is optional here
    icon = "list"  // icon is optional here
  }

  val block = insertBlock(UsersBlock()) {
    command(item = list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }
  }
}

fun main() {
  Application.runForm(formName = FormWithListDomains()) {
    initModules()
  }
}
