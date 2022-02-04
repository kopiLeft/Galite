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

import java.math.BigDecimal

import kotlin.test.assertEquals
import kotlin.test.assertFalse

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editRecord
import org.kopi.galite.testing.editText
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.expectConfirmNotification
import org.kopi.galite.testing.expectErrorNotification
import org.kopi.galite.testing.expectInformationNotification
import org.kopi.galite.testing.findBlock
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.BoolCode
import org.kopi.galite.tests.examples.DecimalCode
import org.kopi.galite.tests.examples.DocumentationFieldsForm
import org.kopi.galite.tests.examples.initDocumentationData
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.examples.IntCode
import org.kopi.galite.tests.examples.StringCode
import org.kopi.galite.tests.examples.TestTable
import org.kopi.galite.tests.examples.TestTable2
import org.kopi.galite.tests.examples.TestTriggers
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.field.VCodeField
import org.kopi.galite.visual.ui.vaadin.field.VPasswordField
import org.kopi.galite.visual.ui.vaadin.form.DBlock
import org.kopi.galite.visual.ui.vaadin.form.DGridBlock
import org.kopi.galite.visual.ui.vaadin.form.DListDialog
import org.kopi.galite.visual.ui.vaadin.list.ListTable
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification
import org.kopi.galite.visual.visual.MessageCode

import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.github.mvysny.kaributesting.v10.expectRow
import com.github.mvysny.kaributesting.v10.expectRows
import com.github.mvysny.kaributesting.v10.getSuggestionItems
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.data.provider.SortDirection

