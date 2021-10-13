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

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.findBlock
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.form.DGridBlock
import org.junit.Assert.assertFalse
import org.kopi.galite.testing.expect
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.waitAndRunUIQueue
import org.kopi.galite.tests.examples.initDatabase
import org.kopi.galite.visual.ui.vaadin.form.DListDialog
import org.kopi.galite.visual.ui.vaadin.list.ListTable
import org.kopi.galite.tests.examples.Center
import org.kopi.galite.tests.examples.Traineeship
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.visual.db.transaction
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.insertBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.visual.VExecFailedException
import org.kopi.galite.testing.expectConfirmNotification

import com.github.mvysny.kaributesting.v10.expectRow
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.textfield.TextField
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value

class MultipleBlockFormTests : GaliteVUITestBase() {

  val multipleBlockSaveForm = FormToTestSaveMultipleBlock().also { it.model }
  val multipleForm = MultipleBlockForm().also { it.model }

  @Before
  fun `login to the App`() {
    login()
    multipleForm.open()
  }

  @Test
  fun `test list command`() {
    //TODO
    /*
      check that the list dialog is displayed & that contain a correct data,
      then chose a row and check that first and second block contains data
     */
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
    //TODO
    /*
      in the second block click on add commands
     */
  }

  @Test
  fun `test tabs command`() {
    //TODO
    /*
      click on the second tabs and display new page
     */
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
  }

  /**
   * put a value in the first field of the first block and a value in the first field of second block
   * then click on resetBlock button,
   * check that a popup is displayed,
   * click on yes and check that the fields are empty
   */
  @Test
  fun `test resetForm command`() {
    multipleForm.open()

    val simpleField = multipleForm.block.trainingID.findField()
    val multipleField = multipleForm.block2.centerName.findField()

    multipleForm.block.trainingID.edit(10)
    multipleForm.block2.centerName.edit("center name")
    assertEquals("10", simpleField.value)
    assertEquals("center name", multipleField.value)
    multipleForm.resetForm.triggerCommand()
    expectConfirmNotification(true)
    assertEquals("", simpleField.value)
    assertEquals("", multipleField.value)
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      initDatabase()
    }
  }
}

class FormToTestSaveMultipleBlock : DictionaryForm() {
  override val locale = Locale.UK

  override val title = "Training Form"
  val action = menu("Action")
  val saveBlock = actor(
    ident = "saveBlock",
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F9
    icon = "save"
  }
  val list = actor(
    ident = "list",
    menu = action,
    label = "list",
    help = "List data",
  ) {
    key = Key.F5
    icon = "list"
  }
  val block = insertBlock(Trainee())
  val multipleBlock = insertBlock(Centers())

  inner class Trainee: FormBlock(1, 1, "Training") {
    val t = table(Training)

    val trainingID = visit(domain = INT(25), position = at(1, 1)) {
      label = "training ID"
      help = "training ID"
      columns(t.id) {
        priority = 1
      }
    }

    init {
      trigger(POSTQRY) {
        multipleBlock.load()
      }

      command(item = list) {
        action = {
          transaction {
            recursiveQuery()
          }
        }
      }
    }
  }

  inner class Centers : FormBlock(20, 20, "Centers") {
    val c = table(Center)

    val centerId = hidden(domain = INT(20)) {
      label = "center id"
      help = "The Center id"
      columns(c.id)
    }
    val ts = hidden(domain = INT(20)) {
      label = "ts"
      value = 0
      columns(c.ts)
    }
    val uc = hidden(domain = INT(20)) {
      label = "uc"
      value = 0
      columns(c.uc)
    }
    val trainingId = visit(domain = INT(20), position = at(1, 1)) {
      label = "training id"
      help = "The training id"
      columns(c.refTraining)
    }
    val centerName = visit(domain = STRING(20), position = at(1, 1)) {
      label = "center name"
      help = "center name"
      columns(c.centerName)
    }
    val address = visit(domain = STRING(20), position = at(1, 2)) {
      label = "address"
      help = "address"
      columns(c.address)
    }
    val mail = visit(domain = STRING(20), position = at(1, 3)) {
      label = "mail"
      help = "mail"
      columns(c.mail)
    }

    init {
      command(item = saveBlock) {
        action = {
          val b = vBlock
          val rec: Int = b.activeRecord

          b.validate()

          if (!b.isFilled()) {
            b.currentRecord = 0
            throw VExecFailedException()
          }

          transaction {
            b.save()
          }

          b.form.gotoBlock(b)
          b.gotoRecord(if (b.isRecordFilled(rec)) rec + 1 else rec)
        }
      }
    }
  }
}

