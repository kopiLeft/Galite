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
package org.kopi.galite.tests.ui.vaadin.field

import kotlin.test.assertEquals
import kotlin.test.assertFalse

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.click
import org.kopi.galite.testing.closeErrorNotification
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editText
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.TestFieldsForm
import org.kopi.galite.tests.examples.Trainer
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.ui.vaadin.common.VSpan
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification
import org.kopi.galite.visual.visual.MessageCode

import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._text
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class FieldsTests : GaliteVUITestBase() {

  val form = TestFieldsForm().also { it.model }

  @Before
  fun `login to the App`() {
    org.kopi.galite.tests.examples.initData()

    login()

    // Open the form
    form.open()
  }

  @Test
  fun `test that skipped field is disabled `() {
    val field = _get<Div> { classes = "skipped"}

    assertFalse(field.isEnabled)
  }

  @Test
  fun `test that block contains only three visible fields`() {
    val blockFields = _find<VerticalLayout> {classes = "k-block" }[1]._find<Div> { classes = "k-field"}
    val labels = _find<VerticalLayout> {classes = "k-block" }[1]._find<Span> { classes = "label"}

    assertEquals(3, blockFields.size)

    // Hidden field not visible
    _expectNone<Span> { text = "hidden field" }

    assertEquals("visit field", labels[0]._text)
    assertEquals("mustFill field", labels[1]._text)
    assertEquals("skipped field", labels[2]._text)
  }

  @Test
  fun `test that notification is displayed when leave block without filling mustfill fields`() {
    // Enter to second block
    form.blockWithAllFieldVisibilityTypes.mustFillField.click()
    // Quit the second block without filling mustfill field
    form.blockWithDifferentTypes.intField.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()
    val errorPopUp = _get<ErrorNotification>()
    val errorMessage = errorPopUp.
      _get<HorizontalLayout> { classes = "k-notification-content"}.
      _get<VSpan> { classes = "k-notification-message"  }

    assertEquals(MessageCode.getMessage("VIS-00023"), errorMessage.getHtml())
    // Close the error notification and check that's disappearing
    closeErrorNotification()
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test maxValue option`() {
    //put value greater than 50 in the first field
    form.blockWithDifferentTypes.intField.edit(70)
    form.blockWithDifferentTypes.decimalField.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()
    val errorPopUp = _get<ErrorNotification>()
    val errorMessage = errorPopUp.
      _get<HorizontalLayout> { classes = "k-notification-content"}.
      _get<VSpan> { classes = "k-notification-message"  }

    assertEquals(MessageCode.getMessage("VIS-00009", 50), errorMessage.getHtml())
    // Close the error notification and check that's disappearing
    closeErrorNotification()
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test minValue option`() {
    //put value less than 10 in the first field
    form.blockWithDifferentTypes.intField.edit(1)
    form.blockWithDifferentTypes.decimalField.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()
    val errorPopUp = _get<ErrorNotification>()
    val errorMessage = errorPopUp.
      _get<HorizontalLayout> { classes = "k-notification-content"}.
      _get<VSpan> { classes = "k-notification-message"  }

    assertEquals(MessageCode.getMessage("VIS-00012", 10), errorMessage.getHtml())
    // Close the error notification and check that's disappearing
    closeErrorNotification()
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test decimal field`() {
    form.blockWithDifferentTypes.decimalField.edit(Decimal("999999"))
    form.blockWithDifferentTypes.intField.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()
    val errorPopUp = _get<ErrorNotification>()
    val errorMessage = errorPopUp.
      _get<HorizontalLayout> { classes = "k-notification-content"}.
      _get<VSpan> { classes = "k-notification-message"  }

    assertEquals(MessageCode.getMessage("VIS-00009", 999.99), errorMessage.getHtml())
    // Close the error notification and check that's disappearing
    closeErrorNotification()
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test convert UPPER for string field`() {
    val fields = _find<VerticalLayout> {classes = "k-block" }[0]._find<Div> { classes = "k-field"}
    val input = "test"

    form.blockWithDifferentTypes.upperStringField.edit(input)
    form.blockWithDifferentTypes.decimalField.click()
    assertEquals(input.toUpperCase(), fields[0]._text)
  }

  @Test
  fun `test convert LOWER for string field`() {
    val fields = _find<VerticalLayout> {classes = "k-block" }[0]._find<Div> { classes = "k-field"}
    val input = "TEST"

    form.blockWithDifferentTypes.lowerStringField.edit(input)
    form.blockWithDifferentTypes.decimalField.click()
    assertEquals(input.toLowerCase(), fields[2]._text)
  }

  @Test
  fun `test convert Name for string field`() {
    val fields = _find<VerticalLayout> {classes = "k-block" }[0]._find<Div> { classes = "k-field"}
    val input = "test"

    form.blockWithDifferentTypes.nameStringField.edit(input)
    form.blockWithDifferentTypes.decimalField.click()
    assertEquals(input.substring(0,1).toUpperCase() + input.substring(1), fields[1]._text)
  }

  @Test
  fun `test Query UPPER and LOWER field options`() {
    form.blockWithSaveCommand.trainerFirstName.edit("FIRST NAME")
    form.blockWithSaveCommand.trainerLastName.editText("last*")

    form.serialQuery.triggerCommand()

    transaction {
      val data = Trainer.selectAll().map {
        arrayOf(it[Trainer.trainerFirstName],
                it[Trainer.trainerLastName]
        )
      }

      val firstNameField = form.blockWithSaveCommand.trainerFirstName.findField()
      val lastNameField = form.blockWithSaveCommand.trainerLastName.findField()

      assertEquals(firstNameField.value, data[0][0])
      assertEquals(lastNameField.value, data[0][1])
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
