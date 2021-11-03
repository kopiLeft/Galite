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

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.tests.db.connectToDatabase

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

// **> for border  check result in kopi
// **> Block Tables & Block Indexes see DocumentationFieldsForm

class DocumentationBlockForm : DictionaryForm() {
  override val locale = Locale.UK

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
  val saveBlock = actor(
    ident = "saveBlock",
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = "save"
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

  val deleteBlock = actor(
    ident = "deleteBlock",
    menu = action,
    label = "deleteBlock",
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

  val block1 = insertBlock(Block1())
  val block2 = insertBlock(Block2())
  /*** TEST Block Border ***/
  //test border = Border.LINE
  val block3 = insertBlock(Block3())
  //test border = Border.RAISED
  val block4 = insertBlock(Block4())
  //test border = Border.LOWERED
  val block5 = insertBlock(Block5())
  //test border = Border.ETCHED
  val block6 = insertBlock(Block6())

  /*** TEST Block Options ***/
  //test NOCHART option
  val block7 = insertBlock(Block7())
  //test NODETAIL option
  val block8 = insertBlock(Block8())
  //test NODELETE option
  val block9 = insertBlock(Block9())
  //test NOINSERT option
  val block10 = insertBlock(Block10())
  //test NOMOVE option
  val block11 = insertBlock(Block11())
  //test ACCESS_ON_SKIPPED option
  val block12 = insertBlock(Block12())
  //test UPDATE_INDEX option
  val block13 = insertBlock(Block13())

  //test command access set access of block in mode insert and make command available in mode query and update
  val block14 = insertBlock(Block1()) {
    trigger(INIT) {
      vBlock.setMode(VConstants.MOD_INSERT)
    }
    command(item = list) {
      mode(Mode.UPDATE, Mode.QUERY)
      action = {
        recursiveQuery()
      }
    }
  }

  // modify block visibility to skipped // **> check if we need to add mode ANY also if we can put visibility without Mode !!!
  val block15 = insertBlock(Block1()) {
    blockVisibility(Access.SKIPPED, Mode.QUERY, Mode.INSERT)
  }

  //test triggers
  val block16 = insertBlock(TriggersBlock())

  //simple block
  inner class Block1 : FormBlock(1, 10, "Block1") {
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }
  //Multi block
  inner class Block2 : FormBlock(2, 2, "Block2") {
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }
  /*** TEST Block Border ***/
  //test border = Border.LINE
  inner class Block3 : FormBlock(2, 2, "Block3") {
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field border LINE"
    }
  }
  //test border = Border.RAISED
  inner class Block4 : FormBlock(2, 2, "Block4") {
    init {
      border = Border.RAISED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field border RAISED"
    }
  }
  //test border = Border.LOWERED
  inner class Block5 : FormBlock(2, 2, "Block5") {
    init {
      border = Border.LOWERED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field border LOWERED"
    }
  }
  //test border = Border.ETCHED
  inner class Block6 : FormBlock(2, 2, "Block6") {
    init {
      border = Border.ETCHED
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field border ETCHED"
    }
  }

  /*** TEST Block Alignment ***/

  class TestAlign : FormBlock(10, 8, "Test block") {

    init {
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
  val TotalPrices = block(1, 1, "Total", "Total block") {

    val totalQuantity = visit(INT(20), position = at(1, 1)) {
      label = "Total"
      help = "Total"
    }
    val totalPrice = visit(INT(7), position = at(1, 2)) {}

    align(targetBlock, totalQuantity to targetBlock.quantity, totalPrice to targetBlock.price)
  }

  /*** TEST Block Options ***/
  //test NOCHART option
  inner class Block7 : FormBlock(2, 2, "Block7") {
    init {
      options(BlockOption.NOCHART)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field Block7 NOCHART"
    }
  }
  //test NODETAIL option
  inner class Block8 : FormBlock(2, 2, "Block8") {
    init {
      options(BlockOption.NODETAIL)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field Block8 NODETAIL"
    }
  }
  //test NODELETE option
  inner class Block9 : FormBlock(2, 2, "Block9") {
    init {
      options(BlockOption.NODELETE)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field Block9 NODELETE"
    }
  }
  //test NOINSERT option
  inner class Block10 : FormBlock(2, 2, "Block10") {
    init {
      options(BlockOption.NOINSERT)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field Block10 NOINSERT"
    }
  }
  //test NOMOVE option
  inner class Block11 : FormBlock(2, 2, "Block11") {
    init {
      options(BlockOption.NOMOVE)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field Block11 NOMOVE"
    }
  }
  //test ACCESS_ON_SKIPPED option
  inner class Block12 : FormBlock(2, 2, "Block12") {
    init {
      options(BlockOption.ACCESS_ON_SKIPPED)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field Block12 ACCESS_ON_SKIPPED"
    }
  }
  //test UPDATE_INDEX option
  inner class Block13 : FormBlock(2, 2, "Block13") {
    init {
      options(BlockOption.UPDATE_INDEX)
    }
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field Block13 UPDATE_INDEX"
    }
  }

  /*** Block Triggers ***/
  inner class TriggersBlock : FormBlock(1, 10, "block Triggers") {
    val t = table(TestTriggers)

    val id = visit(domain = INT(20), position = at(1, 1)) {
      label = "id"
      columns(t.id)
    }
    val name = visit(domain = STRING(20), position = at(2, 1)) {
      label = "name"
      columns(t.INS)
    }

    init {

      // go to the block and enter field
      trigger(PREQRY) {
        println("PREQRY trigger !!")
      }

      // click on list
      trigger(POSTQRY) {
        println("POSTQRY trigger !!")
      }

      // click on list then delete
      trigger(PREDEL) {
        println("PREDEL trigger !!")
      }

      // click on list then delete
      trigger(POSTDEL) {
        println("POSTDEL trigger !!")
      }

      // put values click on insert command then save
      trigger(PREINS) {
        println("PREINS trigger !!")
      }

      // put values click on insert command then save
      trigger(PREUPD) {
        println("PREUPD trigger !!")
      }

      // click on list changes values then save
      trigger(POSTUPD) {
        println("POSTUPD trigger !!")
      }

      // enter values then insert command then save
      trigger(PRESAVE) {
        println("PRESAVE trigger !!")
      }

      // to check !
      trigger(PREREC) {
        println("PREREC trigger !!")
      }

      // to check !
      trigger(POSTREC) {
        println("POSTREC trigger !!")
      }

      // enter block
      trigger(PREBLK) {
        println("PREBLK trigger !!")
      }

      // leave block
      trigger(POSTBLK) {
        println("POSTBLK trigger !!")
      }

      // enter block enter values in fields then leave it
      trigger(VALBLK) {
        println("VALBLK trigger !!")
      }

      // to check !
      trigger(VALREC) {
        println("VALBLK trigger !!")
      }

      // click on insert command
      trigger(DEFAULT) {
        println("DEFAULT trigger !!")
      }

      // enter block
      trigger(INIT) {
        println("INIT trigger !!")
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
      command(item = InsertMode) {
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
}

fun main() {
  connectToDatabase()
  transaction {
    SchemaUtils.create(TestTriggers)
    SchemaUtils.createSequence(org.jetbrains.exposed.sql.Sequence("TRIGGERSID"))
    TestTriggers.insert {
      it[id] = 1
      it[INS] = "INS-1"
      it[UPD] = "UPD-1"
    }
  }

  runForm(formName = DocumentationBlockForm())
}
