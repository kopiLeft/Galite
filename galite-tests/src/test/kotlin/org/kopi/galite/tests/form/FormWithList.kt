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

import org.joda.time.DateTime
import org.kopi.galite.common.POSTQRY
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
    key = Key.RESET   // key is optional here
    icon = "break"  // icon is optional here
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

  val block3 = insertBlock(UsersBlock, testPage1) {
    command(item = list) {
      this.name = "list"
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = resetBlock) {
      this.name = "break"
      action = {
        resetBlock()
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

  val block = insertBlock(BlockWithManyTables, testPage1)
}

object BlockSample : FormBlock(1, 1, "Test block") {
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

object UsersBlock : FormBlock(1, 1, "Test block") {
  val u = table(Users)
  init {
    trigger(POSTQRY) {
      BlockWithManyTables.uid[0] = id.value!!
      // BlockWithManyTables.load() TODO
    }
  }
  val id = hidden(domain = Domain<Int>(20)) {
    label = "id"
    help = "The user id"
    columns(BlockSample.u.id)
  }
  val uc = visit(domain = Domain<Int>(20), position = at(1, 2)) {
    label = "uc"
    help = "uc"
    columns(u.uc)
  }

  val ts = visit(domain = Domain<Int>(20), position = at(1, 3)) {
    label = "ts"
    help = "ts"
    columns(u.ts)
  }

  val Kurzname = visit(domain = Domain<String>(20), position = at(1, 4)) {
    label = "Kurzname"
    help = "Kurzname"
    columns(u.shortName)
  }

  val name = visit(domain = Domain<String>(20), position = at(2, 1)) {
    label = "name"
    help = "name"
    columns(u.name)
  }

  val character = visit(domain = Domain<String>(20), position = at(2, 2)) {
    label = "character"
    help = "character"
    columns(u.character)
  }

  val active = visit(domain = Domain<Boolean>(1), position = at(2, 3)) {
    label = "active"
    help = "active"
    columns(u.active)
  }

  val createdOn = visit(domain = Domain<DateTime>(20), position = at(2, 4)) {
    label = "createdOn"
    help = "createdOn"
    columns(u.createdOn)
  }

  val createdBy = visit(domain = Domain<Int>(10), position = at(2, 5)) {
    label = "createdBy"
    help = "createdBy"
    columns(u.createdBy)
  }

  val changedBy = visit(domain = Domain<Int>(10), position = at(2, 6)) {
    label = "changedBy"
    help = "changedBy"
    columns(u.changedBy)
  }
}

object BlockWithManyTables : FormBlock(20, 20, "Test block") {
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

  val module = visit(domain = Domain<Int>(20), position = at(1, 1)) {
    label = "module"
    help = "module"
    columns(r.module)
  }
}

fun main() {
  Application.runForm(formName = FormWithList)
}
