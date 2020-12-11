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

import org.jetbrains.exposed.sql.SchemaUtils
import java.util.Locale

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.common.INITFORM
import org.kopi.galite.common.POSTFORM
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FieldOption
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock

object FormObject: Form() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val action = menu("Action")
  val p1 = page("test page")
  val tb3ToTestBlockOptions = insertBlock(BlockTest(), p1)
  val preform = trigger(INITFORM) {
    println("init form trigger works")
  }

  val postform = trigger(POSTFORM) {
    println("post form trigger works")
  }

}

class BlockTest : FormBlock(1, 1, "Test", "Test block") {
  val userTable = table(User)
  val index = index(message = "ID should be unique")

  val id = hidden(domain = Domain<Int>(20)) {
    label = "id"
    help = "The user id"
    columns(userTable.id)
  }
  val name = mustFill(domain = Domain<String>(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(userTable.name)
  }
  val password = mustFill(domain = Domain<String>(20), position = at(2, 1)) {
    label = "password"
    help = "The user password"
    options(FieldOption.NOECHO)
  }
  val age = visit(domain = Domain<Int>(3), position = follow(name)) {
    label = "age"
    help = "The user age"
    columns(userTable.age) {
      index = index
      priority = 1
    }
  }

  init {
    Application.connectToDatabase()
    transaction {
      SchemaUtils.create(User)

      userTable.insert {
        it[id] = 0
        it[name] = "user0"
        it[age] = 23
      }
      userTable.insert {
        it[id] = 1
        it[name] = "user1"
        it[age] = 23
      }
      userTable.insert {
        it[id] = 2
        it[name] = "user2"
        it[age] = 26
      }
      userTable.insert {
        it[id] = 3
        it[name] = "user3"
        it[age] = 25
      }
      userTable.insert {
        it[id] = 0
        it[name] = "user0"
        it[age] = 23
      }
    }
  }
}

fun main(){
  Application.runForm(formName = FormObject)

  val suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("A*")
  FormSample.model.getBlock(0).fields[1].list!!.autocompleteType = 1
  println(suggestionsResult)
}
