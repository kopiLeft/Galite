/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.form.UField
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editRecord
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.open
import org.kopi.galite.tests.examples.FormExample
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.type.Date
import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

class MultipleBlockTests: GaliteVUITestBase() {

  val formExample = FormExample().also { it.model }

  @Before
  fun loginToTheApp() {
    login()
  }

  @Test
  fun `test multiple-block data is sent to model after going to next record`() {
    // Open client form
    formExample.open()

    // Enters the sales block
    formExample.salesBlock.enter()

    // Enters the id field editor
    val fields = mutableListOf<UField>()
    val now = Timestamp.now()
    val date = Date.now()
    val week = Week.now()
    val month = Month.now()
    val time = Time.now()
    fields.add(formExample.salesBlock.idClt.edit(100))
    fields.add(formExample.salesBlock.description.edit("description"))
    fields.add(formExample.salesBlock.price.edit(Decimal.valueOf("100.2")))
    fields.add(formExample.salesBlock.active.edit(true))
    fields.add(formExample.salesBlock.date.edit(date))
    fields.add(formExample.salesBlock.month.edit(month))
    fields.add(formExample.salesBlock.timestamp.edit(now))
    fields.add(formExample.salesBlock.time.edit(time))
    fields.add(formExample.salesBlock.week.edit(week))

    // Go to the next record
    formExample.salesBlock.editRecord(1)

    // Check that values are sent to the model
    val values = listOf(100,
                        "description",
                        Decimal.valueOf("100.20000"),
                        true,
                        date,
                        month,
                        Timestamp.parse(now.format("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"),
                        time,
                        week)
    fields.forEachIndexed { i, it ->
      assertEquals(values[i].toString(), it.getModel().getObject(0).toString())
    }
  }

  @Test
  fun `test multiple-block data is sent to model after going to next field`() {
    // Open client form
    formExample.open()

    // Enters the sales block
    formExample.salesBlock.enter()

    // Enters the id field editor
    val field = formExample.salesBlock.idClt.edit(100)

    // Go to the next field
    formExample.salesBlock.description.click()

    // Check that values are sent to the model
    assertEquals(100, field.getModel().getInt(0))
  }

  companion object {
    /**
     * Defined your test specific modules
     */
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        org.kopi.galite.tests.examples.initModules()
      }
    }
  }
}