class MultipleBlockForm : DictionaryForm() {
  override val locale = Locale.UK

  override val title = "Training Form"
  val page1 = page("page1")
  val page2 = page("page2")
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )
  val list = actor(
    ident = "list",
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F2
    icon = "list"
  }
  val query = actor(
    ident = "query",
    menu = action,
    label = "query",
    help = "query",
  ) {
    key = Key.F3
    icon = "list"
  }
  val load = actor(
    ident = "load",
    menu = action,
    label = "load",
    help = "load",
  ) {
    key = Key.F8
    icon = "list"
  }
  val changeBlock = actor(
    ident = "change Block",
    menu = action,
    label = "change Block",
    help = "change Block",
  ) {
    key = Key.F4
    icon = "refresh"
  }
  val resetBlock = actor(
    ident = "reset",
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F5
    icon = "break"
  }
  val showHideFilter = actor(
    ident = "ShowHideFilter",
    menu = action,
    label = "ShowHideFilter",
    help = " Show Hide Filter",
  ) {
    key = Key.F6
    icon = "searchop"
  }
  val add = actor(
    ident = "add",
    menu = action,
    label = "add",
    help = " add",
  ) {
    key = Key.F10
    icon = "add"
  }
  val resetForm = actor(
    ident = "resetForm",
    menu = action,
    label = "resetForm",
    help = "Reset Form",
  ) {
    key = Key.F7
    icon = "break"
  }
  val resetFormCmd = command(item = resetForm) {
    action = {
      resetForm()
    }
  }

  val block = page1.insertBlock(Traineeship()) {
    trigger(POSTQRY) {
      block2.trainingId[0] = trainingID.value
      block2.load()
    }

    command(item = list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }
    command(item = query) {
      action = {
        queryMove()
      }
    }
    command(item = load) {
      action = {
        transaction {
          load()
        }
      }
    }
    command(item = changeBlock) {
      action = {
        changeBlock()
      }
    }
  }

  val block2 = insertBlock(Centers(), page1) {
    command(item = resetBlock) {
      action = {
        resetBlock()
      }
    }
    command(item = showHideFilter) {
      action = {
        showHideFilter()
      }
    }
    command(item = add) {
      action = {
        insertLine()
      }
    }
  }
  val block3 = insertBlock(SimpleBlock(), page2)

  inner class Centers : FormBlock(20, 20, "Centers") {
    val c = table(Center)
    val t = table(Training)

    val unique = index(message = "ID should be unique")

    val CenterId = hidden(domain = INT(20)) {
      label = "center id"
      help = "The Center id"
      columns(c.id) {
        index = unique
      }
    }
    val trainingId = hidden(domain = INT(20)) {
      label = "training id"
      help = "The training id"
      columns(c.refTraining, t.id) {
        index = unique
      }
    }
    val centerName = visit(domain = STRING(20), position = at(1, 1)) {
      label = "center name"
      help = "center name"
      columns(c.centerName)
    }
    val address = visit(domain = STRING(20), position = at(1, 2)) {
      label = "address"
      help = "address"
      columns(c.address)
    }
    val mail = visit(domain = STRING(20), position = at(1, 3)) {
      label = "mail"
      help = "mail"
      columns(c.mail)
    }
    val country = visit(domain = STRING(20), position = at(1, 4)) {
      label = "country"
      help = "country"
      columns(c.country)
    }
    val city = visit(domain = STRING(20), position = at(1, 5)) {
      label = "city"
      help = "city"
      columns(c.city)
    }
    val zipCode = visit(domain = INT(5), position = at(1, 6)) {
      label = "zipCode"
      help = "zipCode"
      columns(c.zipCode)
    }

    init {
      border = VConstants.BRD_LINE
    }
  }

  inner class SimpleBlock : FormBlock(1, 1, "Simple block") {
    val contact = visit(domain = STRING(20), position = at(1, 1)) {
      label = "contact"
      help = "The contact"
    }
    val name = visit(domain = STRING(20), position = follow(contact)) {}

    init {
      border = VConstants.BRD_LINE
    }
  }
}
