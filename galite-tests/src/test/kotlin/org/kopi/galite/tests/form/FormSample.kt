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

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.INT
import org.kopi.galite.domain.STRING
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.Access
import org.kopi.galite.form.dsl.BlockOption
import org.kopi.galite.form.dsl.FieldOption
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.Modes
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

val FormSample = FormSample_()
class FormSample_ : Form() {
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

  val graph = actor(
          ident = "graph",
          menu = action,
          label = "Graph for test",
          help = "show graph values",
  ) {
    key = Key.F9          // key is optional here
    icon = "column_chart"  // icon is optional here
  }

  val formActor = actor(
          ident =  "save",
          menu =   action,
          label =  "form Command",
          help =   "actor to test form command",
  ) {
    key  =  Key.F2          // key is optional here
    icon =  "save"  // icon is optional here
  }

  val cmd = command(item = formActor) {
    action = {
      println("----------- FORM COMMAND ----------------")
    }
  }

  val p1 = page("test page")
  val p2 = page("test page2")

  val tb1 = insertBlock(TestBlock(), p1) {
    command(item = graph) {
      mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
      action = {
        println("---------------------------------- IN TEST COMMAND ----------------------------------" + tb2.age.value)
      }
    }
  }

  val testBlock2 = block(1, 1, "Test2", "Test block2") {

    val totalAge = visit(INT(3), position = at(1, 1)) {
      label = "Total"
      help = "total user age"
    }
  }

  val tb2 = insertBlock(TestBlock(), p2) {
    command(item = graph) {
      mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
      action = {
        println("---------------------------------- IN TEST COMMAND ----------------------------------")
      }

    }
  }

  val tb3ToTestBlockOptions = insertBlock(TestBlock(), p1) {
    options(BlockOption.NOINSERT)
  }

  val tb4ToTestChangeBlockAccess = insertBlock(TestBlock(), p1) {
    blockVisibility(Access.SKIPPED, Modes.QUERY, Modes.INSERT)
  }

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

class TestBlock : FormBlock(1, 5, "Test block") {
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
    trigger(ACCESS) {
      if (name.value == "hidden") {
        Access.HIDDEN
      } else {
        Access.SKIPPED
      }
    }
  }
  val age = visit(domain = INT(3), position = follow(name)) {
    label = "age"
    help = "The user age"
    minValue = 0
    maxValue = 90
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
      FileHandler.fileHandler!!.openFile(form.model.getDisplay()!!, object : FileHandler.FileFilter {
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

fun main() {
  Application.runForm(formName = FormSample)
}
