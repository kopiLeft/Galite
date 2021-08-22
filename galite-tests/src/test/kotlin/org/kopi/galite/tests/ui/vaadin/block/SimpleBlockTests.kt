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

import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._fireEvent

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.testing.open
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.field.TextField

import com.github.mvysny.kaributesting.v10._value
import com.vaadin.flow.component.AbstractField

class SimpleBlockTests: GaliteVUITestBase() {

  val clientForm = ClientForm()

  @Test
  fun `test field value is sent to model`() {
    // Login
    login()

    // Open client form
    clientForm.open()

    // Find fields
    val fields = _find<TextField> { classes = Styles.TEXT_FIELD }
    val codeField = fields[0]
    val firstNameField = fields[1]

    // set field value
    codeField._value = "123"
    val inputField = codeField.inputField.content as AbstractField<*, String?>
    inputField._fireEvent(AbstractField.ComponentValueChangeEvent(inputField, inputField, "123", true))

    // Focus on another field
    firstNameField._clickAndWait(100)

    // Assert the value is sent to the model
    val value = codeField.model.getInt()?.toString()
    assertEquals("123", value)
  }

  companion object {
    /**
     * Defined your test specific modules
     */
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        org.kopi.galite.demo.initModules()
      }
    }
  }
}
