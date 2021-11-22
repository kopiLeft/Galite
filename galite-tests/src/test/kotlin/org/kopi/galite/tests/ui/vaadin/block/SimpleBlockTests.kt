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

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editText
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.findBlock
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.FormExample
import org.kopi.galite.tests.examples.TestFieldsForm
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.type.Week

class SimpleBlockTests: GaliteVUITestBase() {
  val testFieldsForm = TestFieldsForm().also { it.model }
  val formExample = FormExample().also { it.model }

  @Before
  fun `login to the App`() {
    login()
  }

  @Test
  fun `test field's values in simple-block are sent to the model`() {
    // Open the form
    formExample.open()

    // Enter sales block
    formExample.salesSimpleBlock.enter()

    // Enters values to fields
    val currentTimestamp   = Timestamp.now()
    val currentDate        = Date.now()
    val currentWeek        = Week.now()
    val currentMonth       = Month.now()
    val currentTime        = Time.now()
    val idClt       = formExample.salesSimpleBlock.idClt.edit(100)
    val description = formExample.salesSimpleBlock.description.edit("description")
    val price       = formExample.salesSimpleBlock.price.edit(Decimal.valueOf("100.2"))
    val active      = formExample.salesSimpleBlock.active.edit(true)
    val date        = formExample.salesSimpleBlock.date.edit(currentDate)
    val month       = formExample.salesSimpleBlock.month.edit(currentMonth)
    val timestamp   = formExample.salesSimpleBlock.timestamp.edit(currentTimestamp)
    val time        = formExample.salesSimpleBlock.time.edit(currentTime)
    val week        = formExample.salesSimpleBlock.week.edit(currentWeek)
    val codeDomain  = formExample.salesSimpleBlock.codeDomain.editText("Galite")

    // Go to the first field
    formExample.salesSimpleBlock.idClt.click()

    // Check that values are sent to the model
    assertEquals(100, idClt.getModel().getInt(0))
    assertEquals("description", description.getModel().getString(0))
    assertEquals(Decimal.valueOf("100.20000"), price.getModel().getDecimal(0))
    assertEquals(true, active.getModel().getBoolean(0))
    assertEquals(currentDate, date.getModel().getDate(0))
    assertEquals(currentMonth, month.getModel().getMonth(0))
    assertEquals(Timestamp.parse(currentTimestamp.format("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"), timestamp.getModel().getTimestamp(0))
    assertEquals(currentTime.toString(), time.getModel().getTime(0).toString())
    assertEquals(currentWeek, week.getModel().getWeek(0))
    assertEquals(1, codeDomain.getModel().getObject(0))
  }

  @Test
  fun DynamicallyChangeBlocModeAndVerifyFieldsAccess(){
    testFieldsForm.open()
    val block = testFieldsForm.blockWithAllFieldVisibilityTypes

    // add value to mustfill fields in order to enable the commands
    block.mustFillField.edit("50")
    block.mustFillToVisitField.edit(50)
    block.mustFillToSkippedField.edit(50)
    block.mustFillToHiddenField.edit(50)
    block.visitField.click()
    // trigger a command
    testFieldsForm.insert.triggerCommand()

    val dBlock = block.findBlock()

    // verify that the mode have changed in the block
    assertEquals(Mode.INSERT.value,dBlock.model.getMode())
    // verify the modification in the visit fields access
    assertEquals(VConstants.ACS_SKIPPED, block.visitFieldToSkippedField.access[Mode.INSERT.value])
    assertEquals(VConstants.ACS_HIDDEN, block.visitFieldToHiddenField.access[Mode.INSERT.value])
    assertEquals(VConstants.ACS_MUSTFILL, block.visitFieldToMustFillField.access[Mode.INSERT.value])
    // verify the modification in the mustfill fields access
    assertEquals(VConstants.ACS_SKIPPED, block.mustFillToSkippedField.access[Mode.INSERT.value])
    assertEquals(VConstants.ACS_VISIT, block.mustFillToVisitField.access[Mode.INSERT.value])
    assertEquals(VConstants.ACS_HIDDEN, block.mustFillToHiddenField.access[Mode.INSERT.value])
    // verify the modification in the skipped fields access
    assertEquals(VConstants.ACS_HIDDEN, block.skippedToHiddenField.access[Mode.INSERT.value])
    assertEquals(VConstants.ACS_VISIT, block.skippedToVisitField.access[Mode.INSERT.value])
    assertEquals(VConstants.ACS_MUSTFILL, block.skippedToMustFillField.access[Mode.INSERT.value])
  }
  companion object {
    /**
     * Defined your test specific modules
     */
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        initModules()
      }
    }
  }
}
