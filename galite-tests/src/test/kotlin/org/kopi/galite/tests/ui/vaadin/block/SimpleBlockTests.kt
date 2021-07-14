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

import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.field.TextField
import org.kopi.galite.ui.vaadin.menu.ModuleList

import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.menubar.MenuBar
import org.kopi.galite.ui.vaadin.block.SimpleBlockLayout
import org.kopi.galite.ui.vaadin.field.BooleanField
import org.kopi.galite.ui.vaadin.form.DBooleanField

class SimpleBlockTests: GaliteVUITestBase() {

  val modulesMenu get() = _get<ModuleList> { id = "module_list" }._get<MenuBar>()

  @Test
  fun `test field value is sent to model`() {
    // Login
    login()

    // Open client form
    modulesMenu._clickItemWithCaptionAndWait("Client form")

    // Find fields
    val fields = _find<TextField> { classes = Styles.TEXT_FIELD }
    val codeField = fields[0]
    val firstNameField = fields[1]

    // set field value
    codeField._value = "123"
    val inputField = codeField.field.content as AbstractField<*, String?>
    inputField._fireEvent(AbstractField.ComponentValueChangeEvent(inputField, inputField, "123", true))

    // Focus on another field
    firstNameField._clickAndWait(100)

    // Assert the value is sent to the model
    val value = codeField.model.getInt()?.toString()
    assertEquals("123", value)
  }

  @Test
  fun `test timeStamp field value is sent to model`() {
    // Login
    login()

    // Open client form
    modulesMenu._clickItemWithCaptionAndWait("Bills form")

    // Find fields
    val fields = _find<TextField> { classes = Styles.TEXT_FIELD }
    val addressField = fields[0]
    val dateField = fields[1]

    dateField._clickAndWait(100)
    // set field value
    dateField._value = "2018-09-13 00:00:00"
    val inputField = dateField.field.content as AbstractField<*, String?>
    inputField._fireEvent(AbstractField.ComponentValueChangeEvent(inputField, inputField, "2018-09-13 00:00:00", true))

    // Focus on another field
    addressField._clickAndWait(100)

    // Assert the value is sent to the model
    val value = dateField.model.getTimestamp().toString()
    assertEquals("2018-09-13 00:00:00.000000", value)
  }

  @Test
  fun `test boolean field value is sent to model`() {
    // Login
    login()

    // Open client form
    modulesMenu._clickItemWithCaptionAndWait("Client form")

    val simpleBlock  = _get<SimpleBlockLayout> { classes = "simple"}

    // Find fields
    val fields = _find<TextField> { classes = Styles.TEXT_FIELD }
    val codeField = fields[0]

    simpleBlock._get<DBooleanField> {}.wrappedField.isVisible = true
    val content = simpleBlock._get<DBooleanField> {}._get<BooleanField> { classes = "k-boolean-field"}
    val yes = content._get<Checkbox> { classes = "true" }
    val no = content._get<Checkbox> { classes = "false" }

    // Assert that checkbox is visible
    assertEquals(true, yes.isVisible)
    assertEquals(true, no.isVisible)
    // set yes field value
     yes._value = true

    // Focus on another field
    codeField._clickAndWait(100)

    // Assert the value is sent to the model
    assertEquals(true, yes.value)
    assertEquals(false, no.value)

    // set no field value
    no._value = true

    // Focus on another field
    codeField._clickAndWait(100)

    // Assert the value is sent to the model
    assertEquals(false, yes.value)
    assertEquals(true, no.value)
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
