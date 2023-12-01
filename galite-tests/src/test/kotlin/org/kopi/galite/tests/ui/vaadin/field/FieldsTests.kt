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
package org.kopi.galite.tests.ui.vaadin.field

import com.github.mvysny.kaributesting.v10.*
import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.IronIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.database.Users
import org.kopi.galite.testing.*
import org.kopi.galite.tests.examples.TestFieldsForm
import org.kopi.galite.tests.examples.Trainer
import org.kopi.galite.tests.examples.initData
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FieldsTests : GaliteVUITestBase() {

  val form = TestFieldsForm()

  @Before
  fun `login to the App`() {
    transaction {
      initData()
    }

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

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00023"))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test maxValue option`() {
    //put value greater than 50 in the first field
    form.blockWithDifferentTypes.intField.edit(70)
    form.blockWithDifferentTypes.decimalField.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00009", 50))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test minValue option`() {
    //put value less than 10 in the first field
    form.blockWithDifferentTypes.intField.edit(1)
    form.blockWithDifferentTypes.decimalField.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00012", 10))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test big number error`() {
    //put value less greater than max (50)
    form.blockWithDifferentTypes.intField.edit(1111)
    form.blockWithDifferentTypes.decimalField.click()
    expectErrorNotification(MessageCode.getMessage("VIS-00009", 50))
  }

  @Test
  fun `test invalid decimal error`() {
    //put value with invalid scale (> 2)
    form.blockWithDifferentTypes.decimalField.edit(BigDecimal("11111111.1111111111"))
    form.blockWithDifferentTypes.intField.click()
    expectErrorNotification(MessageCode.getMessage("VIS-00011", 2))
  }

  @Test
  fun `test decimal field`() {
    form.blockWithDifferentTypes.decimalField.edit(BigDecimal("999999"))
    form.blockWithDifferentTypes.intField.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00009", 999.99))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test convert UPPER for string field`() {
    val fields = _find<VerticalLayout> {classes = "k-block" }[0]._find<Div> { classes = "k-field"}
    val input = "test"

    form.blockWithDifferentTypes.upperStringField.edit(input)
    form.blockWithDifferentTypes.decimalField.click()
    assertEquals(input.uppercase(), fields[0]._text)
  }

  @Test
  fun `test convert LOWER for string field`() {
    val fields = _find<VerticalLayout> {classes = "k-block" }[0]._find<Div> { classes = "k-field"}
    val input = "TEST"

    form.blockWithDifferentTypes.lowerStringField.edit(input)
    form.blockWithDifferentTypes.decimalField.click()
    assertEquals(input.lowercase(), fields[2]._text)
  }

  @Test
  fun `test convert Name for string field`() {
    val fields = _find<VerticalLayout> {classes = "k-block" }[0]._find<Div> { classes = "k-field"}
    val input = "test"

    form.blockWithDifferentTypes.nameStringField.edit(input)
    form.blockWithDifferentTypes.decimalField.click()
    assertEquals(input.substring(0,1).uppercase() + input.substring(1), fields[1]._text)
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

  @Test
  fun `open form via field`() {
    val form = FormToTestFormPopUp()
    form.open()

    val field = form.userListBlock.user.findField() as Focusable<*>

    field.focus()

    val icon = (field as Component)._get<IronIcon> {  }

    icon._clickAndWait(500)

    // Check that the form is displayed id popUp
    _expectOne<EnhancedDialog>()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        initModules()
      }
    }
  }
}

class FormToTestFormPopUp: Form(title = "apperation of form in popup", locale = Locale.UK) {
  val edit = menu("Edit")

  val autoFill = actor(menu = edit, label = "Autofill", help = "Autofill", command = PredefinedCommand.AUTOFILL)
  override val newItem = actor(menu = edit, label = "NewItem", help = "NewItem", command = PredefinedCommand.NEW_ITEM)
  override val editItem = actor(menu = edit, label = "Edit Item", help = "Edit Item", command = PredefinedCommand.EDIT_ITEM)

  val userListBlock = insertBlock(UsersListBlock()) {}

  inner class UsersListBlock : Block("UsersListBlock", 1, 1) {
    val user = mustFill(domain = UsersList(), position = at(1, 1)) {
      label = "user"
      help = "The user"
    }
  }

  inner class UsersList: ListDomain<Int>(20) {
    override val table = Users
    override val access = {
      FormInPopUp()
    }
  }
}

class FormInPopUp : DictionaryForm(title = "form for test", locale = Locale.UK) {
  val action = menu("Action")

  val autoFill = actor(menu = action, label = "Autofill", help = "Autofill", command = PredefinedCommand.AUTOFILL)
  override val quit = actor(menu = action, label = "quit", help = "Quit") {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }
  val quitCmd = command(item = quit) {
    quitForm()
  }

  val block = insertBlock(UsersBlock()) {}

  inner class UsersBlock : Block("Test block", 1, 1) {
    val shortName = visit(domain = STRING(20), position = at(1, 1)) {
      label = "Kurzname"
      help = "Kurzname"
    }
    val name = visit(domain = STRING(20), position = at(2, 1)) {
      label = "name"
      help = "name"
    }
  }
}
