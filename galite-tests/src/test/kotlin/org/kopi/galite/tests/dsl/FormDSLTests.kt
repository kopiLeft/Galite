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
package org.kopi.galite.tests.dsl

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

import java.util.Locale
import java.awt.event.KeyEvent

import org.junit.Test
import org.kopi.galite.tests.examples.MultipleBlockForm
import org.kopi.galite.tests.form.FormWithAlignedBlock
import org.kopi.galite.tests.form.User
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.FieldAlignment
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.Block
import org.kopi.galite.visual.dsl.form.FormCoordinatePosition
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.form.VConstants

class FormDSLTests : VApplicationTestBase() {
  @Test
  fun `test generated model from a basic form`() {
    val form = BasicForm()
    val model = form.model

    assertEquals(form.locale, model.locale)
    assertEquals(form.title, model.getTitle())
  }

  @Test
  fun `test a simple form block`() {
    val form = FormWithOneSimpleBlock()
    val model = form.model

    assertCollectionsEquals(form.blocks, mutableListOf(form.block))
    assertEquals(1, model.blocks.size)

    val blockModel = model.blocks.single()

    assertEquals(form.block.title, blockModel.title)
    assertEquals(form.block.buffer, blockModel.bufferSize)
    assertEquals(form.block.visible, blockModel.displaySize)
    assertEquals(form.block.border, blockModel.border)
    assertEquals(2, blockModel.maxColumnPos)
    assertEquals(1, blockModel.maxRowPos)
    assertEquals(0, blockModel.pageNumber)
    assertEquals(0, blockModel.displayedFields)
    assertEquals(null, blockModel.alignment)
  }

  @Test
  fun `test a form field`() {
    val form = FormWithOneSimpleBlock()
    val block = form.model.blocks.single()
    val model = block.blockFields.single()

    assertEquals(form.block.idClt.label, model.label)
    assertEquals(form.block.idClt.help, model.toolTip)
    assertEquals(block, model.block)
    assertEquals(FieldAlignment.RIGHT.value, model.align)
    assertEquals(form.block.border.value, model.border)
    assertEquals(0, model.options)
    assertEquals(-1, model.posInArray)
    assertEquals(1, model.height)
    assertEquals(form.block.idClt.domain.width, model.width)
    assertEquals(true, form.block.idClt.vField.isNumeric())
  }

  @Test
  fun `test form with aligned block`() {
    val form = FormWithAlignedBlock()
    val targetBlockModel = form.model.blocks.single { it == form.targetBlock }
    val totalBlockModel = form.model.blocks.single { it == form.totalBlock }

    assertEquals(targetBlockModel, totalBlockModel.alignment!!.block)
    assertArraysEquals(arrayOf(2, 3), totalBlockModel.alignment!!.targets.toTypedArray())
  }

  @Test
  fun `test a form with simple and multiple block`() {
    val form = FormWithMultipleBlock()
    val formModel = form.model

    assertCollectionsEquals(form.blocks, mutableListOf(form.clientBlock, form.commandsBlock))
    assertEquals(2, formModel.blocks.size)

    val clientBlock = formModel.blocks[0]
    val commandsBlock = formModel.blocks[1]

    assertEquals(form.title, formModel.getTitle())
    assertEquals(form.locale, formModel.locale)
    //clientBlock
    assertEquals(3, clientBlock.blockFields.size)
    assertEquals(form.clientBlock.buffer, clientBlock.bufferSize)
    assertEquals(form.clientBlock.visible, clientBlock.displaySize)
    assertEquals(form.clientBlock.border, clientBlock.border)
    assertEquals(form.clientBlock.title, clientBlock.title)
    assertEquals(1, clientBlock.maxColumnPos)
    assertEquals(3, clientBlock.maxRowPos)
    assertEquals(0, clientBlock.pageNumber)
    assertEquals(0, clientBlock.displayedFields)
    assertEquals(null, clientBlock.alignment)
    //commandsBlock
    assertEquals(2, commandsBlock.blockFields.size)
    assertEquals(form.commandsBlock.visible, commandsBlock.displaySize)
    assertEquals(form.commandsBlock.border, commandsBlock.border)
    assertEquals(form.commandsBlock.title, commandsBlock.title)
    assertEquals(1, commandsBlock.maxColumnPos)
    assertEquals(1, commandsBlock.maxRowPos)
    assertEquals(1, commandsBlock.pageNumber)
    assertEquals(1, commandsBlock.displayedFields)
    assertEquals(null, commandsBlock.alignment)
  }

  /* @Test TODO
   fun `test access fields values`() {
     val form = FormWithMultipleBlock()
     val formModel = form.model
     val clientBlock = formModel.blocks[0]
     val commandsBlock = formModel.blocks[1]

     assertEquals(form.clientBlock.idClt.access, clientBlock.fields[0].access) // Mustfill field
     assertEquals(form.clientBlock.clientName.access, clientBlock.fields[1].access) // Visit field
     assertEquals(form.commandsBlock.idCmd.access, commandsBlock.fields[0].access) // Hidden field
     assertEquals(form.commandsBlock.cmdName.access, commandsBlock.fields[1].access) // Skipped field
   }*/

