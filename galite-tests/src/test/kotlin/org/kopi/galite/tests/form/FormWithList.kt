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
package org.kopi.galite.tests.form

import java.util.Locale

import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.visual.db.Modules
import org.kopi.galite.visual.db.UserRights
import org.kopi.galite.visual.db.Users
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TIMESTAMP
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key

class FormWithList : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "form for test"

  val action = menu("Action")
  val reset = menu("reset")
  val edit = menu("Edit")

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

  val autoFill = actor(
    ident = "Autofill",
    menu = edit,
    label = "Autofill",
    help = "Autofill",
  )

  val resetBlock = actor(
    ident = "reset",
    menu = reset,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F3
    icon = "break"
  }

  val resetForm = actor(
    ident = "resetForm",
    menu = reset,
    label = "resetForm",
    help = "Reset Form",
  ) {
    key = Key.F7
    icon = "break"
  }

  val resetFormCmd = command(item = resetForm) {
    action = {
      resetForm()
    }
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

  val block3 = insertBlock(UsersBlock(), testPage1) {
    command(item = list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = save) {
      action = {
        println("-----------Saving-----------------")
        saveBlock()
      }
    }
  }

  val block = insertBlock(BlockWithManyTables(), testPage1) {
    command(item = resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  inner class UsersBlock : FormBlock(1, 1, "Test block") {
    val u = table(Users)
    val unique = index(message = "ID should be unique")

    init {
      trigger(POSTQRY) {
        block.uid[0] = id.value
        block.load()
      }
    }
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

object BlockSample : FormBlock(1, 1, "Test block") {
  val u = table(Users)
  val m = table(Modules)
  val i = index(message = "ID should be unique")

  val id = hidden(domain = INT(20)) {
    label = "id"
    help = "The user id"
    columns(u.id) {
      index = i
    }
  }

  val name = visit(domain = STRING(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name) {
      index = i
      priority = 1
    }
  }
}

class BlockWithManyTables : FormBlock(20, 20, "Test block") {
  val u = table(Users)
  val m = table(Modules)
  val r = table(UserRights)
  val unique = index(message = "ID should be unique")

  val uid = hidden(domain = INT(20)) {
    label = "id"
    help = "The user id"
    columns(u.id, r.user) {
      index = unique
    }
  }

  val mid = hidden(domain = INT(20)) {
    label = "id"
    help = "The module id"
    columns(m.id, r.module)
  }

  val shortName = visit(domain = STRING(20), position = at(1, 1)) {
    label = "short name"
    help = "short name"
    columns(m.shortName)
  }

  val name = visit(domain = STRING(20), position = at(1, 2)) {
    label = "name"
    help = "name"
    columns(u.name)
  }
}

fun main() {
  runForm(formName = FormWithList()) {
    initModules()
  }
}
