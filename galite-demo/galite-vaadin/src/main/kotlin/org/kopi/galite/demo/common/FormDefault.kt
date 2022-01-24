/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.demo.common

import kotlin.reflect.KProperty

import java.util.WeakHashMap

import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Command
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Key

interface IFormDefault {
  fun Form.insertMenus() {
    file; edit; action
  }

  fun Form.insertActors() {
    quit
    _break
    autofill
    editItem
    editItemS
    searchOperator
    insertLine
    deleteLine
    menuQuery
    serialQuery
    insertMode
    save
    delete
    createReport
    createDynamicReport
    help
    showHideFilter
    report
  }

  fun Form.insertCommands() {
    autofill
    editItem
    editItemS

    quitForm; resetForm; helpForm
  }

  // -------------------------------------------------------------------
  // MENUS
  // -------------------------------------------------------------------
  val Form.file: Menu
  val Form.edit: Menu
  val Form.action: Menu

  // -------------------------------------------------------------------
  // ACTORS
  // -------------------------------------------------------------------
  val Form.quit: Actor
  val Form._break: Actor
  val Form.autofill: Actor
  val Form.editItem: Actor
  val Form.editItemS: Actor
  val Form.searchOperator: Actor
  val Form.insertLine: Actor
  val Form.deleteLine: Actor
  val Form.menuQuery: Actor
  val Form.serialQuery: Actor
  val Form.insertMode: Actor
  val Form.save: Actor
  val Form.delete: Actor
  val Form.createReport: Actor
  val Form.createDynamicReport: Actor
  val Form.help: Actor
  val Form.showHideFilter: Actor
  val Form.report: Actor

  // -------------------------------------------------------------------
  // FORM-LEVEL COMMANDS
  // -------------------------------------------------------------------
  val Form.resetForm: Command
  val Form.quitForm: Command
  val Form.helpForm: Command
}

open class FormDefaultImpl: Form(""), IFormDefault {

  // --------------------MENUS-----------------
  override val Form.file by LazyWithReceiver<Form, Menu> { menu("File") }

  override val Form.edit by LazyWithReceiver<Form, Menu> { menu("Edit") }

  override val Form.action by LazyWithReceiver<Form, Menu> { menu("Action") }

  // --------------------ACTORS----------------
  override val Form.quit by LazyWithReceiver<Form, Actor> {
    actor(
      menu = file,
      label = "Quit",
      help = "Quit",
    ) {
      key = Key.ESCAPE
      icon = Icon.QUIT
    }
  }

  override val Form._break by LazyWithReceiver<Form, Actor> {
    actor(
      menu = file,
      label = "Break",
      help = "Break",
    ) {
      key = Key.F3
      icon = Icon.BREAK
    }
  }

  override val Form.autofill by LazyWithReceiver<Form, Actor> {
    actor(
      menu = edit,
      label = "Autofill",
      help = "Autofill",
      command = PredefinedCommand.AUTOFILL
    ) {
      key = Key.F2
    }
  }

  override val Form.editItem by LazyWithReceiver<Form, Actor> {
    actor(
      menu = edit,
      label = "EditItem",
      help = "EditItem",
      command = PredefinedCommand.EDIT_ITEM
    ) {
      key = Key.SHIFT_F2
    }
  }

  override val Form.editItemS by LazyWithReceiver<Form, Actor> {
    actor(
      menu = edit,
      label = "EditItem_S",
      help = "EditItem_S",
      command = PredefinedCommand.EDIT_ITEM_SHORTCUT
    ) {
      key = Key.F2
    }
  }

  override val Form.searchOperator by LazyWithReceiver<Form, Actor> {
    actor(
      menu = edit,
      label = "SearchOperator",
      help = "SearchOperator",
    ) {
      key = Key.F5
      icon = Icon.SEARCH_OP
    }
  }

  override val Form.insertLine by LazyWithReceiver<Form, Actor> {
    actor(
      menu = edit,
      label = "InsertLine",
      help = "InsertLine",
    ) {
      key = Key.F4
      icon = Icon.INSERT_LINE
    }
  }

  override val Form.deleteLine by LazyWithReceiver<Form, Actor> {
    actor(
      menu = edit,
      label = "DeleteLine",
      help = "DeleteLine",
    ) {
      key = Key.F5
      icon = Icon.DELETE_LINE
    }
  }

  override val Form.menuQuery by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "MenuQuery",
      help = "MenuQuery",
    ) {
      key = Key.F8
      icon = Icon.MENU_QUERY
    }
  }

  override val Form.serialQuery by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "SerialQuery",
      help = "SerialQuery",
    ) {
      key = Key.F6
      icon = Icon.SERIAL_QUERY
    }
  }

  override val Form.insertMode by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "InsertMode",
      help = "InsertMode",
    ) {
      key = Key.F4
      icon = Icon.INSERT
    }
  }

  override val Form.save by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "Save",
      help = "Save",
    ) {
      key = Key.F7
      icon = Icon.SAVE
    }
  }

  override val Form.delete by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "Delete",
      help = "Delete",
    ) {
      key = Key.F5
      icon = Icon.DELETE
    }
  }

  override val Form.createReport by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "CreateReport",
      help = "CreateReport",
    ) {
      key = Key.F8
      icon = Icon.PREVIEW
    }
  }

  override val Form.createDynamicReport by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "CreateDynamicReport",
      help = "CreateDynamicReport",
    ) {
      key = Key.F11
      icon = Icon.PREVIEW
    }
  }

  override val Form.help by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "Help",
      help = "Help",
    ) {
      key = Key.F1
      icon = Icon.HELP
    }
  }

  override val Form.showHideFilter by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "ShowHideFilter",
      help = "ShowHideFilter",
    ) {
      key = Key.SHIFT_F12
      icon = Icon.SEARCH_OP
    }
  }

  override val Form.report by LazyWithReceiver<Form, Actor> {
    actor(
      menu = action,
      label = "CreateReport",
      help = "Create report",
    ) {
      key = Key.F8
      icon = Icon.REPORT
    }
  }

  // -------------------------------------------------------------------
  // FORM-LEVEL COMMANDS
  // -------------------------------------------------------------------
  override val Form.resetForm by LazyWithReceiver<Form, Command> {
    command(item = _break) {
      resetForm()
    }
  }

  override val Form.quitForm by LazyWithReceiver<Form, Command> {
    command(item = quit) {
      quitForm()
    }
  }

  override val Form.helpForm by LazyWithReceiver<Form, Command> {
    command(item = help) {
      showHelp()
    }
  }
}

class LazyWithReceiver<This, Return>(val initializer: This.() -> Return) {
  private val values = WeakHashMap<This, Return>()

  @Suppress("UNCHECKED_CAST")
  operator fun getValue(thisRef: Any, property: KProperty<*>): Return = synchronized(values)
  {
    thisRef as This
    return values.getOrPut(thisRef) { thisRef.initializer() }
  }
}
