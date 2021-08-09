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
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editRecord
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.open
import org.kopi.galite.tests.examples.form.ClientForm
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase

class MultipleBlockTests: GaliteVUITestBase() {

  val clientForm = ClientForm().also { it.model }

  @Test
  fun `test multiple-block data is sent to model after going to next record`() {
    // Login
    login()

    // Open client form
    clientForm.open()

    // Enters the sales block
    clientForm.salesBlock.enter()

    // Enters the id field editor
    val field = clientForm.salesBlock.idClt.edit("100")

    // Go to the next record
    clientForm.salesBlock.editRecord(1)

    // Check that values are sent to the model
    assertEquals("100", field.getModel().getString(0))
  }

  @Test
  fun `test multiple-block data is sent to model after going to next field`() {
    // Login
    login()

    // Open client form
    clientForm.open()

    // Enters the sales block
    clientForm.salesBlock.enter()

    // Enters the id field editor
    val field = clientForm.salesBlock.idClt.edit("100")

    // Go to the next field
    clientForm.salesBlock.description.click()

    // Check that values are sent to the model
    assertEquals("100", field.getModel().getString(0))
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