  @Test
  fun `test block triggers`() {
    val form = FormWithMultipleBlock()
    val formModel = form.model
    val clientBlock = formModel.blocks[0]

    assertTrue(clientBlock.hasTrigger(VConstants.TRG_INIT))
    assertTrue(clientBlock.hasTrigger(VConstants.TRG_PREBLK))

    for (i in 0 until 32) {
      if (i !in listOf(VConstants.TRG_INIT, VConstants.TRG_PREBLK)) {
        assertFalse(clientBlock.hasTrigger(i))
      }
    }
  }

  @Test
  fun `test block indexes`() {
    val form = MultipleBlockForm()
    val index = form.block2.indices[0]

    assertEquals(index, form.block2.fields[0].columns?.index)
    assertEquals(index, form.block2.fields[1].columns?.index)
  }

  @Test
  fun `test Form Coordinate Position`() {
    val firstCoordinatePosition = FormCoordinatePosition(1, 2, 3, 4)
    val secondCoordinatePosition = FormCoordinatePosition(5)
    val thirdCoordinatePosition = FormCoordinatePosition(1, 2)
    val fourthCoordinatePosition = FormCoordinatePosition(1, 2, 3, 4, 5)

    // first coordinate position
    assertEquals(-1, firstCoordinatePosition.getPositionModel().chartPos)
    assertEquals(3, firstCoordinatePosition.getPositionModel().column)
    assertEquals(1, firstCoordinatePosition.getPositionModel().line)
    // second coordinate position
    assertEquals(5, secondCoordinatePosition.getPositionModel().chartPos)
    assertEquals(-1, secondCoordinatePosition.getPositionModel().column)
    assertEquals(-1, secondCoordinatePosition.getPositionModel().line)
    // third coordinate position
    assertEquals(-1, thirdCoordinatePosition.getPositionModel().chartPos)
    assertEquals(2, thirdCoordinatePosition.getPositionModel().column)
    assertEquals(1, thirdCoordinatePosition.getPositionModel().line)
    // second coordinate position
    assertEquals(5, fourthCoordinatePosition.getPositionModel().chartPos)
    assertEquals(3, fourthCoordinatePosition.getPositionModel().column)
    assertEquals(1, fourthCoordinatePosition.getPositionModel().line)
  }

  @Test
  fun `test field triggers`() {
    val form = FormWithMultipleBlock()
    val formModel = form.model
    val clientBlock = formModel.blocks[0]
    val idClientModel = clientBlock.blockFields[0]
    val nameClientModel = clientBlock.blockFields[1]

    assertTrue(nameClientModel.hasTrigger(VConstants.TRG_POSTCHG))

    for (i in 0 until 32) {
      assertFalse(idClientModel.hasTrigger(i))
      if (i != VConstants.TRG_POSTCHG) {
        assertFalse(nameClientModel.hasTrigger(i))
      }
    }
  }

  @Test
  fun `test field column name, table, priority and number of columns associated with this field`() {
    val form = FormWithMultipleBlock()
    val formModel = form.model
    val clientBlock = formModel.blocks[0]
    val nameClientModel = clientBlock.blockFields[1]

    assertEquals(User.name.name, nameClientModel.getColumn(0)!!.name)
    assertEquals(User, nameClientModel.getColumn(0)!!.getTable())
    assertEquals(form.clientBlock.clientName.columns!!.priority, nameClientModel.getPriority())
    assertEquals(1, nameClientModel.getColumnCount())
  }

  @Test
  fun `test form command`() {
    val form = FormWithMultipleBlock()
    val formModel = form.model

    assertEquals(1, formModel.commands.size)

    assertEquals(form.resetForm.ident, formModel.commands[0].actorIdent)
    assertEquals(form.resetForm.ident, formModel.commands[0].actor!!.ident)
    assertEquals(form.resetForm.menu.label, formModel.commands[0].actor!!._menuIdent)
    assertEquals(form.resetForm.icon?.iconName, formModel.commands[0].actor!!._iconName)
    assertEquals(form.resetForm.help, formModel.commands[0].actor!!.help)
  }

