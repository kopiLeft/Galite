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

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.Mode
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.FileHandler

object User : Table() {
  val id = integer("ID")
  val uc = integer("UC")
  val ts = integer("TS")
  val name = varchar("NAME", 20).nullable()
  val age = integer("AGE").nullable()
  val job = varchar("JOB", 20).nullable()
  val cv = varchar("CURRICULUM VITAE", 70).nullable()
}

val userSequence = org.jetbrains.exposed.sql.Sequence("USERID", startWith = 1)

class FormSample : Form(title = "form for test", locale = Locale.UK) {

  val action = menu("Action")

  val edit = menu("Edit")

  val autoFill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )

  val editItemShortcut = actor(
    menu = edit,
    label = "Edit Item Shortcut",
    help = "Edit Item Shortcut",
    command = PredefinedCommand.EDIT_ITEM_SHORTCUT
  )

  val editItem = actor(
    menu = edit,
    label = "Edit Item",
    help = "Edit Item",
    command = PredefinedCommand.EDIT_ITEM
  )

  val newItem = actor(
    menu = edit,
    label = "New Item",
    help = "New Item",
    command = PredefinedCommand.NEW_ITEM
  )

  val graph = actor(
          menu = action,
          label = "Graph for test",
          help = "show graph values",
  ) {
    key = Key.F9
    icon = Icon.COLUMN_CHART
  }

  val formActor = actor(
          menu =   action,
          label =  "form Command",
          help =   "actor to test form command",
  ) {
    key  =  Key.F2
    icon =  Icon.SAVE
  }

  val cmd = command(item = formActor) {
    println("----------- FORM COMMAND ----------------")
  }

  val p1 = page("test page")
  val p2 = page("test page2")

  val tb1 = p1.insertBlock(TestBlock()) {
    command(item = graph, modes = arrayOf(Mode.UPDATE, Mode.INSERT, Mode.QUERY)) {
      println("---------------------------------- IN TEST COMMAND ----------------------------------" + tb2.age.value)
    }
  }

  val testBlock2 = block("Test block2", 1, 1) {

    val totalAge = visit(INT(3), position = at(1, 1)) {
      label = "Total"
      help = "total user age"
    }
  }

  val tb2 = p2.insertBlock(TestBlock()) {
    command(item = graph, Mode.UPDATE, Mode.INSERT, Mode.QUERY) {
        println("---------------------------------- IN TEST COMMAND ----------------------------------")
    }
  }

  val tb3ToTestBlockOptions = p1.insertBlock(TestBlock()) {
    options(BlockOption.NOINSERT)
  }

  val tb4ToTestChangeBlockAccess = p1.insertBlock(TestBlock()) {
    blockVisibility(Access.SKIPPED, Mode.QUERY, Mode.INSERT)
  }

  val tb4ToTestListDomain = p1.insertBlock(ListDomainTest())

  val preform = trigger(INIT) {
    println("init form trigger works")
  }

  val postform = trigger(POSTFORM) {
    println("post form trigger works")
  }

  init {
    tb1.age[0] = 5
    tb1.age.value = 6
  }
}

class TestBlock : Block("Test block", 1, 5) {
  val u = table(User)
  val i = index(message = "ID should be unique")

  val id = hidden(domain = INT(20)) {
    label = "id"
    help = "The user id"
    columns(u.id)
  }
  val ts = hidden(domain = INT(20)) {
    label = "ts"
    help = "The user ts"
    value = 0
    columns(u.ts)
  }
  val uc = hidden(domain = INT(20)) {
    label = "uc"
    help = "The user uc"
    value = 0
    columns(u.uc)
  }
  val name = visit(domain = STRING(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name) {
      priority = 1
    }
  }
  val password = mustFill(domain = STRING(20), position = at(2, 1)) {
    label = "password"
    help = "The user password"

    options(FieldOption.NOECHO)
    access {
      if (name.value == "hidden") {
        Access.HIDDEN
      } else {
        Access.SKIPPED
      }
    }
  }
  val age = visit(domain = INT(3) { min = 0; max = 90 }, position = follow(name)) {
    label = "age"
    help = "The user age"
    columns(u.age) {
      priority = 1
    }
    trigger(POSTCHG) {
      println("value changed !!")
      name.value = "Sami"
    }
  }
  val job = visit(domain = STRING(20), position = at(3, 1)) {
    label = "Job"
    help = "The user job"
    options(FieldOption.QUERY_UPPER)
    columns(u.job) {
      priority = 1
    }
  }
  val cv = visit(domain = STRING(20), position = at(4, 1)) {
    label = "Cv"
    help = "The user curriculum vitae"
    options(FieldOption.QUERY_LOWER)
    columns(u.cv)
    droppable("pdf")
    trigger(ACTION) {
      FileHandler.fileHandler!!.openFile(form.getDisplay()!!, object : FileHandler.FileFilter {
        override fun accept(pathname: File?): Boolean {
          return (pathname!!.isDirectory
                  || pathname.name.toLowerCase().endsWith(".pdf"))
        }

        override val description: String
          get() = "PDF"
      })
    }
  }
}

class ListDomainTest : Block("Test block", 1, 1) {
  val u = table(User)
  val listNames = visit(domain = ListNames, position = at(1, 1)) {
    label = "list names"
    columns(u.name) {
      priority = 1
    }
  }

  val listAges = visit(domain = ListAges, position = at(2, 1)) {
    label = "list ages"
    columns(u.age) {
      priority = 1
    }
  }

  val age = visit(domain = Ages, position = at(3, 1)) {
    label = "list ages"
    columns(u.age) {
      priority = 1
    }
  }
}

object ListNames : ListDomain<String>(30) {
  override val table =  User

  init {
    "name" keyOf User.name
  }
}

object ListAges : ListDomain<Int>(3) {
  override val table =  query(User.selectAll())

  init {
    "age" keyOf User.age
  }
}

object Ages : CodeDomain<Int>() {
  init {
    "cde1" keyOf 20
    "cde2" keyOf 30
    "cde3" keyOf 40
  }
}

fun main() {
  runForm(formName = FormSample())
}
