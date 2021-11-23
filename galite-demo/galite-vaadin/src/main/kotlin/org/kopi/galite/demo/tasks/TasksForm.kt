/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.demo.database.Task
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TEXT
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.FullCalendarBlock
import org.kopi.galite.visual.dsl.form.Key

class TasksForm : Form() {
  override val locale = Locale.UK
  override val title = "Tasks"
  val action = menu("Action")
  val quit = actor(
    ident = "quit",
    menu = action,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE          // key is optional here
    icon = "quit"  // icon is optional here
  }
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "report"  // icon is optional here
  }

  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F10
    icon = "list"  // icon is optional here
  }

  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )

  val helpForm = actor(
          ident = "helpForm",
          menu = action,
          label = "Help",
          help = " Help"
  ) {
    key = Key.F1
    icon = "help"
  }

  val helpCmd = command(item = helpForm) {
    action = {
      showHelp()
    }
  }
  val quitCmd = command(item = quit) {
    action = {
      quitForm()
    }
  }

  val tasksBlock = insertBlock(Tasks())

  inner class Tasks : FullCalendarBlock("Tasks") {
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
