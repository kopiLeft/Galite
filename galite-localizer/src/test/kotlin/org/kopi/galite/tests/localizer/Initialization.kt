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
package org.kopi.galite.tests.localizer

import java.util.Locale

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.database.Modules
import org.kopi.galite.database.list_Of_Tables
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.Mode
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.Block
import org.kopi.galite.visual.dsl.form.Key

const val testURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
const val testDriver = "org.h2.Driver"
const val testUser = "admin"
const val testPassword = "admin"

fun initDatabase() {
  Database.connect(testURL, driver = testDriver, user = testUser, password = testPassword)
  transaction {
    list_Of_Tables.forEach { table ->
      SchemaUtils.drop(table)
    }
    list_Of_Tables.forEach { table ->
      SchemaUtils.create(table)
    }

    insertIntoModule("1000", "org/kopi/galite/test/localizer/Menu", 0)
    insertIntoModule("1001", "org/kopi/galite/test/localizer/Menu", 1, "1000", FormSample::class)
  }
}

fun insertIntoModule(shortname: String,
                     source: String,
                     priorityNumber: Int,
                     parentName: String = "-1",
                     className: KClass<*>? = null,
                     symbolNumber: Int? = null) {

  Modules.insert {
    it[uc] = 0
    it[ts] = 0
    it[shortName] = shortname
    it[parent] = if (parentName != "-1") Modules.select { shortName eq parentName }.single()[id] else -1
    it[sourceName] = source
    it[priority] = priorityNumber
    it[objectName] = if (className != null) className.qualifiedName!! else null
    it[symbol] = symbolNumber
  }
}

object User : Table() {
  val id = integer("ID")
  val uc = integer("UC")
  val ts = integer("TS")
  val name = varchar("NAME", 20).nullable()
  val age = integer("AGE").nullable()
  val job = varchar("JOB", 20).nullable()
  val cv = varchar("CURRICULUM VITAE", 70).nullable()
}

class FormSample : Form(title = "form for test", locale = Locale.UK) {

  val action = menu("Action")

  val edit = menu("Edit")

  val autoFill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
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

  val cmd = command(item = formActor) {}

  val p1 = page("test page")
  val p2 = page("test page2")

  val tb1 = p1.insertBlock(TestBlock())

  val testBlock2 = block("Test block2", 1, 1) {

    val totalAge = visit(INT(3), position = at(1, 1)) {
      label = "Total"
      help = "total user age"
    }
  }

  val tb2 = p2.insertBlock(TestBlock())

  val preform = trigger(INIT) {
    println("init form trigger works")
  }

  val postform = trigger(POSTFORM) {
    println("post form trigger works")
  }

  inner class TestBlock : Block("Test block", 1, 5) {
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
      access { Access.SKIPPED }
    }
    val age = visit(domain = INT(3) { min = 0; max = 90 }, position = follow(name)) {
      label = "age"
      help = "The user age"
      columns(u.age) {
        priority = 1
      }
      trigger(POSTCHG) {}
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
      droppable("pdf")
      trigger(ACTION) {}
    }

    init {
      command(item = graph, Mode.UPDATE, Mode.INSERT, Mode.QUERY) { }
    }
  }
}
