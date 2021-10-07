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
package org.kopi.galite.tests.ui.vaadin.triggers

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.testing.click
import org.kopi.galite.testing.editText
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.CommandsForm
import org.kopi.galite.tests.examples.FormExample
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.visual.type.Decimal

class TriggersTests : GaliteVUITestBase() {

  val form = FormExample().also { it.model }

  @Before
  fun `login to the App`() {
    org.kopi.galite.tests.examples.initData()

    login()

    // Open the form
    form.open()
  }

  /**
   * put value in the first field, leave it
   * and check that a second field contains data from POSTCHG field trigger
   */
  @Test
  fun `test POSTCHG field trigger`() {
    val field = form.salesSimpleBlock.description.findField()

    assertEquals("description value", field.value)
    form.salesSimpleBlock.idClt.edit(20)
    form.salesSimpleBlock.information.click()
    assertEquals("sales description", field.value)
  }

  /**
   * assert that the field contains data from the VALUE field trigger
   */
  @Test
  fun `test VALUE field trigger`() {
    val field = form.salesSimpleBlock.information.findField()

    assertEquals("sales information", field.value)
  }

  /**
   * assert that the field contains data from the INIT block trigger
   */
  @Test
  fun `test INIT block trigger`() {
    val field = form.salesSimpleBlock.description.findField()

    assertEquals("description value", field.value)
  }

  /**
   * assert that the field contains data from the PREBLK block trigger
   */
  @Test
  fun `test PREBLK block trigger`() {
    val field = form.block.idClt.findField()

    assertEquals("50", field.value)
  }

  @Ignore
  @Test
  fun `test ACTION field trigger`() {
    val field = form.salesSimpleBlock.description.findField()

    assertEquals("description value", field.value)
    form.salesSimpleBlock.action.click()
    assertEquals("item description", field.value)
  }

  @Ignore
  @Test
  fun `test DEFAULT trigger and save new record`() {
    val form = CommandsForm().also { it.model }
    form.open()
    transaction {
      // Check initial data
      val initialData = Training.selectAll().map {
        arrayOf(it[Training.id],
                it[Training.trainingName],
                it[Training.type],
                it[Training.price],
                it[Training.active]
        )
      }

      assertArraysEquals(arrayOf(1, "training 1", 3, Decimal("1149.240").value, true), initialData[0])
      assertArraysEquals(arrayOf(2, "training 2", 1, Decimal("219.600").value, true), initialData[1])
      assertArraysEquals(arrayOf(3, "training 3", 2, Decimal("146.900").value, true), initialData[2])
      assertArraysEquals(arrayOf(4, "training 4", 1, Decimal("3129.700").value, true), initialData[3])
    }

    form.InsertMode.triggerCommand()

    form.block.trainingType.editText("Galite")
    form.block.trainingPrice.edit(Decimal("1000"))
    form.block.active.edit(true)

    form.saveBlock.triggerCommand()

    transaction {
      val data = Training.selectAll().map {
        arrayOf(it[Training.id],
                it[Training.trainingName],
                it[Training.type],
                it[Training.price],
                it[Training.active]
        )
      }

      assertArraysEquals(arrayOf(1, "training 1", 3, Decimal("1149.240").value, true), data[0])
      assertArraysEquals(arrayOf(2, "training 2", 1, Decimal("219.600").value, true), data[1])
      assertArraysEquals(arrayOf(3, "training 3", 2, Decimal("146.900").value, true), data[2])
      assertArraysEquals(arrayOf(4, "training 4", 1, Decimal("3129.700").value, true), data[3])
      assertArraysEquals(arrayOf(5, "training value", 1, Decimal("1000.000").value, true), data[4])
    }
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        org.kopi.galite.tests.examples.initModules()
      }
    }
  }
}
