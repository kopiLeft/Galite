/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.db.Modules
import org.kopi.galite.visual.db.Users
import org.kopi.galite.visual.domain.AutoComplete
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TIMESTAMP
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.FileHandler

class FormWithListDomains: Form(title = "form to test list domains", locale = Locale.UK) {
  val edit = menu("Edit")
  val autoFill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )
  val newItem = actor(
    menu = edit,
    label = "NewItem",
    help = "NewItem",
    command = PredefinedCommand.NEW_ITEM
  )
  val editItem = actor(
    menu = edit,
    label = "EditItem",
    help = "EditItem",
    command = PredefinedCommand.EDIT_ITEM
  )

  val userListBlock = insertBlock(UsersListBlock()) {

    val file = visit(domain = STRING(25), position = at(3, 1)) {
      label = "test"
      help = "The test"
      command(item = autoFill) {
        val file = FileHandler.fileHandler!!.openFile(model.getDisplay()!!, FileFilter());
        if (file != null) {
          value = file.absolutePath
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

class UsersListBlock : Block("UsersListBlock", 1, 1) {
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
class SomeDictionnaryForm : DictionaryForm(title = "form for test", locale = Locale.UK) {

  val action = menu("Action")

  val edit = menu("Edit")
  val autoFill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )

  val quit = actor(
    menu = action,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }
  val quitCmd = command(item = quit) {
    quitForm()
  }
  val list = actor(
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F1
    icon = Icon.LIST
  }

  val block = insertBlock(UsersBlock()) {
    command(item = list) {
      recursiveQuery()
    }
  }

  inner class UsersBlock : Block("Test block", 1, 1) {
    val u = table(Users)
    val unique = index(message = "ID should be unique")

    val id = hidden(domain = INT(20)) {
      label = "ID"
      help = "The user id"
      columns(u.id) {
        index = unique
      }
    }
    val uc = visit(domain = INT(20), position = at(1, 2)) {
      label = "UC"
      help = "uc"
      columns(u.uc)
    }

    val ts = visit(domain = INT(20), position = at(1, 3)) {
      label = "TS"
      help = "ts"
      columns(u.ts)
    }

    val shortName = visit(domain = STRING(20), position = at(1, 4)) {
      label = "Kurzname"
      help = "Kurzname"
      columns(u.shortName)
    }

    val name = visit(domain = STRING(20), position = at(2, 1)) {
      label = "name"
      help = "name"
      columns(u.name) {
        priority = 1
      }
    }

    val character = visit(domain = STRING(20), position = at(2, 2)) {
      label = "character"
      help = "character"
      columns(u.character)
    }

    val active = visit(domain = BOOL, position = at(2, 3)) {
      label = "active"
      help = "active"
      columns(u.active)
    }

    val createdOn = visit(domain = TIMESTAMP, position = at(2, 4)) {
      label = "createdOn"
      help = "createdOn"
      columns(u.createdOn)
    }

    val createdBy = visit(domain = INT(10), position = at(2, 5)) {
      label = "createdBy"
      help = "createdBy"
      columns(u.createdBy)
    }

    val changedBy = visit(domain = INT(10), position = at(2, 6)) {
      label = "changedBy"
      help = "changedBy"
      columns(u.changedBy)
    }
  }
}

fun main() {
  runForm(formName = FormWithListDomains())
}
