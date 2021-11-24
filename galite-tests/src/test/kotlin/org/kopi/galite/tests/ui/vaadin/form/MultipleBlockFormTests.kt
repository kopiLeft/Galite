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
package org.kopi.galite.tests.ui.vaadin.form

import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.expect
import org.kopi.galite.testing.findBlock
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.findForm
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.testing.waitAndRunUIQueue
import org.kopi.galite.tests.examples.FormToTestSaveMultipleBlock
import org.kopi.galite.tests.examples.MultipleBlockForm
import org.kopi.galite.tests.examples.initDatabase
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.form.DForm
import org.kopi.galite.visual.ui.vaadin.form.DGridBlock
import org.kopi.galite.visual.ui.vaadin.form.DListDialog
import org.kopi.galite.visual.ui.vaadin.list.ListTable
import org.kopi.galite.visual.ui.vaadin.main.VWindowsMenuItem

import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._expect
import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.github.mvysny.kaributesting.v10.expectRow
import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.textfield.TextField
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.tests.examples.initData

class MultipleBlockFormTests : GaliteVUITestBase() {

  val multipleBlockSaveForm = FormToTestSaveMultipleBlock().also { it.model }
  val multipleForm = MultipleBlockForm().also { it.model }

  @Before
  fun `login to the App`() {
    login()
    multipleForm.open()
  }

  /**
   * click on changeBlock commands,
   * check that list dialog with correct name of blocks is displayed
   */
  @Test
  fun `test changeBlock command`() {
    var blockCaption = _get<H4> { classes = "block-title" }

    assertEquals(multipleForm.formBlocks[1].title, blockCaption.text)
    multipleForm.changeBlock.triggerCommand()
    // Check that the list dialog is displayed
    _expectOne<DListDialog>()

    // Check that the list dialog contains a grid
    val listDialog = _get<DListDialog>()
    listDialog._expectOne<Grid<*>>()

    // Check that the grid data is correct
    val grid = _get<DListDialog>()._get<ListTable>()

    grid.expect(arrayOf(
      arrayOf(multipleForm.formBlocks[1].title),
      arrayOf(multipleForm.formBlocks[2].title)
    ))

    // Choose second row
    grid.selectionModel.selectFromClient(grid.dataCommunicator.getItem(1))

    waitAndRunUIQueue(100)

    blockCaption = _get<H4> { classes = "block-title" }

    // Dialog is closed and the block title is correct
    assertFalse(listDialog.isOpened)
    assertEquals(multipleForm.formBlocks[2].title, blockCaption.text)
  }

  /**
   * click on showHideFilter commands,
   * check that new row of filter is displayed in the grid
   */
  @Test
  fun `test showHideFilter command`() {
    multipleForm.block.trainingID.edit(1)
    multipleForm.list.triggerCommand()
    multipleForm.block2.enter()
    multipleForm.showHideFilter.triggerCommand()

    val block = multipleForm.block2.findBlock() as DGridBlock
    val data = arrayOf(
      arrayOf("Center 1", "10,Rue Lac", "example@mail", "Tunisia", "Megrine", "2001"),
      arrayOf("Center 2", "14,Rue Mongi Slim", "example@mail", "Tunisia", "Tunis", "6000")
    )

    data.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }

    val filter = _find<TextField> { classes = "block-filter-text" }

    filter[0]._value = "2"
    waitAndRunUIQueue(500)

