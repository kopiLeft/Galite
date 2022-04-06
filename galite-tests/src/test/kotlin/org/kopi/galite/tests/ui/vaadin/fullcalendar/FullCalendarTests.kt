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
package org.kopi.galite.tests.ui.vaadin.fullcalendar

import java.util.Locale

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.open
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TEXT
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.FullCalendar
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm

import com.github.mvysny.kaributesting.v10._get

class FullCalendarTests: GaliteVUITestBase() {

  val form = TasksForm()

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
  }

  @Test
  fun `test full calendar is displayed`() {
    _get<org.vaadin.stefan.fullcalendar.FullCalendar>()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        initModules()
      }
    }
  }
}

class TasksForm : ReportSelectionForm(title = "Tasks", locale = Locale.UK) {
  val edit = menu("Edit")

  val autofill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  ) {
    key = Key.F2
  }

  val tasksBlock = insertBlock(Tasks())

  inner class Tasks : FullCalendar("Tasks") {
    val t = table(Task)

    val id = hidden(INT(20)) { columns(t.id) }
    val uc = hidden(domain = INT(20)) { columns(t.uc) }
    val ts = hidden(domain = INT(20)) { columns(t.ts) }

    val from = from(position = at(1, 1)) {
      label = "From"
      columns(t.from)
    }
    val to = to(position = at(2, 1)) {
      label = "To"
      columns(t.to)
    }
    val description1 = mustFill(domain = STRING(20), position = at(3, 1)) {
      label = "description1"
      columns(t.description1) {
        priority = 1
      }
    }
    val description2 = mustFill(domain = TEXT(20, 5), position = at(4, 1)) {
      label = "description2"
      columns(t.description2)
    }
  }
}

object Task : Table("Task") {
  val id = integer("ID").autoIncrement()
  val date = date("DATE").nullable()
  val from = timestamp("FROM")
  val to = timestamp("TO")
  val description1 = varchar("DESCRIPTION_1", 20)
  val description2 = varchar("DESCRIPTION_2", 20)
  val uc = integer("UC").autoIncrement()
  val ts = integer("TS").autoIncrement()

  override val primaryKey = PrimaryKey(id)
}
