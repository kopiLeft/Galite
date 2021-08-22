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
package org.kopi.galite.tests.ui.vaadin.form

import kotlin.test.assertEquals

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.findBlock
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.FormToTestSaveMultipleBlock
import org.kopi.galite.tests.examples.initData
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.ui.vaadin.form.DGridBlock

import com.github.mvysny.kaributesting.v10.expectRow

class MultipleBlockFormTests : GaliteVUITestBase() {

  val multipleBlockSaveForm = FormToTestSaveMultipleBlock().also { it.model }

  @Before
  fun `login to the App`() {
    login()
  }

  @Test
  fun `test list command`() {
    //TODO
    /*
      check that the list dialog is displayed & that contain a correct data,
      then chose a row and check that first and second block contains data
     */
  }

  @Test
  fun `test changeBlock command`() {
    //TODO
    /*
      click on changeBlock commands and check that list dialog with correct name of blocks is displayed
     */
  }

  @Test
  fun `test showHideFilter command`() {
    //TODO
    /*
      in the second block click on showHideFilter commands
      and check that new row of filter is displayed in the grid
     */
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
      arrayOf("1", "Center 2", "14,Rue Mongi Slim", "example@mail"),
      arrayOf("3", "Center 3", "10,Rue du Lac", "example@mail"),
      arrayOf("4", "Center 4", "10,Rue du Lac", "example@mail")
    )

    data.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }

    assertEquals(1, block.editor.item.record)
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      initData()
      initModules()
    }
  }
}
