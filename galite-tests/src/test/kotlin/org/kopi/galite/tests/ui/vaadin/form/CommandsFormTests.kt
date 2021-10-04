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

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing._clickCell
import org.kopi.galite.testing.confirm
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editText
import org.kopi.galite.testing.expect
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.CommandsForm
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.examples.Type
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.report.VFixnumColumn
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.ui.vaadin.report.DReport
import org.kopi.galite.visual.ui.vaadin.report.DTable
import com.github.mvysny.kaributesting.v10._get

class CommandsFormTests : GaliteVUITestBase() {

  val form = CommandsForm().also { it.model }

  @Before
  fun `login to the App`() {
    org.kopi.galite.tests.examples.initData()

    login()

    // Open the form
    form.open()
  }

  @Test
  fun `test list command`() {
    //TODO
    /*
      check that the list dialog is displayed & that contain a correct data,
      then chose a row and check that first field in form contain data
     */
  }

  /**
   * put a value in the first field then click on resetBlock button,
   * check that a popup is displayed,
   * click on yes and check that the first field is empty
   */
  @Test
  fun `test resetBlock command`() {
    val field = form.block.trainingID.findField()

    form.block.trainingID.edit(10)
    assertEquals("10", field.value)
    form.resetBlock.triggerCommand()
    confirm(true)
    assertEquals("", field.value)
  }


  /**
   * click on serialQuery button,
   * check that the first field contain a value
   */
  @Test
  fun `test serialQuery command`() {
    val field = form.block.trainingID.findField()

    form.serialQuery.triggerCommand()
    assertEquals("1", field.value)
  }

  /**
   * click on report button,
   * check that a report contains data is displayed
   * double click on the first row then check that new values are displayed
   */
  @Test
  fun `test report command and report groups`() {
    form.report.triggerCommand()
    val reportTable = _get<DReport>().getTable() as DTable
    val reportColumn = reportTable.model.accessibleColumns.single { it is VFixnumColumn }!!

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("1", "", "", "", ""),
      arrayOf("2", "", "", "", ""),
      arrayOf("3", "", "", "", "")
    ))

    // TEST UNFOLDING

    reportTable._clickCell(1, 0, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("1", "", "", "", ""),
      arrayOf("", "TRAINING 2", reportColumn.format(Decimal("219.6")), "yes", ""),
      arrayOf("", "TRAINING 4", reportColumn.format(Decimal("3129.7")), "yes", ""),
      arrayOf("2", "", "", "", ""),
      arrayOf("3", "", "", "", "")
    ))

    reportTable._clickCell(4, 0, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("1", "", "", "", ""),
      arrayOf("", "TRAINING 2", reportColumn.format(Decimal("219.6")), "yes", ""),
      arrayOf("", "TRAINING 4", reportColumn.format(Decimal("3129.7")), "yes", ""),
      arrayOf("2", "", "", "", ""),
      arrayOf("", "TRAINING 3", reportColumn.format(Decimal("146.9")), "yes", ""),
      arrayOf("3", "", "", "", "")
    ))

    reportTable._clickCell(6, 0, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("1", "", "", "", ""),
      arrayOf("", "TRAINING 2", reportColumn.format(Decimal("219.6")), "yes", ""),
      arrayOf("", "TRAINING 4", reportColumn.format(Decimal("3129.7")), "yes", ""),
      arrayOf("2", "", "", "", ""),
      arrayOf("", "TRAINING 3", reportColumn.format(Decimal("146.9")), "yes", ""),
      arrayOf("3", "", "", "", ""),
      arrayOf("", "TRAINING 1", reportColumn.format(Decimal("1149.24")), "yes", "")
    ))

    // TEST FOLDING

    reportTable._clickCell(6, 0, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("1", "", "", "", ""),
      arrayOf("", "TRAINING 2", reportColumn.format(Decimal("219.6")), "yes", ""),
      arrayOf("", "TRAINING 4", reportColumn.format(Decimal("3129.7")), "yes", ""),
      arrayOf("2", "", "", "", ""),
      arrayOf("", "TRAINING 3", reportColumn.format(Decimal("146.9")), "yes", ""),
      arrayOf("3", "", "", "", ""),
    ))

    reportTable._clickCell(1, 0, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("1", "", "", "", ""),
      arrayOf("2", "", "", "", ""),
      arrayOf("", "TRAINING 3", reportColumn.format(Decimal("146.9")), "yes", ""),
      arrayOf("3", "", "", "", ""),
    ))
  }

  /**
   * click on dynamicReport button,
   * check that a report contains data is displayed
   */
  @Test
  fun `test dynamicReport command`() {
    form.dynamicReport.triggerCommand()
    val reportTable = _get<DReport>().getTable() as DTable
    val reportColumn = reportTable.model.accessibleColumns.single { it is VFixnumColumn }!!

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", "", ""),
      arrayOf("1", "training 1", Type.labelOf(3), reportColumn.format(Decimal("1149.24")), "yes", "informations training 1"),
      arrayOf("2", "training 2", Type.labelOf(1), reportColumn.format(Decimal("219.6")), "yes", "informations training 2"),
      arrayOf("3", "training 3", Type.labelOf(2), reportColumn.format(Decimal("146.9")), "yes", "informations training 3"),
      arrayOf("4", "training 4", Type.labelOf(1), reportColumn.format(Decimal("3129.7")), "yes", "informations training 4")
    ))
  }

  /**
   * fill into form fields and click on saveBlock.
   * assert that new data is saved
   */
  @Test
  fun `test saveBlock command to save new record`() {

  }


  /**
   * load data with serialQuery command,
   * then change second field and click on saveBlock.
   * assert that new data is saved
   */
  @Test
  fun `test saveBlock command to update a record`() {

  }


  /**
   * load data with serialQuery command,
   * then click on deleteBlock.
   * assert that data is deleted.
   */
  @Test
  fun `test deleteBlock command`() {

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
