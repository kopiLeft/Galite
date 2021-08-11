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

import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.tests.examples.initData
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase

class MultipleBlockFormTests : GaliteVUITestBase() {

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

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      initData()
      initModules()
    }
  }
}
