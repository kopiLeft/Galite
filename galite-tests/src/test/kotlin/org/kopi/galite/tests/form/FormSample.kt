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

import java.util.Locale

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.common.INITFORM
import org.kopi.galite.common.POSTFORM
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.field.ACCESS
import org.kopi.galite.field.ACTION
import org.kopi.galite.field.POSTCHG
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.FieldOption
import org.kopi.galite.form.dsl.BlockOption
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key

object User : Table() {
  val id = integer("ID")
  val uc = integer("UC")
  val ts = integer("TS")
  val name = varchar("NAME", 20).nullable()
  val age = integer("AGE").nullable()
  val job = varchar("JOB", 20).nullable()
}

object FormSample : Form() {
  override val locale = Locale.FRANCE
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
    key  =  Key.F9          // key is optional here
    icon =  "column_chart"  // icon is optional here
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

  val preform = trigger(INITFORM) {
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

class TestBlock : FormBlock(1, 1, "Test block") {
  val u = table(User)
  val i = index(message = "ID should be unique")

  val id = hidden(domain = Domain<Int>(20)) {
    label = "id"
    help = "The user id"
    columns(u.id)
  }
  val ts = hidden(domain = Domain<Int>(20)) {
    label = "ts"
    help = "The user ts"
    value = 0
    columns(u.ts)
  }
  val uc = hidden(domain = Domain<Int>(20)) {
    label = "uc"
    help = "The user uc"
    value = 0
    columns(u.uc)
  }
  val name = mustFill(domain = Domain<String?>(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name)
  }
  val password = mustFill(domain = Domain<String>(20), position = at(2, 1)) {
    label = "password"
    help = "The user password"

    options(FieldOption.NOECHO)
    trigger(ACCESS) {
      if (name.value == "hidden") {
        VConstants.ACS_HIDDEN
      } else {
        VConstants.ACS_SKIPPED
      }
    }
  }
  val age = visit(domain = Domain<Int?>(3), position = follow(name)) {
    label = "age"
    help = "The user age"
    minValue = 0
    maxValue =90
    columns(u.age) {
      index = i
      priority = 1
    }
    trigger(POSTCHG) {
      println("value changed !!")
      name.value = "Sami"
    }
  }
  val job = visit(domain = Domain<String?>(20), position = at(3, 1)) {
    label = "Job"
    help = "The user job"
    columns(u.job)
    trigger(ACTION) {
      println("Action on field !!")
    }
  }
}

fun main() {
  Application.runForm(formName = FormSample)
}