    block.grid.expect(arrayOf(
      arrayOf("Center 2", "14,Rue Mongi Slim", "example@mail", "Tunisia", "Tunis", "6000")
    ))
  }

  /**
   * click on load commands,
   * check that first and second block contains data
   */
  @Test
  fun `test load command`() {
    multipleForm.load.triggerCommand()

    val field = multipleForm.block.trainingID.findField()
    val block = multipleForm.block2.findBlock() as DGridBlock

    assertEquals("1", field.value)
    val data = arrayOf(
      arrayOf("Center 1", "10,Rue Lac", "example@mail", "Tunisia", "Megrine", "2001"),
      arrayOf("Center 2", "14,Rue Mongi Slim", "example@mail", "Tunisia", "Tunis", "6000")
    )

    data.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }
  }

  /**
   * click on queryMove commands,
   * check that first and second block contains data
   */
  @Test
  fun `test queryMove command`() {
    multipleForm.query.triggerCommand()

    // Check that the list dialog contains a grid
    val listDialog = _get<DListDialog>()
    listDialog._expectOne<Grid<*>>()

    // Check that the grid data is correct
    val grid = _get<DListDialog>()._get<ListTable>()

    grid.expect(arrayOf(
      arrayOf("1", "training 1", "Java", "1.149,240", "yes", "informations training 1"),
      arrayOf("2", "training 2", "Galite", "219,600", "yes", "informations training 2"),
      arrayOf("3", "training 3", "Kotlin", "146,900", "yes", "informations training 3"),
      arrayOf("4", "training 4", "Galite", "3.129,700", "yes", "informations training 4"),
    ))

    // Choose third row
    grid.selectionModel.selectFromClient(grid.dataCommunicator.getItem(2))

    waitAndRunUIQueue(100)

    val field = multipleForm.block.trainingID.findField()
    val block = multipleForm.block2.findBlock() as DGridBlock

    // Dialog is closed and row data are filled into the form
    assertFalse(listDialog.isOpened)
    assertEquals("3", field.value)
    val data = arrayOf(
      arrayOf("Center 3", "10,Rue du Lac", "example@mail", "Tunisia", "Mourouj", "5003"),
    )

    data.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }
  }

  @Test
  fun `test add command`() {
    val form = MultipleBlockForm().also { it.model }
    form.query.triggerCommand()

    // Check that the list dialog contains a grid
    val listDialog = _get<DListDialog>()
    listDialog._expectOne<Grid<*>>()

    // Check that the grid data is correct
    val grid = _get<DListDialog>()._get<ListTable>()

    // Choose first row
    grid.selectionModel.selectFromClient(grid.dataCommunicator.getItem(1))

    waitAndRunUIQueue(100)

    val block = form.block2.findBlock() as DGridBlock
    var data = arrayOf(
      arrayOf("Center 1", "10,Rue Lac", "example@mail", "Tunisia", "Megrine", "2001"),
    )

    data.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }
    // Command is enabled as the query command navigate automatically to next block
    form.add.triggerCommand()

    // the added row will take the position of the current focused record
    assertEquals(0, block.editor.item.record)
    data = arrayOf(
      arrayOf("", "", "", "", "", ""),
      arrayOf("Center 1", "10,Rue Lac", "example@mail", "Tunisia", "Megrine", "2001"),
    )

    data.forEachIndexed { index, row ->
      block.editor.grid.expectRow(index, *row)
    }
  }

  @Test
  fun `test tabs command`() {
    val form = multipleForm.findForm()
    val tabsBeforeChangingPage = _find<Tab>{}
    val currentPage = 0

    assertTrue(tabsBeforeChangingPage[currentPage].isSelected)
    val targetPage = 1

    assertFalse(tabsBeforeChangingPage[targetPage].isSelected)
    form.gotoPage(targetPage)

    val tabsAfterChangingPage = _find<Tab>{}

    assertTrue(tabsAfterChangingPage[targetPage].isSelected)
  }

  @Test
  fun `test save data then enter block then enter next record`() {
    multipleBlockSaveForm.open()
    multipleBlockSaveForm.list.triggerCommand()
    multipleBlockSaveForm.multipleBlock.enter()
    multipleBlockSaveForm.multipleBlock.address.edit("new address")
    multipleBlockSaveForm.saveBlock.triggerCommand()
    val block = multipleBlockSaveForm.multipleBlock.findBlock() as DGridBlock
    val data = arrayOf(
      arrayOf("2", "Center 1", "new address", "example@mail"),
      arrayOf("1", "Center 1", "10,Rue Lac", "example@mail"),
      arrayOf("1", "Center 2", "14,Rue Mongi Slim", "example@mail"),
      arrayOf("3", "Center 3", "10,Rue du Lac", "example@mail"),
      arrayOf("4", "Center 4", "10,Rue du Lac", "example@mail")
    )

    data.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }

    assertEquals(1, block.editor.item.record)
