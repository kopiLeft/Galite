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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm

class CommandsForm : ReportSelectionForm(title = "Commands Form", locale = Locale.UK) {
  val action = menu("Action")
  val autoFill = actor(
    menu = action,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )
  val list = actor(
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F2
    icon = Icon.LIST
  }
  val resetBlock = actor(
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F11
    icon = Icon.BREAK
  }
  val serialQuery = actor(
    menu = action,
    label = "serialQuery",
    help = "serial query",
  ) {
    key = Key.F6
    icon = Icon.SERIAL_QUERY
  }
  val report = actor(
    menu = action,
    label = "CreateReport",
    help = "Create report",
  ) {
    key = Key.F8
    icon = Icon.REPORT
  }
  val dynamicReport = actor(
    menu = action,
    label = "DynamicReport",
    help = " Create Dynamic Report",
  ) {
    key = Key.F9
    icon = Icon.REPORT
  }
  val saveBlock = actor(
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = Icon.SAVE
  }
  val deleteBlock = actor(
    menu = action,
    label = "deleteBlock",
    help = " deletes block",
  ) {
    key = Key.F4
    icon = Icon.DELETE
  }
  val Operator = actor(
    menu = action,
    label = "search",
    help = " search",
  ) {
    key = Key.F7
    icon = Icon.DETAIL_VIEW
  }
  val InsertMode = actor(
    menu = action,
    label = "Insert",
    help = " Insert",
  ) {
    key = Key.F7
    icon = Icon.INSERT
  }
  val pivottable = actor(
    menu = action,
    label = "Pivot table",
    help = " Pivot table",
  ) {
    key = Key.F8
    icon = Icon.REPORT
  }
  val quit = actor(
    menu = action,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }
  val helpForm = actor(
    menu = action,
    label = "Help",
    help = " Help"
  ) {
    key = Key.F1
    icon = Icon.HELP
  }
  val helpCmd = command(item = helpForm) {
    showHelp()
  }
  val quitCmd = command(item = quit) {
    quitForm()
  }

  val block = insertBlock(Traineeship()) {
    command(item = list) {
      recursiveQuery()
    }
    command(item = resetBlock) {
      resetBlock()
    }
    command(item = serialQuery) {
      serialQuery()
    }
    command(item = report) {
      createReport {
        TrainingR()
      }
    }
    command(item = dynamicReport) {
      createDynamicReport()
    }
    command(item = saveBlock) {
      saveBlock()
    }
    command(item = deleteBlock) {
      deleteBlock()
    }
    command(item = Operator) {
      searchOperator()
    }
    command(item = InsertMode) {
      insertMode()
    }
    command(item = pivottable) {
      PivotTableExample()
    }
  }
}

fun main() {
  runForm(formName = CommandsForm())
}
