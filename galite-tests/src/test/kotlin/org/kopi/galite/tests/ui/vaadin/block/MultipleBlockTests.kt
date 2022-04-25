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

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing._enter
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editRecord
import org.kopi.galite.testing.editText
import org.kopi.galite.testing.findMultiBlock
import org.kopi.galite.testing.open
import org.kopi.galite.tests.examples.FormExample
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.type.Week
import org.kopi.galite.visual.type.format

import com.github.mvysny.kaributesting.v10.expectRow

class MultipleBlockTests: GaliteVUITestBase() {

  val formExample = FormExample()

  @Before
  fun `login to the App`() {
    login()
  }

  @Test
  fun `test multiple-block data is sent to model after going to next record`() {
    // Open client form
    formExample.open()

    // Enters the sales block
    formExample.salesBlock._enter()

    // Enters values to fields
    val currentTimestamp   = Instant.now()
    val currentDate        = LocalDate.now()
    val currentWeek        = Week.now()
    val currentMonth       = Month.now()
    val currentTime        = LocalTime.now()
    val idClt       = formExample.salesBlock.idClient.edit(100)
    val description = formExample.salesBlock.description.edit("description")
    val price       = formExample.salesBlock.price.edit(BigDecimal("100.2"))
    val active      = formExample.salesBlock.active.edit(true)
    val date        = formExample.salesBlock.date.edit(currentDate)
    val month       = formExample.salesBlock.month.edit(currentMonth)
    val timestamp   = formExample.salesBlock.timestamp.edit(currentTimestamp)
    val time        = formExample.salesBlock.time.edit(currentTime)
    val week        = formExample.salesBlock.week.edit(currentWeek)
    val codeDomain  = formExample.salesBlock.codeDomain.editText("Galite")

    // Go to the next record
    formExample.salesBlock.editRecord(1)

    // Check that values are sent to the model
    assertEquals(100, idClt.getModel().getInt(0))
    assertEquals("description", description.getModel().getString(0))
    assertEquals(BigDecimal("100.20000"), price.getModel().getDecimal(0))
    assertEquals(true, active.getModel().getBoolean(0))
    assertEquals(currentDate, date.getModel().getDate(0))
    assertEquals(currentMonth, month.getModel().getMonth(0))
    assertEquals(currentTimestamp.defaultFormat(), timestamp.getModel().getTimestamp(0).defaultFormat())
    assertEquals(currentTime.format(), time.getModel().getTime(0).toString())
    assertEquals(currentWeek, week.getModel().getWeek(0))
    assertEquals(1, codeDomain.getModel().getObject(0))
  }

  @Test
  fun `test multiple-block data is sent to model after going to next field`() {
    // Open client form
    formExample.open()

    // Enters the sales block
    formExample.salesBlock._enter()

    // Enters the id field editor
    val field = formExample.salesBlock.idClient.edit(100)

    // Go to the next field
    formExample.salesBlock.description.click()

    // Check that values are sent to the model
    assertEquals(100, field.getModel().getInt(0))
  }

  @Test
  fun `test displayed value of code-domain field in a multiple-block`() {
    // Open client form
    formExample.open()

    // Enters the sales block
    formExample.salesBlock._enter()

    // Set the value of the code-domain field
    formExample.salesBlock.codeDomain.editText("Kotlin")

    // Go to the next record
    formExample.salesBlock.editRecord(1)

    val block = formExample.salesBlock.findMultiBlock()
    block.grid.expectRow(0, "", "", "", "", "", "", "", "", "", "Kotlin")
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
