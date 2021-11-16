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
    val formModel = FormSample().model

    formModel.prepareForm()
    assertNotNull(formModel.getActiveBlock())
    assertNotNull(formModel.getActiveBlock()?.getActiveCommands())
  }

  @Test
  fun `reset test`() {
    val formModel = FormSample().model

    formModel.reset()
    assertTrue(formModel.blocks.all { it.getMode() == VConstants.MOD_QUERY })
    assertNotNull(formModel.getActiveBlock())
  }

  @Test
  fun `setCommandsEnabled test`() {
    val form = FormSample()
    val formModel = form.model

    formModel.prepareForm() // internally will invoke setCommandsEnabled(true)
    assertTrue(formModel.blocks.any{
      it.getActiveCommands().isNotEmpty()
    })
  }

  @Test
  fun `gotoPage test`() {
    val form = FormSample()
    val formModel = form.model
    val targetPage = 0

    formModel.gotoPage(targetPage)
    assertEquals(form.formBlocks[0].vBlock, formModel.getActiveBlock())
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
  fun `commitTrail test`() {
    val form = FormSample()
    val formModel = form.model

    formModel.commitTrail()
    assertTrue(formModel.blocks.all {
      for (i in 0 until it.bufferSize) {
        if (!it.isRecordTrailed(i))
        {
          false
        }
      }
      true
    })
  }

  @Test
  fun `abortTrail test`() {
    val form = FormSample()
    val formModel = form.model

    formModel.abortTrail()
    assertTrue(formModel.blocks.all {
      for (i in 0 until it.bufferSize) {
        if (it.isRecordTrailed(i))
        {
          false
        }
      }
      true
    })
  }

  @Test
  fun `getDefaultActor test`() {
    val form = FormSample()
    val formModel = form.model

    assertNull(formModel.getDefaultActor(VForm.CMD_NEWITEM))
    assertNotNull(formModel.getDefaultActor(VForm.CMD_AUTOFILL))
    assertNull(formModel.getDefaultActor(VForm.CMD_EDITITEM))
    assertNotNull(formModel.getDefaultActor(VForm.CMD_EDITITEM_S))
  }
}
