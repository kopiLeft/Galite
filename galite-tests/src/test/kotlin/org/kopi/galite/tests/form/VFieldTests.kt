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
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.common.INITFORM
import org.kopi.galite.common.POSTFORM
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VPosition
import org.kopi.galite.form.dsl.FieldOption
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.VCommand
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

object FormObject : Form() {
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
        it[name] = "AUDREY"
        it[age] = 23
      }
      userTable.insert {
        it[id] = 1
        it[name] = "BARBEY"
        it[age] = 23
      }
      userTable.insert {
        it[id] = 2
        it[name] = "BARBEY1"
        it[age] = 26
      }
      userTable.insert {
        it[id] = 3
        it[name] = "Fabienne BUGHIN"
        it[age] = 25
      }
      userTable.insert {
        it[id] = 4
        it[name] = "FABIENNE BUGHIN2"
        it[age] = 23
      }
      userTable.insert {
        it[id] = 5
        it[name] = "USER0"
        it[age] = 23
      }
      userTable.insert {
        it[id] = 6
        it[name] = "user1"
        it[age] = 23
      }
      userTable.insert {
        it[id] = 7
        it[name] = "user2"
        it[age] = 23
      }
    }
  }
}

class VFieldTests : JApplicationTestBase() {
  init {
    Application.runForm(formName = FormObject)
    val vListColumn = VList("Benutzer",
            "apps/common/Global", arrayOf<VListColumn>(
            VStringColumn(null, "NAME", 2, 50, true)),
            1,
            -1,
            0,
            0,
            null,
            false)

    FormSample.model.getBlock(0).fields[1].setInfo("name", 1, -1, 0, intArrayOf(4, 4, 4),
            vListColumn,
            arrayOf<VColumn?>(
                    VColumn(0, "NAME", false, false, User.name)
            ),
            0, 0, null as Array<VCommand>?, VPosition(1, 1, 2, 2, -1), 2,
            null)

    FormSample.model.getBlock(0).fields[1].list = vListColumn
  }

    @Test
    fun autoCompleteStartsWithTest() {
      FormSample.model.getBlock(0).fields[1].list!!.autocompleteType = 1
      var suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("AUD")
      var suggestionsResultList = suggestionsResult!!.map {
        it[0]
      }
      println("Starts with AUD")
      println(suggestionsResultList)


      var expectedList = mutableListOf<String>("AUDREY")
      assertCollectionsEquals(expectedList , suggestionsResultList)

      suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("BarB")
      suggestionsResultList = suggestionsResult!!.map {
        it[0]
      }
      println("Starts with BarB")
      println(suggestionsResultList)

      expectedList = mutableListOf<String>("BARBEY" , "BARBEY1")

      assertCollectionsEquals(expectedList , suggestionsResultList)

      suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("BARBEY")
      suggestionsResultList = suggestionsResult!!.map {
        it[0]
      }
      println("Starts with BARBEY")
      println(suggestionsResultList)

      expectedList = mutableListOf<String>("BARBEY" , "BARBEY1")

      assertCollectionsEquals(expectedList , suggestionsResultList)

      suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("User")
      suggestionsResultList = suggestionsResult!!.map {
        it[0]
      }
      println("Starts with User")
      println(suggestionsResultList)

      expectedList = mutableListOf<String>("USER0" ,"user1" , "user2")

      assertCollectionsEquals(expectedList , suggestionsResultList)
    }

    @Test
    fun autoCompleteContainsTest() {
      FormSample.model.getBlock(0).fields[1].list!!.autocompleteType = 2
      var suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("A")
      var suggestionsResultList = suggestionsResult!!.map {
        it[0]
      }
      println("Contains A")
      println(suggestionsResultList)
      var expectedList = mutableListOf( "AUDREY" , "BARBEY" , "BARBEY1" , "Fabienne BUGHIN" , "FABIENNE BUGHIN2")

      assertCollectionsEquals(expectedList , suggestionsResultList)

       suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("s")
       suggestionsResultList = suggestionsResult!!.map {
        it[0]
      }
      println("Contains s")
      println(suggestionsResultList)

      expectedList = mutableListOf("USER0" , "user1" , "user2")

      assertCollectionsEquals(expectedList , suggestionsResultList)

       suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("0")
       suggestionsResultList = suggestionsResult!!.map {
        it[0]
      }
      println("Contains 0")
      println(suggestionsResultList)
       expectedList = mutableListOf("USER0")

      assertCollectionsEquals(expectedList , suggestionsResultList)

    }
}