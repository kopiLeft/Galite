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
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Key

class CommandsForm : DictionaryForm(title = "Commands Form", locale = Locale.UK) {
  val autoFill = actor(
    menu = actionMenu,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )
  val list = actor(
    menu = actionMenu,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F2
    icon = Icon.LIST
  }
  val resetBlock = actor(
    menu = actionMenu,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F11
    icon = Icon.BREAK
  }
  val saveBlock = actor(
    menu = actionMenu,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = Icon.SAVE
  }
  val deleteBlock = actor(
    menu = actionMenu,
    label = "deleteBlock",
    help = " deletes block",
  ) {
    key = Key.F4
    icon = Icon.DELETE
  }
  val Operator = actor(
    menu = actionMenu,
    label = "search",
    help = " search",
  ) {
    key = Key.F7
    icon = Icon.DETAIL_VIEW
  }

  val helpCmd = command(item = help) {
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
    command(item = insertMode) {
      insertMode()
    }
  }
}

fun main() {
  runForm(formName = CommandsForm())
}
