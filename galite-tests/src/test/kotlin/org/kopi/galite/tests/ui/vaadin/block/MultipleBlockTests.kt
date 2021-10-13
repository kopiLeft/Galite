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

import java.util.Locale

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editRecord
import org.kopi.galite.testing.editText
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.findMultipleBlock
import org.kopi.galite.testing.open
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.type.Week
import org.kopi.galite.tests.examples.Type
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.MONTH
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TIME
import org.kopi.galite.visual.domain.TIMESTAMP
import org.kopi.galite.visual.domain.WEEK
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.tests.examples.initDatabase

import com.github.mvysny.kaributesting.v10.expectRow

class MultipleBlockTests: GaliteVUITestBase() {

  val multipleForm = MultipleFormExample().also { it.model }

  @Before
  fun `login to the App`() {
    login()
  }

  @Test
  fun `test multiple-block data is sent to model after going to next record`() {
    // Open client form
    multipleForm.open()

    // Enters the sales block
    multipleForm.salesBlock.enter()

    // Enters values to fields
    val currentTimestamp   = Timestamp.now()
    val currentDate        = Date.now()
    val currentWeek        = Week.now()
    val currentMonth       = Month.now()
    val currentTime        = Time.now()
    val idClt       = multipleForm.salesBlock.idClient.edit(100)
    val description = multipleForm.salesBlock.description.edit("description")
    val price       = multipleForm.salesBlock.price.edit(Decimal.valueOf("100.2"))
    val active      = multipleForm.salesBlock.active.edit(true)
    val date        = multipleForm.salesBlock.date.edit(currentDate)
    val month       = multipleForm.salesBlock.month.edit(currentMonth)
    val timestamp   = multipleForm.salesBlock.timestamp.edit(currentTimestamp)
    val time        = multipleForm.salesBlock.time.edit(currentTime)
    val week        = multipleForm.salesBlock.week.edit(currentWeek)
    val codeDomain  = multipleForm.salesBlock.codeDomain.editText("Galite")

    // Go to the next record
    multipleForm.salesBlock.editRecord(1)

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
  fun `test multiple-block data is sent to model after going to next field`() {
    // Open client form
    multipleForm.open()

    // Enters the sales block
    multipleForm.salesBlock.enter()

    // Enters the id field editor
    val field = multipleForm.salesBlock.idClient.edit(100)

    // Go to the next field
    multipleForm.salesBlock.description.click()

    // Check that values are sent to the model
    assertEquals(100, field.getModel().getInt(0))
  }

  @Test
  fun `test displayed value of code-domain field in a multiple-block`() {
    // Open client form
    multipleForm.open()

    // Enters the sales block
    multipleForm.salesBlock.enter()

    // Set the value of the code-domain field
    multipleForm.salesBlock.codeDomain.editText("Kotlin")

    // Go to the next record
    multipleForm.salesBlock.editRecord(1)

    val block = multipleForm.salesBlock.findMultipleBlock()
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
        initDatabase()
      }
    }
  }
}

class MultipleFormExample : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Sales"
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )
  val block = insertBlock(Clients())
  val salesBlock = insertBlock(Sales())

  inner class Clients : FormBlock(1, 1, "Clients") {
    val idClt = visit(domain = INT(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
    }
  }

  inner class Sales : FormBlock(10, 10, "Sales") {
    val idClient = visit(domain = INT(5), position = at(1, 1..2)) {
      label = "ID"
      help = "The item id"
    }
    val description = visit(domain = STRING(25), position = at(2, 1)) {
      label = "Description"
      help = "The item description"
    }
    val price = visit(domain = DECIMAL(10, 5), position = at(3, 2)) {
      label = "Price"
      help = "The item price"
    }
    val active = visit(domain = BOOL, position = at(4, 1)) {
      label = "Status"
      help = "Is the user account active?"
    }
    val date = visit(domain = DATE, position = at(5, 1)) {
      label = "Date"
      help = "The date"
    }
    val month = visit(domain = MONTH, position = at(6, 1)) {
      label = "Month"
      help = "The month"
    }
    val timestamp = visit(domain = TIMESTAMP, position = at(7, 1)) {
      label = "Timestamp"
      help = "The Timestamp"
    }
    val time = visit(domain = TIME, position = at(8, 1)) {
      label = "Time"
      help = "The time"
    }
    val week = visit(domain = WEEK, position = at(9, 1)) {
      label = "Week"
      help = "The week"
    }
    val codeDomain = visit(domain = Type, position = at(10, 1)) {
      label = "codeDomain"
      help = "A code-domain field"
    }

    init {
      border = VConstants.BRD_LINE
    }
  }
}
