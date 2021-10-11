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
package org.kopi.galite.tests.ui.vaadin.list

import kotlin.test.assertEquals
import kotlin.test.assertFalse

import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.testing.waitAndRunUIQueue
import org.kopi.galite.tests.examples.CommandsForm
import org.kopi.galite.tests.examples.initDatabase
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.form.DListDialog
import org.kopi.galite.visual.ui.vaadin.list.ListTable

import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.expectRow
import com.github.mvysny.kaributesting.v10.expectRows
import com.vaadin.flow.component.grid.Grid

class ListTests: GaliteVUITestBase() {

  private val formWithList = CommandsForm().also { it.model } // initialize the model

  /**
   * Checks that the list dialog is displayed and contains a correct data,
   * then select a row and check that form fields contain data
   */
  @Test
  fun `test list command`() {
    // Login
    login()

    // Opens a form that contain a list command
    formWithList.open(700)

    // Trigger the report command
    formWithList.list.triggerCommand()

    // Check that the list dialog is displayed
    _expectOne<DListDialog>()

    // Check that the list dialog contains a grid
    val listDialog = _get<DListDialog>()
    listDialog._expectOne<Grid<*>>()

    // Check that the grid data is correct
    val grid = _get<DListDialog>()._get<ListTable>()
    val data = arrayOf(
      arrayOf("1", "training 1", "Java", "1.149,240", "yes", "informations training 1"),
      arrayOf("2", "training 2", "Galite", "219,600", "yes", "informations training 2"),
      arrayOf("3", "training 3", "Kotlin", "146,900", "yes", "informations training 3"),
      arrayOf("4", "training 4", "Galite", "3.129,700", "yes", "informations training 4")
    )

    grid.expectRows(data.size)

    data.forEachIndexed { index, it ->
      grid.expectRow(index, *it)
    }

    // Choose second row
    grid.selectionModel.selectFromClient(grid.dataCommunicator.getItem(1))

    waitAndRunUIQueue(100)

    // Dialog is closed and row data are filled into the form
    assertFalse(listDialog.isOpened)
    assertEquals("2", formWithList.block.trainingID.findField().value)
    assertEquals("training 2", formWithList.block.trainingName.findField().value)
    assertEquals(true, formWithList.block.active.findField().value)
    assertEquals("informations training 2", formWithList.block.informations.findField().value)
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      initDatabase()
    }
  }
}
