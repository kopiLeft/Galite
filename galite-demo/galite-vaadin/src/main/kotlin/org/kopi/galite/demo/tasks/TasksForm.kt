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
package org.kopi.galite.demo.tasks

import java.util.Locale

import org.kopi.galite.demo.common.FormDefaultImpl
import org.kopi.galite.demo.common.IFormDefault
import org.kopi.galite.demo.database.Task
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TEXT
import org.kopi.galite.visual.dsl.form.FullCalendar
import org.kopi.galite.visual.dsl.form.ReportSelectionForm

class TasksForm : ReportSelectionForm(title = "Tasks", locale = Locale.UK), IFormDefault by FormDefaultImpl() {

  init {
    insertMenus()
    insertCommands()
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
