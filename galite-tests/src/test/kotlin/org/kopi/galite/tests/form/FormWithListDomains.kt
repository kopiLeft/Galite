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

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.mod
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.rem
import org.kopi.galite.database.Modules
import org.kopi.galite.database.Users
import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.domain.*
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Key
import java.io.File
import java.util.*

class FormWithListDomains: Form(title = "form to test list domains", locale = Locale.UK) {
  val autoFill = actor(menu = editMenu, label = "Autofill", help = "Autofill", command = PredefinedCommand.AUTOFILL)
  override val newItem = actor(menu = editMenu, label = "NewItem", help = "NewItem", command = PredefinedCommand.NEW_ITEM)

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
  override fun accept(pathname: File?): Boolean {
    return (pathname!!.isDirectory
            || pathname.name.lowercase().endsWith(".xls")
            || pathname.name.lowercase().endsWith(".xlsx"))
  }

  override val description: String
    get() = "XLS/XLSX"
}

class UsersListBlock : Block("UsersListBlock", 1, 1) {
  val u = table(User)
  val user = mustFill(domain = UsersList(), position = at(1, 1)) {
    label = "user"
    help = "The user"
  }
  val module = mustFill(domain = Module(), position = at(2, 1)) {
    label = "module"
    help = "The module"
  }
  val name = visit(domain = Names, position = at(1, 1)) {
    label = "Names"
    columns(u.name) {
      priority = 1
    }
  }
  val age = visit(domain = AgesUsers, position = at(2, 1)) {
    label = "Age"
    columns(u.age) {
      priority = 2
    }
  }
  val job = visit(domain = Jobs, position = at(3, 1)) {
    label = "Job"
    columns(u.job) {
      priority = 3
    }
  }
}

class UsersList: ListDomain<Int>(20) {
  override val table = query(Users.select { Users.id greater 0 })
  override val access = { SomeDictionnaryForm() }
  val autoComplete = complete(AutoComplete.LEFT, 1)

  init {
    "ID"        keyOf Users.id
    "UC"        keyOf Users.uc
    "TS"        keyOf Users.ts
    "KURZNAME"  keyOf Users.shortName
    "ZEICHEN"   keyOf Users.character
    "TELEFON"   keyOf Users.phone
    "EMAIL"     keyOf Users.email
  }
}

class Module: ListDomain<String>(20) {
  override val table = Modules
  val autoComplete = complete(AutoComplete.LEFT, 2)

  init {
    "KURZNAME"    keyOf Modules.shortName
    "UC"          keyOf Modules.uc
    "ID"          keyOf Modules.id
    "TS"          keyOf Modules.ts
    "VATER"       keyOf Modules.parent
    "QUELLE"      keyOf Modules.sourceName
    "PRIORITAET"  keyOf Modules.priority
    "OBJEKT"      keyOf Modules.objectName
    "SYMBOL"      keyOf Modules.symbol
  }
}

object Names : ListDomain<String>(30) {
  override val table =  User

  init {
    "name"      keyOf User.name
    "uc + ts"   keyOf User.uc.plus(User.ts)
    "id"        keyOf User.id.rem(2)
  }
}

object AgesUsers : ListDomain<Int>(3) {
  override val table = AgesUsers.query(User.slice(User.age.minus(1).alias("age"),
                                                  User.name,
                                                  User.id.castTo<String>(VarCharColumnType()).alias("id"))
                                           .selectAll())

  init {
    "age"   keyOf User.age
    "name"  keyOf User.name
    "id"    keyOf User.id
  }
}

object Jobs : ListDomain<String>(20) {
  override val table =  User.alias("newUser")

  init {
    "job"   keyOf table[User.job]
    "cv"    keyOf table[User.cv].countDistinct()
    "id"    keyOf table[User.id].mod(5)
  }
}

class SomeDictionnaryForm : DictionaryForm(title = "form for test", locale = Locale.UK) {

  val autoFill = actor(menu = editMenu, label = "Autofill", help = "Autofill", command = PredefinedCommand.AUTOFILL)
  val quitCmd = command(item = quit) {
    quitForm()
  }
  val list = actor(menu = actionMenu, label = "list", help = "Display List", ) {
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

    val createdOn = visit(domain = DATETIME, position = at(2, 4)) {
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