  @Test
  fun `test form actors`() {
    val form = FormWithMultipleBlock()
    val formModel = form.model

    assertEquals(3, formModel.actors.size)

    assertEquals("File", formModel.actors[0]._menuName)
    assertEquals("GotoShortcuts", formModel.actors[0].ident)
    assertEquals(null, formModel.actors[0]._iconName)
    assertEquals("Go to shortcut list", formModel.actors[0].help)
    assertEquals(KeyEvent.VK_F12, formModel.actors[0]._acceleratorKey)

    assertEquals(form.edit.label, formModel.actors[1]._menuName)
    assertEquals(form.autoFill.ident, formModel.actors[1].ident)
    assertEquals(form.autoFill.icon?.iconName, formModel.actors[1]._iconName)
    assertEquals(form.autoFill.help, formModel.actors[1].help)

    assertEquals(form.reset.label, formModel.actors[2]._menuName)
    assertEquals(form.resetForm.ident, formModel.actors[2].ident)
    assertEquals(form.resetForm.icon?.iconName, formModel.actors[2]._iconName)
    assertEquals(form.resetForm.help, formModel.actors[2].help)
    assertEquals(form.resetForm.key!!.value, formModel.actors[2]._acceleratorKey)
  }

  @Test
  fun `test field command`() {
    val form = FormWithMultipleBlock()
    val formModel = form.model
    val clientBlock = formModel.blocks[0]
    val fileModel = clientBlock.blockFields[2]

    assertEquals(1, fileModel.command!!.size)

    assertEquals(form.autoFill.ident, fileModel.command!![0].actorIdent)
    assertEquals(form.autoFill.ident, fileModel.command!![0].actor!!.ident)
    assertEquals(form.autoFill.menu.label, fileModel.command!![0].actor!!._menuIdent)
    assertEquals(form.autoFill.icon?.iconName, fileModel.command!![0].actor!!._iconName)
    assertEquals(form.autoFill.help, fileModel.command!![0].actor!!.help)
  }

  @Test
  fun `test field options`() {
    val form = FormWithMultipleBlock()
    val formModel = form.model
    val clientBlock = formModel.blocks[0]
    val nameClientModel = clientBlock.blockFields[1]
    val fileModel = clientBlock.blockFields[2]

    assertEquals(FieldOption.QUERY_LOWER.value, nameClientModel.options)
    assertEquals(FieldOption.QUERY_UPPER.value, fileModel.options)
  }

  /*@Test
  fun `test access fields`() {
    val form = org.kopi.galite.tests.form.FormToCheckFieldVisibility
    val formModel = form.model
    val clientBlock = formModel.blocks[0]
    val nameClientModel =  clientBlock.fields[1]
    val ageClientModel =  clientBlock.fields[2]
    val genderClientModel =  clientBlock.fields[4]

    assertEquals(blockToCheckFieldVisibility.name.access, nameClientModel.access)
    assertEquals(blockToCheckFieldVisibility.age.access, ageClientModel.access)
    assertEquals(blockToCheckFieldVisibility.gender.access, genderClientModel.access)
  }*/
}

class BasicForm : Form(title = "Clients", locale = Locale.UK)

class FormWithOneSimpleBlock : Form(title = "Clients", locale = Locale.UK) {
  val page = page("title")
  val block = page.insertBlock(SimpleBlock())

  inner class SimpleBlock : Block("SimpleBlock", 1, 1) {
    init {
      help = "Information about the block"
    }
    val idClt = visit(domain = INT(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
    }
  }
}

class FormWithMultipleBlock : Form(title = "Information", locale = Locale.UK) {
  val reset = menu("reset")
  val edit = menu("edit")

  val autoFill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )

  val resetForm = actor(
    menu = reset,
    label = "resetForm",
    help = "Reset Form",
  ) {
    key = Key.F7
    icon = Icon.BREAK
  }

  val resetFormCmd = command(item = resetForm) {
    resetForm()
  }

  val firstPage = page("Client")
  val secondPage = page("Commands")
  val clientBlock = firstPage.insertBlock(ClientBlock())
  val commandsBlock = secondPage.insertBlock(CommandsBlock())

  inner class ClientBlock : Block("ClientBlock", 1, 1) {
    init {
      help = "Information about the client"
    }

    val u = table(User)
    val idClt = mustFill(domain = INT(30), position = at(1, 1)) {
      label = "ID"
      help = "The client id"
    }
    val clientName = visit(domain = STRING(30), position = at(2, 1)) {
      label = "Name"
      help = "The client Name"
      options(FieldOption.QUERY_LOWER)
      columns(u.name) {
        priority = 9
      }
      trigger(POSTCHG) {
        println("value changed !!")
      }
    }
    val file = visit(domain = STRING(25), position = at(3, 1)) {
      label = "test"
      help = "The test"
      options(FieldOption.QUERY_UPPER)
      command(item = autoFill) {}
    }

    init {
      trigger(PREBLK, INIT) {}
    }
  }

  inner class CommandsBlock : Block("CommandsBlock", 10, 5) {
    init {
      help = "Information about the commands"
    }
    val idCmd = hidden(domain = INT(30)) {
      label = "ID"
      help = "The command id"
    }
    val cmdName = skipped(domain = STRING(30), position = at(1, 1)) {
      label = "Name"
      help = "The command Name"
    }

    init {
      border = Border.LINE
    }
  }
}
