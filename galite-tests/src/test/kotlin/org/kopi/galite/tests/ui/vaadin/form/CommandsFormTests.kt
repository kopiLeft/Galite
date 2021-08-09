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

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase

class CommandsFormTests : GaliteVUITestBase() {

  @Test
  fun `test list command`() {
    //TODO
    /*
      check that the list dialog is displayed & that contain a correct data,
      then chose a row and check that first field in form contain data
     */
  }

  @Test
  fun `test resetBlock command`(){
    //TODO
    /*
      put a value in the first field then click on resetBlock button,
      check that a popup is displayed,
      click on yes and check that the first field is empty
     */
  }

  @Test
  fun `test serialQuery command`() {
    //TODO
    /*
      click on serialQuery button,
      check that the first field contain a value
     */
  }

  @Test
  fun `test report command`() {
    //TODO
    /*
      click on report button,
      check that a report contains data is displayed
     */
  }

  fun `test report group`() {
    //TODO
    /*
      open the report,
      double click on the first row then check that new values are displayed
     */
  }

  fun `test dynamicReport command`() {
    //TODO
    /*
       click on dynamicReport button,
      check that a report contains data is displayed
     */
  }

  fun `test saveBlock command`() {
    //TODO
    /*
       load data with list button,
       then change second field and click on saveBlock.
       assert that new data is saved
     */
  }

  fun `test deleteBlock command`() {
    //TODO
    /*
       load data with list button,
       then click on deleteBlock.
       assert that data is deleted
     */
  }

  fun `test Operator command`() {
    //TODO
    /*
       click on Operator button.
       assert that list contain data is displayed
     */
  }
  fun `test quit command`() {
    //TODO
    /*
       click on Operator button.
       assert that list popup is displayed
     */
  }

  fun `test helpForm command`() {
    //TODO
    /*
       click on Operator button.
       assert that help is displayed
     */
  }

  fun `test shortcut`() {
    //TODO
    /*
       try to show help with F1 shortcut
     */
  }
}
