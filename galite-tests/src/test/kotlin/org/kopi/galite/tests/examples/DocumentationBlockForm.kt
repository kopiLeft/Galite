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
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.Mode
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.form.Block
import org.kopi.galite.visual.dsl.form.Key

/*** Block Tables & Block Indexes  ***/
// See [DocumentationFieldsForm]

class DocumentationBlockForm : DictionaryForm(title = "Form to test Blocks", locale = Locale.UK) {

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

  val saveBlock = actor(
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = Icon.SAVE
  }

  val insertMode = actor(
    menu = action,
    label = "Insert",
    help = " Insert",
  ) {
    key = Key.F7
    icon = Icon.INSERT
  }

  val deleteBlock = actor(
    menu = action,
    label = "delete Block",
    help = " deletes block",
  ) {
    key = Key.F4
    icon = Icon.DELETE
  }

  val resetForm = actor(
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F11
    icon = Icon.BREAK
  }

  val resetFormCmd = command(item = resetForm) {
    resetForm()
  }

  /*** Block Types ***/
  val simpleBlock = insertBlock(SimpleBlock())
  val multiBlock = insertBlock(MultiBlock())
  /*** Block Border ***/
  // test border = Border.LINE
  val lineBorderBlock = insertBlock(LineBorderBlock())
  // test border = Border.RAISED
  val raisedBorderBlock = insertBlock(RaisedBorderBlock())
  // test border = Border.LOWERED
  val loweredBorderBlock = insertBlock(LoweredBorderBlock())
  // test border = Border.ETCHED
  val etchedBorderBlock = insertBlock(EtchedBorderBlock())

  /*** Block Options ***/
  // test NOCHART option
  val noChartBlock = insertBlock(NoChartBlock())
  // test NODETAIL option
  val noDetailBlock = insertBlock(NoDetailBlock())
  // test NODELETE option
  val noDeleteBlock = insertBlock(NoDeleteBlock())
  // test NOINSERT option
  val noInsertBlock = insertBlock(NoInsertBlock())
  // test NOMOVE option
  val noMoveBlock = insertBlock(NoMoveBlock())
  // test ACCESS_ON_SKIPPED option
  val accessOnSkippedBlock = insertBlock(AccessOnSkippedBlock())
  // test UPDATE_INDEX option
  val updateIndexBlock = insertBlock(UpdateIndexBlock())

  // test command access
  val commandAccessBlock = insertBlock(CommandAccessBlock()) {
    trigger(INIT) {
      setMode(Mode.INSERT)
    }

    command(item = list, modes = arrayOf(Mode.UPDATE, Mode.QUERY)) {
      recursiveQuery()
    }

    command(item = deleteBlock) {
      deleteBlock()
    }
  }

 // modify block visibility to skipped in mode Query and Insert
  val visibilityBlock = insertBlock(ChangeVisibilityBlock()) {
    blockVisibility(Access.SKIPPED, Mode.QUERY, Mode.INSERT)
  }

  // test triggers
  val triggersBlock = insertBlock(TriggersBlock())
  val triggersMultiBlock = insertBlock(TriggersMultiBlock())
  val lastBlock = insertBlock(LastBlock())

