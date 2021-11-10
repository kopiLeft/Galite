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
package org.kopi.galite.tests.ui.vaadin.block

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editRecord
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.expectConfirmNotification
import org.kopi.galite.testing.expectErrorNotification
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.TestTriggers
import org.kopi.galite.tests.examples.initDocumentationData
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification
import org.kopi.galite.visual.visual.MessageCode
import org.kopi.galite.testing.expectInformationNotification
import org.kopi.galite.testing.findMultipleBlock
import org.kopi.galite.tests.examples.DocumentationBlockForm
import org.kopi.galite.visual.ui.vaadin.field.TextField
import org.kopi.galite.visual.ui.vaadin.visual.DActor
import org.kopi.galite.visual.ui.vaadin.form.DBlock

import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._size
import com.github.mvysny.kaributesting.v10._value
import com.github.mvysny.kaributesting.v10.expectRow
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class DocumentationBlockFormTests : GaliteVUITestBase() {
  val form = DocumentationBlockForm().also { it.model }

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
  }

  @Test
  fun `test simple block`() {
    // Enter simple block
    form.simpleBlock.enter()
    val blockCaption = _find<VerticalLayout> {classes = "caption-container" }[0]._get<H4> { classes = "block-title" }

    assertEquals(form.simpleBlock.title, blockCaption.text)
  }

  @Test
  fun `test multi block`() {
    // Enter multi block
    form.multiBlock.enter()
    val blockCaption = _find<VerticalLayout> {classes = "caption-container" }[1]._get<H4> { classes = "block-title" }

    assertEquals(form.multiBlock.title, blockCaption.text)

    val block = form.multiBlock.findMultipleBlock()

    assertEquals(form.multiBlock.buffer, block.grid._size())

    for(i in 0 until block.grid._size()) {
      block.grid.expectRow(i, "")
    }
  }

  @Test
  fun `test LINE Border block`() {
    form.lineBorderBlock.enter()
    val blockCaption = _find<VerticalLayout> {classes = "caption-container" }[2]._get<H4> { classes = "block-title" }

    assertEquals(form.lineBorderBlock.title, blockCaption.text)
  }

  @Test
  fun `test RAISED Border block`() {
    form.raisedBorderBlock.enter()
    val blockCaption = _find<VerticalLayout> {classes = "caption-container" }[3]._get<H4> { classes = "block-title" }

    assertEquals(form.raisedBorderBlock.title, blockCaption.text)
  }

  @Test
  fun `test LOWERED Border block`() {
    form.loweredBorderBlock.enter()
    val blockCaption = _find<VerticalLayout> {classes = "caption-container" }[4]._get<H4> { classes = "block-title" }

    assertEquals(form.loweredBorderBlock.title, blockCaption.text)
  }

  @Test
  fun `test ETCHED Border block`() {
    form.etchedBorderBlock.enter()
    val blockCaption = _find<VerticalLayout> {classes = "caption-container" }[5]._get<H4> { classes = "block-title" }

    assertEquals(form.etchedBorderBlock.title, blockCaption.text)
  }

  @Test
  fun `test NOINSERT block`() {
    // NO INSERT block is the 9th block in the form
  _find<VerticalLayout> {classes = "k-block" }[9]._expectNone<TextField>()
  }

  @Test
  fun `test NOMOVE block`() {
    form.noMoveBlock.enter()
    form.noMoveBlock.editRecord(0)
    // Go to the next record
    form.noMoveBlock.editRecord(1)

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00025"))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test ACCESS ON SKIPPED block`() {
    form.accessOnSkippedBlock.enter()
    form.accessOnSkippedBlock.editRecord(0)
    // Go to the next record
    form.accessOnSkippedBlock.editRecord(1)
    // Go to the first record
    form.accessOnSkippedBlock.editRecord(0)
    // Check that there is no error notification, and that we can navigate between fields
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test PREQUERY block trigger`() {
    transaction {
      initDocumentationData()
    }

    form.triggersBlock.enter()
    // PREQRY trigger : click on list and check preQueryTrigger field
    form.list.triggerCommand()

    val field = form.triggersBlock.preQueryTrigger.findField()

    assertEquals("PREQRY trigger", field._value)
  }

  @Test
  fun `test POSTQRY block trigger`() {
    transaction {
      initDocumentationData()
    }

    form.triggersBlock.enter()
    // POSTQRY trigger : click on list and check postQueryTrigger field
    form.list.triggerCommand()

    val field = form.triggersBlock.postQueryTrigger.findField()

    assertEquals("POSTQRY trigger", field._value)
  }

  @Test
  fun `test PREDEL block trigger`() {
    transaction {
      initDocumentationData()
    }

    form.triggersBlock.enter()

    // PREDEL trigger : click on list command then delete command and check that information notification is displayed
    form.list.triggerCommand()
    form.deleteBlock.triggerCommand()

    expectConfirmNotification(true)

    expectInformationNotification("PREDEL Trigger")
  }

  @Test
  fun `test POSTDEL block trigger`() {
    form.triggersBlock.enter()
    form.deleteBlock.triggerCommand()

    expectConfirmNotification(true)

    val field = form.lastBlock.postDelTrigger.findField()

    // POSTDEL : click on deleteBlock command and assert that that POSTDEL trigger change the field value of lastBlock
    assertEquals("POSTDEL Trigger", field.value)
  }

  @Test
  fun `test PREINS and POSTINS block trigger`() {
    transaction {
      initDocumentationData()
    }

    // PREINS : click on insertMode command then save command and assert that PREINS trigger change the field value
    form.triggersBlock.enter()
    form.triggersBlock.preInsTrigger.edit("pre Ins")
    form.insertMode.triggerCommand()
    form.saveBlock.triggerCommand()

    expectInformationNotification("PRESAVE Trigger")

    transaction {
      assertEquals("PREINS Trigger", TestTriggers.selectAll().last()[TestTriggers.INS])
    }

    // POSTINS : click on insertMode command then save command and assert that POSTINS trigger change the field value of the lastBlock
    val field = form.lastBlock.postInsTrigger.findField()

    transaction {
      assertEquals("POSTINS Trigger", field._value)
    }
  }

  @Test
  fun `test PREUPD and POSTUPD block trigger`() {
    transaction {
      initDocumentationData()
    }

    // PREUPD : click on list command then save command and assert that PREUPD trigger change the field value
    form.triggersBlock.enter()
    form.list.triggerCommand()
    form.triggersBlock.preUpdTrigger.click()
    form.triggersBlock.preUpdTrigger.edit("a")
    form.saveBlock.triggerCommand()

    expectInformationNotification("PRESAVE Trigger")

    // POSTUPD : click on list command then save command and assert that POSTUPD trigger show an Information Notification
    expectInformationNotification("POSTUPD Trigger")

    transaction {
      assertEquals("PREUPD Trigger", TestTriggers.selectAll().last()[TestTriggers.UPD])
    }
  }

  @Test
  fun `test PRESAVE block trigger`() {
    transaction {
      initDocumentationData()
    }

    form.triggersBlock.enter()
    form.list.triggerCommand()
    form.triggersBlock.preUpdTrigger.click()
    form.triggersBlock.preUpdTrigger.edit("a")
    form.saveBlock.triggerCommand()

    // PRESAVE : click on list command then save command and assert that PRESAVE trigger show an Information Notification
    expectInformationNotification("PRESAVE Trigger")

    expectInformationNotification("POSTUPD Trigger")
  }

  @Test
  fun `test PREREC block trigger`() {
    form.triggersMultiBlock.enter()

    val block = form.triggersMultiBlock.findMultipleBlock()
    // PREREC : assert that PREREC trigger change the value of the first field when enter block
    block.grid.expectRow(0, "PREREC Trigger", "", "")
  }

  @Test
  fun `test POSTREC and VALREC block trigger`() {
    form.triggersMultiBlock.enter()
    // Go to the next record
    form.triggersMultiBlock.editRecord(1)

    val block = form.triggersMultiBlock.findMultipleBlock()
    // POSTREC : assert that POSTREC trigger change the value of the second field
    // VALREC : enter block, then leave it and check valRecTrigger field
    block.grid.expectRow(0, "PREREC Trigger", "POSTREC Trigger", "")
  }

  @Test
  fun `test VALREC block trigger`() {
    form.triggersMultiBlock.enter()
    //leave the block
    form.lastBlock.enter()

    val block = form.triggersMultiBlock.findMultipleBlock()
    // VALREC : enter block, then leave it and check valRecTrigger field
    block.grid.expectRow(0, "PREREC Trigger", "POSTREC Trigger", "VALREC Trigger")
  }

  @Test
  fun `test PREBLK block trigger`() {
    form.triggersBlock.enter()

    val field = form.triggersBlock.preBlkTrigger.findField()

    assertEquals("PREBLK Trigger", field._value)
  }

  @Test
  fun `test POSTBLK block trigger`() {
    form.triggersBlock.enter()
    form.lastBlock.enter()

    val field = form.triggersBlock.postBlkTrigger.findField()

    assertEquals("POSTBLK Trigger", field._value)
  }

  @Test
  fun `test VALBLK block trigger`() {
    form.triggersBlock.enter()
    form.lastBlock.enter()

    val field = form.triggersBlock.validateBlkTrigger.findField()

    assertEquals("VALBLK Trigger", field._value)
  }

  @Test
  fun `test DEFAULT block trigger`() {
    form.triggersBlock.enter()
    form.insertMode.triggerCommand()

    val field = form.triggersBlock.defaultBlkTrigger.findField()

    assertEquals("DEFAULT Trigger", field._value)
  }

  @Test
  fun `test INIT block trigger`() {
    form.triggersBlock.enter()

    val field = form.triggersBlock.initBlkTrigger.findField()

    assertEquals("INIT Trigger", field._value)
  }

  @Test
  fun `test command access in block`() {
    form.commandAccessBlock.enter()

    val actors = _find<DActor>()

    // the block is in mode insert, the actor list is disable in this mode
    assertFalse(actors.single { it.getModel().actorIdent == "list" }.isEnabled)
    // the block is in mode insert, the actor list is enabled in this mode
    assertTrue(actors.single { it.getModel().actorIdent == "delete Block" }.isEnabled)
  }

  @Test
  fun `test visibility of block`() {
    val blocks = _find<DBlock>()
    val block = blocks.single { it.model.title == "Block to test visibility" }
    // assert that the visibility of the field is changed by the blockVisibility method
    assertFalse(block._get<TextField>().isEnabled)
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        initModules()
      }
    }
  }
}