class DocumentationFieldsFormTests : GaliteVUITestBase() {
  val form = DocumentationFieldsForm()

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
  }

  @Test
  fun `test Convert Upper`() {
    val text = "input"
    val field = form.fieldsTypesBlock.string2.findField()

    form.fieldsTypesBlock.string2.edit(text)
    form.fieldsTypesBlock.string3.click()

    assertEquals(text.toUpperCase(), field._value)
  }

  @Test
  fun `test Convert Lower`() {
    val text = "INPUT"
    val field = form.fieldsTypesBlock.string3.findField()

    form.fieldsTypesBlock.string3.edit(text)
    form.fieldsTypesBlock.string2.click()

    assertEquals(text.toLowerCase(), field._value)
  }

  @Test
  fun `test Convert Name`() {
    val text = "INPUT TEXT"
    val field = form.fieldsTypesBlock.string4.findField()

    form.fieldsTypesBlock.string4.edit(text)
    form.fieldsTypesBlock.string2.click()

    assertEquals("Input Text", field._value)
  }

  @Test
  fun `test Decimal minValue`() {
    val value = BigDecimal("1.5")

    form.fieldsTypesBlock.decimal.edit(value)
    form.fieldsTypesBlock.int.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00012", BigDecimal("1.9")))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test Decimal maxValue`() {
    val value = BigDecimal("6.5")

    form.fieldsTypesBlock.decimal.edit(value)
    form.fieldsTypesBlock.int.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00009", BigDecimal("5.9")))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test int minValue`() {
    val value = 0

    form.fieldsTypesBlock.int.edit(value)
    form.fieldsTypesBlock.decimal.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00012", 1))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test int maxValue`() {
    val value = 110

    form.fieldsTypesBlock.int.edit(value)
    form.fieldsTypesBlock.decimal.click()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00009", 100))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test codeDomain`() {
    val booleanCodeField = _find<VCodeField> {}[0]
    val booleanList = BoolCode.codes.map { it.label }.toList()

    assertCollectionsEquals(booleanList, booleanCodeField.content.getSuggestionItems())

    val intCodeField = _find<VCodeField> {}[1]
    val intList = IntCode.codes.map { it.label }.toList()

    assertCollectionsEquals(intList, intCodeField.content.getSuggestionItems())

    val decimalCodeField = _find<VCodeField> {}[2]
    val decimalList = DecimalCode.codes.map { it.label }.toList()

    assertCollectionsEquals(decimalList, decimalCodeField.content.getSuggestionItems())

    val stringCodeField = _find<VCodeField> {}[3]
    val stringList = StringCode.codes.map { it.label }.toList()

    assertCollectionsEquals(stringList, stringCodeField.content.getSuggestionItems())
  }

  @Test
  fun `test NOECHO`() {
    _expectOne<VPasswordField> {  }
  }

  @Ignore
  @Test
  fun `test SORTABLE`() {
    val block = form.sortableMultiBlock.findBlock() as DGridBlock

    form.sortableMultiBlock.enter()

    form.sortableMultiBlock.sortable.edit("2")
    form.sortableMultiBlock.editRecord(1)
    form.sortableMultiBlock.sortable.edit("3")
    form.sortableMultiBlock.editRecord(2)
    form.sortableMultiBlock.sortable.edit("1")
    form.sortableMultiBlock.editRecord(3)

    val data = arrayOf(
      arrayOf("2"),
      arrayOf("3"),
      arrayOf("1")
    )

    data.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }

    val orderDsc = GridSortOrder(block.grid.columns[0], SortDirection.DESCENDING)

    block.grid.sort(listOf(orderDsc))

    val sortedDataDsc = arrayOf(
      arrayOf("3"),
      arrayOf("2"),
      arrayOf("1")
    )

    sortedDataDsc.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }

    val orderAsc = GridSortOrder(block.grid.columns[0], SortDirection.ASCENDING)

    block.grid.sort(listOf(orderAsc))

    val sortedDataAsc = arrayOf(
      arrayOf("1"),
      arrayOf("2"),
      arrayOf("3")
    )

    sortedDataAsc.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }
  }

  @Test
  fun `test NOEDIT`() {
    val field = form.fieldsOptionsBlock.noEdit.findField()

    assertEquals(true, field.isReadOnly)
  }

  @Test
  fun `test QUERY UPPER and QUERRY LOWER`() {
    transaction {
      initDocumentationData()
    }

    form.queryBlock.queryUpper.edit("na*")
    form.queryBlock.queryLower.editText("LAST*")

    form.serialQuery.triggerCommand()

    transaction {
      val data = TestTable.select { (TestTable.name like  "NA%") and (TestTable.lastName like  "last%") }.map {
        arrayOf(
          it[TestTable.name],
          it[TestTable.lastName]
        )
      }

      val nameField = form.queryBlock.queryUpper.findField()
      val lastNameField = form.queryBlock.queryLower.findField()

      assertEquals(nameField.value, data[0][0])
      assertEquals(lastNameField.value, data[0][1])
    }
  }

  @Test
  fun `test columns priority`() {
    transaction {
      initDocumentationData()
    }

    form.priorityAndIndexBlock.enter()
    // Trigger the list command
    form.list.triggerCommand()

    // Check that the list dialog is displayed
    _expectOne<DListDialog>()

    // Check that the list dialog contains a grid
    val listDialog = _get<DListDialog>()
    listDialog._expectOne<Grid<*>>()

    // Check that the grid data is correct
    val grid = _get<DListDialog>()._get<ListTable>()
    val data = arrayOf(
      arrayOf("NAME", "3"),
      arrayOf("TEST-1", "1"),
      arrayOf("TEST-2", "2")
    )

    grid.expectRows(data.size)

    data.forEachIndexed { index, it ->
      grid.expectRow(index, *it)
    }
  }

 /* @Test
  fun `test after changing columns priority order`() {
    transaction {
      initDocumentationData()
    }

    form.priorityAndIndexBlock.id.columns!!.priority = 9
    form.priorityAndIndexBlock.name.columns!!.priority = 1

    form.priorityAndIndexBlock.enter()

    // Trigger the list command
    form.list.triggerCommand()

    // Check that the list dialog is displayed
    _expectOne<DListDialog>()

    // Check that the list dialog contains a grid
    val listDialogAfterChangePriority = _get<DListDialog>()
    listDialogAfterChangePriority._expectOne<Grid<*>>()

    // Check that the grid data is correct
    val gridAfterChangePriority = _get<DListDialog>()._get<ListTable>()
    val dataAfterChangePriority = arrayOf(
      arrayOf("3", "NAME"),
      arrayOf("1", "TEST-1"),
      arrayOf("2", "TEST-2")
    )

    gridAfterChangePriority.expectRows(dataAfterChangePriority.size)

    dataAfterChangePriority.forEachIndexed { index, it ->
      gridAfterChangePriority.expectRow(index, *it)
    }
  }*/

  @Test
  fun `test columns index`() {
    transaction {
      initDocumentationData()
    }

    val index = form.priorityAndIndexBlock.i.message

    form.priorityAndIndexBlock.enter()
    form.priorityAndIndexBlock.name.edit("NAME")

    // Trigger the save command
    form.saveBlock.triggerCommand()

    // Check that the error notification is displayed
    _expectOne<ErrorNotification>()

    // Check that error notification display message, then close the error notification and check that's disappearing
    expectErrorNotification(MessageCode.getMessage("VIS-00014", index))
    _expectNone<ErrorNotification>()
  }

  @Test
  fun `test columns inner join`() {
    transaction {
      initDocumentationData()
    }

    form.innerJoinBlock.enter()
    val field = form.innerJoinBlock.innerJoinColumns.findField()

    // Trigger the list command
    form.list.triggerCommand()

    transaction {
      val data = TestTable.join(TestTable2, JoinType.INNER, TestTable.id, TestTable2.refTable1)
        .selectAll().first()[TestTable.id]

      assertEquals(data.toString(), field._value) // check cast !
    }
  }

  @Test
  fun `test PREFLD trigger`() {
    form.triggersFieldsBlock.enter()

    val field = form.triggersFieldsBlock.preFldTriggerField.findField()

    form.triggersFieldsBlock.preFldTriggerField.click()

      assertEquals("PREFLD Trigger", field._value)
  }

  @Test
  fun `test POSTFLD trigger`() {
    form.triggersFieldsBlock.enter()

    val field = form.triggersFieldsBlock.postFldTriggerField.findField()

    // enter field then leave it to call POSTFLD trigger
    form.triggersFieldsBlock.postFldTriggerField.click()
    form.triggersFieldsBlock.preFldTriggerField.click()

    assertEquals("POSTFLD Trigger", field._value)
  }

  @Test
  fun `test POSTCHG trigger`() {
    form.triggersFieldsBlock.enter()

    val field = form.triggersFieldsBlock.postChgTriggerField.findField()

    // enter field pout value then leave it to call POSTCHG trigger
    form.triggersFieldsBlock.postChgTriggerField.click()
    form.triggersFieldsBlock.postChgTriggerField.edit("value")
    form.triggersFieldsBlock.preFldTriggerField.click()

    assertEquals("POSTCHG Trigger", field._value)
  }

  @Test
  fun `test PREVAL trigger`() {
    form.triggersFieldsBlock.enter()

    val field = form.triggersFieldsBlock.preValTriggerField.findField()

    // put value in this field then leave it to call PREVAL trigger (executed before validate field)
    form.triggersFieldsBlock.preValTriggerField.click()
    form.triggersFieldsBlock.preValTriggerField.edit("value")
    form.triggersFieldsBlock.preFldTriggerField.click()

    assertEquals("PREVAL Trigger", field._value)
  }

  @Test
  fun `test VALFLD trigger`() {
    form.triggersFieldsBlock.enter()

    val field = form.triggersFieldsBlock.valFldTriggerField.findField()

    // put value in this field then leave it to call VALFLD trigger (executed when validates the field)
    form.triggersFieldsBlock.valFldTriggerField.click()
    form.triggersFieldsBlock.valFldTriggerField.edit("value")
    form.triggersFieldsBlock.preFldTriggerField.click()

    assertEquals("VALFLD Trigger", field._value)
  }

  @Test
  fun `test VALIDATE trigger`() {
    form.triggersFieldsBlock.enter()

    val field = form.triggersFieldsBlock.validateTriggerField.findField()

    // put value in this field then leave it to call VALFLD trigger (executed when validates the field)
    form.triggersFieldsBlock.validateTriggerField.click()
    form.triggersFieldsBlock.validateTriggerField.edit("value")
    form.triggersFieldsBlock.preFldTriggerField.click()

    assertEquals("VALIDATE Trigger", field._value)
  }

  @Test
  fun `test DEFAULT trigger`() {
    form.triggersFieldsBlock.enter()
    form.insertMode.triggerCommand()

    val field = form.triggersFieldsBlock.defaultTriggerField.findField()

    assertEquals("DEFAULT Trigger", field._value)
  }

  @Test
  fun `test ACCESS trigger`() {
    val blocks = _find<DBlock>()
    val block = blocks.single { it.model.title == "Block to test: Field Triggers" }
    // the visibility of the filed is changed to skipped by the ACCESS trigger
    assertFalse(block._get<Div> { classes = "skipped"}.isEnabled)
  }

  @Test
  fun `test VALUE trigger`() {
    form.triggersFieldsBlock.enter()

    val field = form.triggersFieldsBlock.valueTriggerField.findField()

    assertEquals("VALUE Trigger", field._value)
  }

  @Ignore
  @Test
  fun `test AUTOLEAVE trigger`() {
    form.triggersFieldsBlock.enter()

    form.triggersFieldsBlock.autoleaveTriggerField.click()
    // enter letter in this field then assert that th AUTOLEAVE trigger is called and the activate field is set to nextField
    form.triggersFieldsBlock.autoleaveTriggerField.edit("a")

    assertEquals(form.triggersFieldsBlock.preInsTriggerField.vField, form.triggersFieldsBlock.block.activeField)
  }

  @Test
  fun `test PREINS and POSTINS trigger`() {
    transaction {
      initDocumentationData()
    }

    // PREINS : click on insertMode command then save command and assert that PREINS trigger change the field value
    form.triggersFieldsBlock.enter()
    form.insertMode.triggerCommand()
    form.saveBlock.triggerCommand()

    transaction {
      assertEquals("PREINS Trigger", TestTriggers.selectAll().last()[TestTriggers.INS])
    }

    // POSTINS : click on insertMode command then save command and assert that POSTINS trigger change the field value of the lastBlock
    val field = form.lastBlock.postInsTriggerField.findField()

    transaction {
      assertEquals("POSTINS Trigger", field._value)
    }
  }

  @Test
  fun `test PREUPD and POSTUPD trigger`() {
    transaction {
      initDocumentationData()
    }

    // PREUPD : click on list command then save command and assert that PREUPD trigger change the field value
    form.triggersFieldsBlock.enter()
    form.list.triggerCommand()
    form.triggersFieldsBlock.preUpdTriggerField.click()
    form.triggersFieldsBlock.preUpdTriggerField.edit("a")
    form.saveBlock.triggerCommand()

    // POSTUPD : click on list command then save command and assert that POSTUPD trigger show an Information Notification
    expectInformationNotification("POSTUPD Trigger")

    transaction {
      assertEquals("PREUPD Trigger", TestTriggers.selectAll().last()[TestTriggers.UPD])
    }
  }

  @Test
  fun `test PREDEL trigger`() {
    transaction {
      initDocumentationData()
    }

    form.triggersFieldsBlock.enter()
    form.list.triggerCommand()
    form.deleteBlock.triggerCommand()

    expectConfirmNotification(true)

    // PREDEL : click on list command then deleteBlock command and assert that PREDEL trigger show an Information Notification
    expectInformationNotification("PREDEL Trigger")
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
