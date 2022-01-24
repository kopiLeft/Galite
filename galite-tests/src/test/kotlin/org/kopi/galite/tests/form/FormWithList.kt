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

import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.visual.db.Modules
import org.kopi.galite.visual.db.UserRights
import org.kopi.galite.visual.db.Users
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TIMESTAMP
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key

class FormWithList : DictionaryForm(title = "form for test", locale = Locale.UK) {

  val action = menu("Action")
  val reset = menu("reset")
  val edit = menu("Edit")

  val testPage1 = page("test page1")
  val testPage2 = page("test page2")

  val list = actor(
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F1
    icon = Icon.LIST
  }

  val autoFill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )

  val resetBlock = actor(
    menu = reset,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F3
    icon = Icon.BREAK
  }

  val resetForm = actor(
    menu = reset,
    label = "resetForm",
    help = "Reset Form",
  ) {
    key = Key.F7
    icon = Icon.BREAK
  }

  val resetFormCmd = command(item = resetForm) {
    resetForm()
  }

  val save = actor(
    menu = action,
    label = "save",
    help = "save",
  ) {
    key = Key.F2
    icon = Icon.SAVE
  }

  val block3 = testPage1.insertBlock(UsersBlock()) {
    command(item = list) {
      recursiveQuery()
    }

    command(item = save) {
      saveBlock()
    }
  }

  val blockWithManyTables = testPage1.insertBlock(BlockWithManyTables()) {
    command(item = resetBlock) {
      resetBlock()
    }
  }

  inner class UsersBlock : Block("Test block", 1, 1) {
    val u = table(Users)
    val unique = index(message = "ID should be unique")

    init {
      trigger(POSTQRY) {
        blockWithManyTables.uid[0] = id.value
        blockWithManyTables.load()
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

object BlockSample : Block("Test block", 1, 1) {
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

class BlockWithManyTables : Block("Test block", 20, 20) {
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