  /*** Block Types ***/
  // Simple block
  inner class SimpleBlock : Block("Simple Block", 1, 10) {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }
  // Multi block
  inner class MultiBlock : Block("Multi Block", 2, 2) {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  /*** Block Border ***/
  // test border = Border.LINE
  inner class LineBorderBlock : Block("Line Border Block", 2, 2) {
    init {
      border = Border.LINE
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test border = Border.RAISED
  inner class RaisedBorderBlock : Block("Raised Border Block", 2, 2) {
    init {
      border = Border.RAISED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test border = Border.LOWERED
  inner class LoweredBorderBlock : Block("Lowered Border Block", 2, 2) {
    init {
      border = Border.LOWERED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test border = Border.ETCHED
  inner class EtchedBorderBlock : Block("Etched Border Block", 2, 2) {
    init {
      border = Border.ETCHED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  /*** Block Alignment ***/
  class TestAlign : Block("Align block", 10, 8) {
    init {
      border = Border.LINE
      options(BlockOption.NODETAIL)
    }

    val description = visit(domain = STRING(20), position = at(1, 1)) {
      label = "Description"
      help = "The description of product"
    }

    val reference = visit(domain = STRING(20), position = at(2, 1)) {
      label = "Reference"
      help = "The reference of product"
    }

    val quantity = visit(domain = INT(20), position = at(3, 1)) {
      label = "quantity"
      help = "The quantity"
    }

    val price = visit(domain = STRING(20), position = at(4, 1)) {
      label = "Price"
      help = "The price"
    }
  }

  val targetBlock = insertBlock(TestAlign())
  val totalPrices = block("Total block", 1, 1) {

    val totalQuantity = visit(INT(20), position = at(1, 1)) {
      label = "Total"
      help = "Total"
    }

    val totalPrice = visit(INT(7), position = at(1, 2)) {}

    align(targetBlock, totalQuantity to targetBlock.quantity, totalPrice to targetBlock.price)
  }

  /*** Block Options ***/
  // test NOCHART option
  inner class NoChartBlock : Block("NOCHART Block", 2, 2) {
    init {
      border = Border.LINE
      options(BlockOption.NOCHART)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test NODETAIL option
  inner class NoDetailBlock : Block("NODETAIL Block", 2, 2) {
    init {
      border = Border.LINE
      options(BlockOption.NODETAIL)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test NODELETE option
  inner class NoDeleteBlock : Block("NODELETE Block", 2, 2) {
    init {
      border = Border.LINE
      options(BlockOption.NODELETE)

      command(item = deleteBlock) {
        deleteBlock()
      }
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test NOINSERT option
  inner class NoInsertBlock : Block("NOINSERT Block", 2, 2) {
    init {
      border = Border.LINE
      options(BlockOption.NOINSERT)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test NOMOVE option
  inner class NoMoveBlock : Block("NOMOVE Block", 2, 2) {
    init {
      border = Border.LINE
      options(BlockOption.NOMOVE)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test ACCESS_ON_SKIPPED option
  inner class AccessOnSkippedBlock : Block("ACCESS_ON_SKIPPED Block", 2, 2) {
    init {
      border = Border.LINE
      options(BlockOption.ACCESS_ON_SKIPPED)
    }
    val field = skipped(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test UPDATE_INDEX option
  inner class UpdateIndexBlock : Block("UPDATE_INDEX Block", 2, 2) {
    init {
      border = Border.LINE
      options(BlockOption.UPDATE_INDEX)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  /*** Simple Block Triggers ***/
  inner class TriggersBlock : Block("Simple block Triggers", 1, 10) {
    val t = table(TestTriggers)

    val id = hidden(domain = INT(20)) { columns(t.id) }
    val uc = hidden(domain = INT(20)) { columns(t.uc) }
    val ts = hidden(domain = INT(20)) { columns(t.ts) }

    val preQueryTrigger = visit(domain = STRING(20), position = at(1, 1)) {
      label = "PREQUERY Trigger"
    }

    val postQueryTrigger = visit(domain = STRING(20), position = at(1, 2)) {
      label = "POSYQUERY Trigger"
    }

    val preInsTrigger = visit(domain = STRING(20), position = at(1, 3)) {
      label = "PREINS Trigger"
      columns(t.INS)
    }

    val preUpdTrigger = visit(domain = STRING(20), position = at(1, 4)) {
      label = "PREUPD Trigger"
      columns(t.UPD)
    }

    val preBlkTrigger = visit(domain = STRING(20), position = at(2, 1)) {
      label = "PREBLK Trigger"
    }

    val postBlkTrigger = visit(domain = STRING(20), position = at(2, 2)) {
      label = "POSTBLK Trigger"
    }

    val validateBlkTrigger = visit(domain = STRING(20), position = at(2, 3)) {
      label = "VALBLK Trigger"
    }

    val defaultBlkTrigger = visit(domain = STRING(20), position = at(2, 4)) {
      label = "DEFAULT blockTrigger"
    }
    val initBlkTrigger = visit(domain = STRING(20), position = at(3, 1)) {
      label = "INIT block Trigger"
    }

    init {
      border = Border.LINE

      // test PREQRY trigger : click on list and check preQueryTrigger field
      trigger(PREQRY) {
        preQueryTrigger.value = "PREQRY trigger"
      }

      // test POSTQRY trigger : click on list and check postQueryTrigger field
      trigger(POSTQRY) {
        postQueryTrigger.value = "POSTQRY trigger"
      }

      // test PREDEL : click on list then delete
      trigger(PREDEL) {
        form.notice("PREDEL Trigger")
      }

      // test POSTDEL : click on deleteBlock command and assert that that POSTDEL trigger change the field value of lastBlock
      trigger(POSTDEL) {
       lastBlock.postDelTrigger.value = "POSTDEL Trigger"
      }

      // test PREINS : put values click on insert command then save
      trigger(PREINS) {
        preInsTrigger.value = "PREINS Trigger"
      }

      // test POSTINS : put values click on insert command then save
      trigger(POSTINS) {
        lastBlock.postInsTrigger.value = "POSTINS Trigger"
      }

      // test PREUPD : put values click on insert command then save
      trigger(PREUPD) {
        preUpdTrigger.value = "PREUPD Trigger"
      }

      // test POSTUPD : click on list changes values then save
      trigger(POSTUPD) {
        form.notice("POSTUPD Trigger")
      }

      // test PRESAVE : enter values then click on insert command then save
      trigger(PRESAVE) {
        form.notice("PRESAVE Trigger")
      }

      // test PREBLK : enter block, check preBlkTrigger field
      trigger(PREBLK) {
        preBlkTrigger.value = "PREBLK Trigger"
      }

      // test POSTBLK : leave block, check postBlkTrigger field
      trigger(POSTBLK) {
        postBlkTrigger.value = "POSTBLK Trigger"
      }

      // test VALBLK : enter block then leave it and check validateBlkTrigger field
      trigger(VALBLK) {
        validateBlkTrigger.value = "VALBLK Trigger"
      }

      // test DEFAULT : click on insert command
      trigger(DEFAULT) {
        defaultBlkTrigger.value = "DEFAULT Trigger"
      }

      // test INIT : enter block
      trigger(INIT) {
        initBlkTrigger.value = "INIT Trigger"
      }

      // check !
      trigger(RESET) {
        false
      }

      // check !
      trigger(CHANGED) {
        true
      }

      command(item = list) {
        recursiveQuery()
      }

      command(item = insertMode) {
        insertMode()
      }

      command(item = saveBlock) {
        saveBlock()
      }

      command(item = deleteBlock) {
        deleteBlock()
      }
    }
  }

  /*** Multi Block Triggers ***/
  inner class TriggersMultiBlock : Block("Multi block Triggers", 10, 10) {
    val preRecTrigger = visit(domain = STRING(20), position = at(1, 1)) {
      label = "PREREC Trigger"
    }

    val postRecTrigger = visit(domain = STRING(20), position = at(1, 2)) {
      label = "POSTREC Trigger"
    }

    val valRecTrigger = visit(domain = STRING(20), position = at(1, 3)) {
      label = "VALREC Trigger"
    }

    init {
      border = Border.LINE

      // test PREREC : enter block
      trigger(PREREC) {
        preRecTrigger.value = "PREREC Trigger"
      }

      // test POSTREC : enter block, then go to second record
      trigger(POSTREC) {
        postRecTrigger.value = "POSTREC Trigger"
      }

      // test VALREC : enter block, then leave it and check valRecTrigger field
      trigger(VALREC) {
        valRecTrigger.value = "VALREC Trigger"
      }
    }
  }

  inner class CommandAccessBlock : Block("Block to test command access", 1, 10) {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  inner class ChangeVisibilityBlock : Block("Block to test visibility", 1, 10) {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  inner class LastBlock : Block("Last block", 1, 10) {
    val postDelTrigger = visit(domain = STRING(20), position = at(1, 1)) {
      label = "post Del Trigger"
    }
    val postInsTrigger = visit(domain = STRING(20), position = at(1, 2)) {
      label = "POSTINS Trigger Field"
    }
  }
}

fun main() {
  runForm(formName = DocumentationBlockForm())
}
