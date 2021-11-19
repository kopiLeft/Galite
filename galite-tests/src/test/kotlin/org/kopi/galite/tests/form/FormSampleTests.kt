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
package org.kopi.galite.tests.form

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VForm

class FormSampleTests: JApplicationTestBase() {

  @Test
  fun sourceFormTest() {
    val formModel = FormSample().model

    assertEquals(FormSample::class.qualifiedName!!.replace(".", "/"), formModel.source)
  }

  @Test
  fun changeBlockAccessTest() {
    val formSample = FormSample().also { it.model }

    assertEquals(1, formSample.tb4ToTestChangeBlockAccess.vBlock.getAccess())

    assertArrayEquals(intArrayOf(0, 0, 0), formSample.tb4ToTestChangeBlockAccess.id.access)
    assertArrayEquals(intArrayOf(1, 1, 2), formSample.tb4ToTestChangeBlockAccess.name.access)
    assertArrayEquals(intArrayOf(1, 1, 4), formSample.tb4ToTestChangeBlockAccess.password.access)
    assertArrayEquals(intArrayOf(1, 1, 2), formSample.tb4ToTestChangeBlockAccess.age.access)
  }

  @Test
  fun `prepareForm test`() {
    val form = FormSample()
    val formModel = form.model

    assertNull(formModel.getActiveBlock())
    assertNull(formModel.getActiveBlock()?.getActiveCommands())
    formModel.prepareForm()
    assertEquals(form.tb1.vBlock, formModel.getActiveBlock())
    assertNotNull(formModel.getActiveBlock()?.getActiveCommands())
  }

  @Test
  fun `reset test`() {
    val form = FormSample()
    val formModel = form.model

    formModel.reset()
    assertTrue(formModel.blocks.all { it.getMode() == VConstants.MOD_QUERY })
    assertEquals(form.tb1.vBlock, formModel.getActiveBlock())
  }

  @Test
  fun `setCommandsEnabled test`() {
    val form = FormSample()
    val formModel = form.model

    formModel.setCommandsEnabled(false)
    assertTrue(formModel.blocks.all {
      it.getActiveCommands().isEmpty()
    })
    formModel.blocks[0].enter()
    formModel.setCommandsEnabled(true)
    assertTrue(formModel.blocks.any {
      it.getActiveCommands().isNotEmpty()
    })
  }

  @Test
  fun `gotoPage test`() {
    val form = FormSample()
    val formModel = form.model
    val targetPage = 1

    formModel.gotoPage(targetPage)
    assertEquals(form.formBlocks[2].vBlock, formModel.getActiveBlock())
  }

  @Test
  fun `gotoBlock test`() {
    val form = FormSample()
    val formModel = form.model
    val targetBlock = 2

    formModel.gotoBlock(formModel.getBlock(targetBlock))
    assertEquals(form.formBlocks[targetBlock].vBlock, formModel.getActiveBlock())
  }

  @Test
  fun `gotoNextBlock test`() {
    val form = FormSample()
    val formModel = form.model
    val targetBlock = 1

    formModel.gotoBlock(formModel.getBlock(targetBlock))
    formModel.gotoNextBlock()
    assertEquals(form.formBlocks[targetBlock + 1].vBlock, formModel.getActiveBlock())
  }

  @Test
  fun `enterBlock test`() {
    val form = FormSample()
    val formModel = form.model

    formModel.enterBlock()
    assertEquals(form.formBlocks[0].vBlock, formModel.getActiveBlock())
  }

  @Test
  fun `getDefaultActor test`() {
    val formWithDefaultActors = FormSample()
    val forWithDefaultActorsModel = formWithDefaultActors.model

    assertNotNull(forWithDefaultActorsModel.getDefaultActor(VForm.CMD_NEWITEM))
    assertNotNull(forWithDefaultActorsModel.getDefaultActor(VForm.CMD_AUTOFILL))
    assertNotNull(forWithDefaultActorsModel.getDefaultActor(VForm.CMD_EDITITEM))
    assertNotNull(forWithDefaultActorsModel.getDefaultActor(VForm.CMD_EDITITEM_S))
    val formWithoutDefaultActors = FormWithFields()
    val formWithoutDefaultActorsModel = formWithoutDefaultActors.model

    assertNull(formWithoutDefaultActorsModel.getDefaultActor(VForm.CMD_NEWITEM))
    assertNull(formWithoutDefaultActorsModel.getDefaultActor(VForm.CMD_AUTOFILL))
    assertNull(formWithoutDefaultActorsModel.getDefaultActor(VForm.CMD_EDITITEM))
    assertNull(formWithoutDefaultActorsModel.getDefaultActor(VForm.CMD_EDITITEM_S))
  }
}
