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

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.form.VConstants

/*** Block Tables & Block Indexes  ***/
// See [DocumentationFieldsForm]

class DocumentationBlockForm : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Form to test Blocks"

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

  val saveBlock = actor(
    ident = "saveBlock",
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = "save"
  }

  val insertMode = actor(
    ident = "Insert",
    menu = action,
    label = "Insert",
    help = " Insert",
  ) {
    key = Key.F7
    icon = "insert"
  }

  val deleteBlock = actor(
    ident = "delete Block",
    menu = action,
    label = "delete Block",
    help = " deletes block",
  ) {
    key = Key.F4
    icon = "delete"
  }

  val resetForm = actor(
    ident = "reset",
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F11
    icon = "break"
  }

  val resetFormCmd = command(item = resetForm) {
    action = {
      resetForm()
    }
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
      vBlock.setMode(VConstants.MOD_INSERT)
    }

    command(item = list) {
      mode(Mode.UPDATE, Mode.QUERY)
      action = {
        recursiveQuery()
      }
    }

    command(item = deleteBlock) {
      action = {
        deleteBlock()
      }
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
  inner class SimpleBlock : FormBlock(1, 10, "Simple Block") {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }
  // Multi block
  inner class MultiBlock : FormBlock(2, 2, "Multi Block") {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  /*** Block Border ***/
  // test border = Border.LINE
  inner class LineBorderBlock : FormBlock(2, 2, "Line Border Block") {
    init {
      border = Border.LINE
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test border = Border.RAISED
  inner class RaisedBorderBlock : FormBlock(2, 2, "Raised Border Block") {
    init {
      border = Border.RAISED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test border = Border.LOWERED
  inner class LoweredBorderBlock : FormBlock(2, 2, "Lowered Border Block") {
    init {
      border = Border.LOWERED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test border = Border.ETCHED
  inner class EtchedBorderBlock : FormBlock(2, 2, "Etched Border Block") {
    init {
      border = Border.ETCHED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  /*** Block Alignment ***/
  class TestAlign : FormBlock(10, 8, "Align block") {
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
  val totalPrices = block(1, 1, "Total", "Total block") {

    val totalQuantity = visit(INT(20), position = at(1, 1)) {
      label = "Total"
      help = "Total"
    }

    val totalPrice = visit(INT(7), position = at(1, 2)) {}

    align(targetBlock, totalQuantity to targetBlock.quantity, totalPrice to targetBlock.price)
  }

  /*** Block Options ***/
  // test NOCHART option
  inner class NoChartBlock : FormBlock(2, 2, "NOCHART Block") {
    init {
      border = Border.LINE
      options(BlockOption.NOCHART)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test NODETAIL option
  inner class NoDetailBlock : FormBlock(2, 2, "NODETAIL Block") {
    init {
      border = Border.LINE
      options(BlockOption.NODETAIL)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test NODELETE option
  inner class NoDeleteBlock : FormBlock(2, 2, "NODELETE Block") {
    init {
      border = Border.LINE
      options(BlockOption.NODELETE)

      command(item = deleteBlock) {
        action = {
          deleteBlock()
        }
      }
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test NOINSERT option
  inner class NoInsertBlock : FormBlock(2, 2, "NOINSERT Block") {
    init {
      border = Border.LINE
      options(BlockOption.NOINSERT)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test NOMOVE option
  inner class NoMoveBlock : FormBlock(2, 2, "NOMOVE Block") {
    init {
      border = Border.LINE
      options(BlockOption.NOMOVE)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test ACCESS_ON_SKIPPED option
  inner class AccessOnSkippedBlock : FormBlock(2, 2, "ACCESS_ON_SKIPPED Block") {
    init {
      border = Border.LINE
      options(BlockOption.ACCESS_ON_SKIPPED)
    }
    val field = skipped(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // test UPDATE_INDEX option
  inner class UpdateIndexBlock : FormBlock(2, 2, "UPDATE_INDEX Block") {
    init {
      border = Border.LINE
      options(BlockOption.UPDATE_INDEX)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  /*** Simple Block Triggers ***/
  inner class TriggersBlock : FormBlock(1, 10, "Simple block Triggers") {
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
        vBlock.form.notice("PREDEL Trigger")
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
        vBlock.form.notice("POSTUPD Trigger")
      }

      // test PRESAVE : enter values then click on insert command then save
      trigger(PRESAVE) {
        vBlock.form.notice("PRESAVE Trigger")
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

      command(item = list) {
        action = {
          recursiveQuery()
        }
      }

      command(item = insertMode) {
        action = {
          insertMode()
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
    }
  }

  /*** Multi Block Triggers ***/
  inner class TriggersMultiBlock : FormBlock(10, 10, "Multi block Triggers") {
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

  inner class CommandAccessBlock : FormBlock(1, 10, "Block to test command access") {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  inner class ChangeVisibilityBlock : FormBlock(1, 10, "Block to test visibility") {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  inner class LastBlock : FormBlock(1, 10, "Last block") {
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