//    reverting changes to avoid affecting other tests
    multipleBlockSaveForm.block.enter()
    multipleBlockSaveForm.multipleBlock.enter()
    multipleBlockSaveForm.multipleBlock.vBlock.activeRecord = 0
    multipleBlockSaveForm.multipleBlock.address.edit("10,Rue Lac")
    multipleBlockSaveForm.saveBlock.triggerCommand()
    val newdata = arrayOf(
      arrayOf("2", "Center 1", "10,Rue Lac", "example@mail"),
      arrayOf("1", "Center 1", "10,Rue Lac", "example@mail"),
      arrayOf("1", "Center 2", "14,Rue Mongi Slim", "example@mail"),
      arrayOf("3", "Center 3", "10,Rue du Lac", "example@mail"),
      arrayOf("4", "Center 4", "10,Rue du Lac", "example@mail")
    )

    newdata.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }
    transaction {
      initData()
    }
  }

  @Test
  fun `test the actors shortcut dialog`() {
    val button = _get<Button> { classes = "actors-rootNavigationItem" }

    button.click()
    _expect<Dialog> {  }
    val dialog = _get<Dialog> {}

    assertNotNull(dialog)

    val navigationColumns = _find<VerticalLayout> { classes= "actor-navigationColumn" }

    assertEquals(2, navigationColumns.size)
    assertEquals(2, navigationColumns[0].children.count())
    assertEquals(10, navigationColumns[1].children.count())

    val headers = _find<Span> { classes = "header" }.map { it._get<Div> {  } }

    assertEquals("File", headers[0].text)
    assertEquals("Action", headers[1].text)

    val firstColumnItems = navigationColumns[0]._find<Span> { classes = "actor-navigationItem" }.map { it._get <Div>{  }.text }
    val secondColumnItems = navigationColumns[1]._find<Span> { classes = "actor-navigationItem" }.map { it._get <Div>{  }.text }

    assertEquals(listOf("Shortcuts"), firstColumnItems)
    // "break", "ShowHideFilter" and  "add" actors will be ignored because they are disabled
    assertEquals(listOf("list","query","load","change Block","resetForm"), secondColumnItems)
  }

  @Test
  fun `test the switch window button`() {
    // multiple form is opened, open a second form: multipleBlockSaveForm
    multipleBlockSaveForm.open()
    // get back to multiple form and verify that the form is visible
    val windowsDiv = _get<Div> { id = "windows" }

    windowsDiv._click()
    _expect<EnhancedDialog> {  }

    var windowsContainer = _get<VerticalLayout> { classes = "window-items-container" }
    var windowsItems = windowsContainer._find<VWindowsMenuItem> { classes = "item" }

    assertEquals(2, windowsItems.size)
    windowsItems[0].click()
    _expectNone<EnhancedDialog>()

    var form = multipleForm.findForm()
    var visibleForm = _get<DForm> {  }

    assertEquals(form, visibleForm)
    assertFails { multipleBlockSaveForm.findForm() }

    // get back to multipleBlockSaveForm
    windowsItems[1].click()
    _expectNone<EnhancedDialog>()
    form = multipleBlockSaveForm.findForm()
    visibleForm = _get<DForm> {  }
    assertEquals(form, visibleForm)
    assertFails { multipleForm.findForm() }
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      initDatabase()
    }
  }
}
