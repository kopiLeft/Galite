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

import java.util.Locale

import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Command
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.form.Commands

open class FormDefault(title: String, locale: Locale? = null): ReportSelectionForm(title, locale) {

  fun insertMenus() {
    file; edit; action
  }

  fun insertActors() {
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
    dynamicReport
    help
    showHideFilter
    report
  }

  fun insertCommands() {
    autofill
    editItem
    editItemS

    quitForm; resetForm; helpForm
  }

  fun insertDefaultActors() {
    autofill
    editItem
    editItemS
  }

  // --------------------MENUS-----------------
  val file by lazy { menu(FileMenu()) }

  val edit by lazy { menu(EditMenu()) }

  val action by lazy { menu(ActionMenu()) }

  // --------------------ACTORS----------------
  val quit by lazy { actor(QuitActor()) }

  val _break by lazy { actor(BreakActor()) }

  val autofill by lazy { actor(AutofillActor()) }

  val editItem by lazy { actor(EditItemActor()) }

  val editItemS by lazy { actor(EditItemSActor()) }

  val searchOperator by lazy { actor(SearchOperatorActor()) }

  val insertLine by lazy { actor(InsertLineActor()) }

  val deleteLine by lazy { actor(DeleteLineActor()) }

  val menuQuery by lazy { actor(MenuQueryActor()) }

  val serialQuery by lazy { actor(SerialQueryActor()) }

  val insertMode by lazy { actor(InsertModeActor()) }

  val save by lazy { actor(SaveActor()) }

  val delete by lazy { actor(DeleteActor()) }

  val report by lazy { actor(ReportActor()) }

  val dynamicReport by lazy { actor(DynamicReportActor()) }

  val help by lazy { actor(HelpActor()) }

  val showHideFilter by lazy { actor(ShowHideFilterActor()) }

  // -------------------------------------------------------------------
  // FORM-LEVEL COMMANDS
  // -------------------------------------------------------------------
  val resetForm  by lazy {
    command(item = _break) {
      resetForm()
    }
  }

  val quitForm by lazy {
    command(item = quit) {
      quitForm()
    }
  }

  val helpForm by lazy {
    command(item = help) {
      showHelp()
    }
  }

  // -------------------------------------------------------------------
  // BLOCK-LEVEL COMMANDS
  // -------------------------------------------------------------------
  val Block.breakCmd: Command
    get() = command(item = _break) {
      resetBlock()
    }

  val Block.recursiveQueryCmd: Command
    get() = command(item = menuQuery) {
      Commands.recursiveQuery(block)
    }

  val Block.menuQueryCmd: Command
    get() = command(item = menuQuery) {
      Commands.menuQuery(block)
    }

  val Block.queryMoveCmd: Command
    get() = command(item = menuQuery) {
      Commands.queryMove(block)
    }

  val Block.serialQueryCmd: Command
    get() = command(item = serialQuery) {
      Commands.serialQuery(block)
    }

  val Block.insertModeCmd: Command
    get() = command(item = insertMode) {
      insertMode()
    }

  val Block.saveCmd: Command
    get() = command(item = save) {
      saveBlock()
    }
  val Block.deleteCmd: Command
    get() = command(item = delete) {
      deleteBlock()
    }

  val Block.insertLineCmd: Command
    get() = command(item = insertLine) {
      insertLine()
    }

  val Block.showHideFilterCmd: Command
    get() = command(item = showHideFilter) {
      showHideFilter()
    }
}

class AutofillActor: Actor(
  menu = EditMenu(),
  label = "Autofill",
  help = "Gives the possible values.",
  command = PredefinedCommand.AUTOFILL
) {
  init {
    key = Key.F2
  }
}

class BreakActor: Actor(
  menu = FileMenu(),
  label = "Break",
  help = "Reset current changes.",
) {
  init {
    key = Key.F3
    icon = Icon.BREAK
  }
}

class DynamicReportActor: Actor(
  menu = ActionMenu(),
  label = "Dyn report",
  help = "Create a dynamic report.",
) {
  init {
    key = Key.F11
    icon = Icon.PREVIEW
  }
}

class DeleteActor: Actor(
  menu = ActionMenu(),
  label = "Delete",
  help = "Delete this record.",
) {
  init {
    key = Key.F5
    icon = Icon.DELETE
  }
}

class DeleteLineActor: Actor(
  menu = EditMenu(),
  label = "Row -",
  help = "Delete this row.",
) {
  init {
    key = Key.F5
    icon = Icon.DELETE_LINE
  }
}

class EditItemActor: Actor(
  menu = EditMenu(),
  label = "Edit",
  help = "Edit this element.",
  command = PredefinedCommand.EDIT_ITEM
) {
  init {
    key = Key.SHIFT_F2
  }
}

class EditItemSActor: Actor(
  menu = EditMenu(),
  label = "Edit",
  help = "Edit this element.",
  command = PredefinedCommand.EDIT_ITEM_SHORTCUT
) {
  init {
    key = Key.SHIFT_F2
  }
}

class HelpActor:  Actor(
  menu = ActionMenu(),
  label = "Help",
  help = "Display a help.",
) {
  init {
    key = Key.F1
    icon = Icon.HELP
  }
}

class InsertLineActor: Actor(
  menu = EditMenu(),
  label = "Row +",
  help = "Insert a new row to this block.",
) {
  init {
    key = Key.F4
    icon = Icon.INSERT_LINE
  }
}

class InsertModeActor: Actor(
  menu = ActionMenu(),
  label = "New",
  help = "Create a new record.",
) {
  init {
    key = Key.F4
    icon = Icon.INSERT
  }
}

class MenuQueryActor: Actor(
  menu = ActionMenu(),
  label = "List",
  help = "Query: display results in a list.",
) {
  init {
    key = Key.F8
    icon = Icon.MENU_QUERY
  }
}

class PDFActor: Actor(
  menu = ActionMenu(),
  label = "PDF",
  help = "PDF Format",
) {
  init {
    key = Key.F9
    icon = Icon.EXPORT_PDF
  }
}

class QuitActor: Actor(
  menu = FileMenu(),
  label = "Quit",
  help = "Quit this form.",
) {
  init {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }
}

class ReportActor: Actor(
  menu = ActionMenu(),
  label = "Report",
  help = "Create a report.",
) {
  init {
    key = Key.F8
    icon = Icon.REPORT
  }
}

class SaveActor: Actor(
  menu = ActionMenu(),
  label = "Save",
  help = "Save the modifications in the database.",
) {
  init {
    key = Key.F7
    icon = Icon.SAVE
  }
}

class SearchOperatorActor: Actor(
  menu = EditMenu(),
  label = "Condition",
  help = "Change the search operator.",
) {
  init {
    key = Key.F5
    icon = Icon.SEARCH_OP
  }
}

class SerialQueryActor: Actor(
  menu = ActionMenu(),
  label = "Query",
  help = "Load the data after filling to the fields.",
) {
  init {
    key = Key.F6
    icon = Icon.SERIAL_QUERY
  }
}

class ShowHideFilterActor: Actor(
  menu = ActionMenu(),
  label = "Show/hider filter",
  help = "Show or hide the fliter of the block",
) {
  init {
    key = Key.SHIFT_F12
    icon = Icon.SEARCH_OP
  }
}
