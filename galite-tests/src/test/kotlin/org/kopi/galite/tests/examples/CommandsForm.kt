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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.connectToDatabase
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.dsl.report.Report

class CommandsForm : ReportSelectionForm() {
  override val locale = Locale.UK
  override fun createReport(): Report {
    return TrainingR()
  }

  override val title = "Commands Form"
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )
  val list = actor(
    ident = "list",
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F2
    icon = "list"
  }
  val resetBlock = actor(
    ident = "reset",
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F11
    icon = "break"
  }
  val serialQuery = actor(
    ident = "serialQuery",
    menu = action,
    label = "serialQuery",
    help = "serial query",
  ) {
    key = Key.F6
    icon = "serialquery"
  }
  val report = actor(
    ident = "report",
    menu = action,
    label = "CreateReport",
    help = "Create report",
  ) {
    key = Key.F8
    icon = "report"
  }
  val dynamicReport = actor(
    ident = "dynamicReport",
    menu = action,
    label = "DynamicReport",
    help = " Create Dynamic Report",
  ) {
    key = Key.F9
    icon = "report"
  }
  val saveBlock = actor(
    ident = "saveBlock",
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = "save"
  }
  val deleteBlock = actor(
    ident = "deleteBlock",
    menu = action,
    label = "deleteBlock",
    help = " deletes block",
  ) {
    key = Key.F4
    icon = "delete"
  }
  val Operator = actor(
    ident = "search",
    menu = action,
    label = "search",
    help = " search",
  ) {
    key = Key.F7
    icon = "detail_view"
  }
  val InsertMode = actor(
    ident = "Insert",
    menu = action,
    label = "Insert",
    help = " Insert",
  ) {
    key = Key.F7
    icon = "insert"
  }
  val quit = actor(
    ident = "quit",
    menu = action,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = "quit"
  }
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

  val block = insertBlock(Traineeship()) {
    command(item = list) {
      action = {
        recursiveQuery()
      }
    }
    command(item = resetBlock) {
      action = {
        resetBlock()
      }
    }
    command(item = serialQuery) {
      action = {
        serialQuery()
      }
    }
    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }
    command(item = dynamicReport) {
      action = {
        createDynamicReport()
      }
    }
    command(item = saveBlock) {
      action = {
        saveBlock()
      }
    }
    command(item = deleteBlock) {
      action = {
        deleteBlock()
      }
    }
    command(item = Operator) {
      action = {
        searchOperator()
      }
    }
    command(item = InsertMode) {
      action = {
        insertMode()
      }
    }
  }
}

fun main() {
  Application.runForm(formName = CommandsForm())
}
