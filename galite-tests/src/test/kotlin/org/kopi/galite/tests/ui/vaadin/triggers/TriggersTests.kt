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

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.testing.click
import org.kopi.galite.tests.examples.FormExample

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
    form.salesSimpleBlock.price.click()
    assertEquals("item description", field.value)
  }

  /**
   * assert that the field contains data from the VALUE field trigger
   */
  @Test
  fun `test VALUE field trigger`() {
    val field = form.salesSimpleBlock.idClt.findField()

    assertEquals("10", field.value)
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
