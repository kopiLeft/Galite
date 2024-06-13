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
package org.kopi.galite.tests.ui.vaadin.block

import java.time.LocalDateTime

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.github.mvysny.kaributesting.v10.expectRow

import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.orderedlayout.VerticalLayout

import org.jetbrains.exposed.sql.selectAll

import org.kopi.galite.testing._enter
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editRecord
import org.kopi.galite.testing.expectConfirmNotification
import org.kopi.galite.testing.expectErrorNotification
import org.kopi.galite.testing.expectInformationNotification
import org.kopi.galite.testing.findActor
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.findMultiBlock
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.DocumentationBlockForm
import org.kopi.galite.tests.examples.TestTriggers
import org.kopi.galite.tests.examples.initData
import org.kopi.galite.tests.examples.initDocumentationData
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.ui.vaadin.field.TextField
import org.kopi.galite.visual.ui.vaadin.form.DBlock
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification

class DocumentationBlockFormTests : GaliteVUITestBase() {
  val form = DocumentationBlockForm()

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
  }

  @After
  fun `close pool connection`() {
    ApplicationContext.getDBConnection()?.poolConnection?.close()
  }

  @Test
  fun `test titles of form blocks`() {
    val simpleBlockCaption = _find<VerticalLayout> {classes = "caption-container" }[0]._get<H4> { classes = "block-title" }
    assertEquals(form.simpleBlock.title, simpleBlockCaption.text)

    val multiBlockCaption = _find<VerticalLayout> {classes = "caption-container" }[1]._get<H4> { classes = "block-title" }
    assertEquals(form.multiBlock.title, multiBlockCaption.text)

    val lineBorderBlockCaption = _find<VerticalLayout> {classes = "caption-container" }[2]._get<H4> { classes = "block-title" }
    assertEquals(form.lineBorderBlock.title, lineBorderBlockCaption.text)

    val lineRaisedBlockCaption = _find<VerticalLayout> {classes = "caption-container" }[3]._get<H4> { classes = "block-title" }
    assertEquals(form.raisedBorderBlock.title, lineRaisedBlockCaption.text)

    val loweredBorderBlockCaption = _find<VerticalLayout> {classes = "caption-container" }[4]._get<H4> { classes = "block-title" }
    assertEquals(form.loweredBorderBlock.title, loweredBorderBlockCaption.text)

    val etchedBorderBlockCaption = _find<VerticalLayout> {classes = "caption-container" }[5]._get<H4> { classes = "block-title" }
    assertEquals(form.etchedBorderBlock.title, etchedBorderBlockCaption.text)
  }

 /* @Test
  fun `test LINE Border block`() {
    TODO
  }*/

  /*@Test
  fun `test RAISED Border block`() {
    TODO
  }*/

  /*@Test
  fun `test LOWERED Border block`() {
    TODO
  }*/

 /* @Test
  fun `test ETCHED Border block`() {
    TODO
  }*/

  @Test
  fun `test noDetail record of type LocalDateTime`() {
    transaction {
      initData()
      form.noDetailBlock.load()
    }
    for ( rec in 0 until form.noDetailBlock.buffer) {
      if (form.noDetailBlock.isCurrentRecordFilled() && form.noDetailBlock.trainingDateTime[rec] != null) {
        assertEquals(LocalDateTime::class.simpleName,
                     form.noDetailBlock.trainingDateTime[rec]!!::class.simpleName)
      }
    }
  }

  @Test
  fun `test NOINSERT block`() {
    // NO INSERT block is the 9th block in the form
  _find<VerticalLayout> {classes = "k-block" }[9]._expectNone<TextField>()
  }

  @Test
  fun `test NOMOVE block`() {
    form.noMoveBlock._enter()
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
    form.accessOnSkippedBlock._enter()
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

    form.triggersBlock._enter()
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

    form.triggersBlock._enter()
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

    form.triggersBlock._enter()

    // PREDEL trigger : click on list command then delete command and check that information notification is displayed
    form.list.triggerCommand()
    form.deleteBlock.triggerCommand()

    expectConfirmNotification(true)

    expectInformationNotification("PREDEL Trigger")
  }

  @Test
  fun `test POSTDEL block trigger`() {
    form.triggersBlock._enter()
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
    form.triggersBlock._enter()
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
    form.triggersBlock._enter()
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

    form.triggersBlock._enter()
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
    form.triggersMultiBlock._enter()

    val block = form.triggersMultiBlock.findMultiBlock()
    // PREREC : assert that PREREC trigger change the value of the first field when enter block
    block.grid.expectRow(0, """Div[text='PREREC Trigger']""",  """Div[]""",  """Div[]""")
  }

  @Test
  fun `test POSTREC and VALREC block trigger`() {
    form.triggersMultiBlock._enter()
    // Go to the next record
    form.triggersMultiBlock.editRecord(1)

    val block = form.triggersMultiBlock.findMultiBlock()
    // POSTREC : assert that POSTREC trigger change the value of the second field
    // VALREC : enter block, then leave it and check valRecTrigger field

    block.grid.expectRow(0,  """Div[text='PREREC Trigger']"""  , """Div[text='POSTREC Trigger']""", """Div[]""")
  }

  @Test
  fun `test VALREC block trigger`() {
    form.triggersMultiBlock._enter()
    //leave the block
    form.lastBlock._enter()

    val block = form.triggersMultiBlock.findMultiBlock()
    // VALREC : enter block, then leave it and check valRecTrigger field
    block.grid.expectRow(0, """Div[text='PREREC Trigger']"""  , """Div[text='POSTREC Trigger']""",  """Div[text='VALREC Trigger']""")
  }

  @Test
  fun `test PREBLK block trigger`() {
    form.triggersBlock._enter()

    val field = form.triggersBlock.preBlkTrigger.findField()

    assertEquals("PREBLK Trigger", field._value)
  }

  @Test
  fun `test POSTBLK block trigger`() {
    form.triggersBlock._enter()
    form.lastBlock._enter()

    val field = form.triggersBlock.postBlkTrigger.findField()

    assertEquals("POSTBLK Trigger", field._value)
  }

  @Test
  fun `test VALBLK block trigger`() {
    form.triggersBlock._enter()
    form.lastBlock._enter()

    val field = form.triggersBlock.validateBlkTrigger.findField()

    assertEquals("VALBLK Trigger", field._value)
  }

  @Test
  fun `test DEFAULT block trigger`() {
    form.triggersBlock._enter()
    form.insertMode.triggerCommand()

    val field = form.triggersBlock.defaultBlkTrigger.findField()

    assertEquals("DEFAULT Trigger", field._value)
  }

  @Test
  fun `test INIT block trigger`() {
    form.triggersBlock._enter()

    val field = form.triggersBlock.initBlkTrigger.findField()

    assertEquals("INIT Trigger", field._value)
  }

  @Test
  fun `test command access in block`() {
    form.commandAccessBlock._enter()

    // the block is in mode insert, the actor list is disable in this mode
    assertFalse(form.list.findActor().isEnabled)
    // the block is in mode insert, the actor list is enabled in this mode
    assertTrue(form.deleteBlock.findActor().isEnabled)
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
      org.jetbrains.exposed.sql.transactions.transaction(connection.dbConnection) {
        initModules()
      }
    }
  }
}
