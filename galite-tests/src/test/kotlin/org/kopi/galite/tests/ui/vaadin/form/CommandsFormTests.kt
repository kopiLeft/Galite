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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing._clickCell
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.editText
import org.kopi.galite.testing.expect
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.findForm
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.testing.waitAndRunUIQueue
import org.kopi.galite.tests.examples.Center
import org.kopi.galite.tests.examples.CommandsForm
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.examples.Type
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.report.VFixnumColumn
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.ui.vaadin.form.DListDialog
import org.kopi.galite.visual.ui.vaadin.list.ListTable
import org.kopi.galite.visual.ui.vaadin.report.DReport
import org.kopi.galite.visual.ui.vaadin.report.DTable
import org.kopi.galite.visual.ui.vaadin.visual.DHelpViewer
import org.kopi.galite.visual.visual.VlibProperties
import org.kopi.galite.tests.examples.MultipleBlockForm
import org.kopi.galite.tests.examples.initData
import org.kopi.galite.tests.examples.initDatabase
import org.kopi.galite.testing.expectConfirmNotification

import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get

class CommandsFormTests : GaliteVUITestBase() {

  val form = CommandsForm().also { it.model }
  val multipleForm = MultipleBlockForm().also { it.model }

  @Before
  fun `login to the App`() {
    transaction {
      initData()
    }

    login()

    // Open the form
    form.open(500)
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
    transaction {
      // Check initial data
      val initialData = Training.selectAll().map {
        arrayOf(it[Training.id],
                it[Training.trainingName],
                it[Training.type],
                it[Training.price],
                it[Training.active]
        )
      }

      assertArraysEquals(arrayOf(1, "training 1", 3, Decimal("1149.240").value, true), initialData[0])
      assertArraysEquals(arrayOf(2, "training 2", 1, Decimal("219.600").value, true), initialData[1])
      assertArraysEquals(arrayOf(3, "training 3", 2, Decimal("146.900").value, true), initialData[2])
      assertArraysEquals(arrayOf(4, "training 4", 1, Decimal("3129.700").value, true), initialData[3])
    }

    form.InsertMode.triggerCommand()

    form.block.trainingName.edit("training test")
    form.block.trainingType.editText("Galite")
    form.block.trainingPrice.edit(Decimal("1000"))
    form.block.active.edit(true)

    form.saveBlock.triggerCommand()

    transaction {
      val data = Training.selectAll().map {
        arrayOf(it[Training.id],
                it[Training.trainingName],
                it[Training.type],
                it[Training.price],
                it[Training.active]
        )
      }

      assertArraysEquals(arrayOf(1, "training 1", 3, Decimal("1149.240").value, true), data[0])
      assertArraysEquals(arrayOf(2, "training 2", 1, Decimal("219.600").value, true), data[1])
      assertArraysEquals(arrayOf(3, "training 3", 2, Decimal("146.900").value, true), data[2])
      assertArraysEquals(arrayOf(4, "training 4", 1, Decimal("3129.700").value, true), data[3])
      assertArraysEquals(arrayOf(5, "training test", 1, Decimal("1000.000").value, true), data[4])
    }
  }


  /**
   * load data with serialQuery command,
   * then change second field and click on saveBlock.
   * assert that new data is saved
   */
  @Test
  fun `test saveBlock command to update a record`() {
    transaction {
      // Check initial data
      val initialData = Training.selectAll().map {
        arrayOf(it[Training.id],
                it[Training.trainingName],
                it[Training.type],
                it[Training.price],
                it[Training.active]
        )
      }

      assertArraysEquals(arrayOf(1, "training 1", 3, Decimal("1149.240").value, true), initialData[0])
      assertArraysEquals(arrayOf(2, "training 2", 1, Decimal("219.600").value, true), initialData[1])
      assertArraysEquals(arrayOf(3, "training 3", 2, Decimal("146.900").value, true), initialData[2])
      assertArraysEquals(arrayOf(4, "training 4", 1, Decimal("3129.700").value, true), initialData[3])
    }

    form.serialQuery.triggerCommand()

    form.block.trainingName.edit("training test")

    form.saveBlock.triggerCommand()

    transaction {
      val data = Training.selectAll().map {
        arrayOf(it[Training.id],
                it[Training.trainingName],
                it[Training.type],
                it[Training.price],
                it[Training.active]
        )
      }

      assertArraysEquals(arrayOf(1, "training test", 3, Decimal("1149.240").value, true), data[0])
      assertArraysEquals(arrayOf(2, "training 2", 1, Decimal("219.600").value, true), data[1])
      assertArraysEquals(arrayOf(3, "training 3", 2, Decimal("146.900").value, true), data[2])
      assertArraysEquals(arrayOf(4, "training 4", 1, Decimal("3129.700").value, true), data[3])
    }
  }


  /**
   * load data with serialQuery command,
   * then click on deleteBlock.
   * assert that data is deleted.
   */
  @Test
  fun `test deleteBlock command`() {
    // Check initial data
    val initialData = transaction {
      Training.selectAll().map {
        arrayOf(it[Training.id],
                it[Training.trainingName],
                it[Training.type],
                it[Training.price],
                it[Training.active]
        )
      }
    }
    assertEquals(4, initialData.size)
    assertArraysEquals(arrayOf(1, "training 1", 3, Decimal("1149.240").value, true), initialData[0])
    assertArraysEquals(arrayOf(2, "training 2", 1, Decimal("219.600").value, true), initialData[1])
    assertArraysEquals(arrayOf(3, "training 3", 2, Decimal("146.900").value, true), initialData[2])
    assertArraysEquals(arrayOf(4, "training 4", 1, Decimal("3129.700").value, true), initialData[3])


    // Delete the foreign key references first.
    transaction {
      Center.deleteWhere {
        Center.refTraining eq 1
      }
    }

    form.block.trainingID.edit(1)
    form.serialQuery.triggerCommand()
    form.deleteBlock.triggerCommand()
    expectConfirmNotification(true)

    val data = transaction {
      Training.selectAll().map {
        arrayOf(it[Training.id],
                it[Training.trainingName],
                it[Training.type],
                it[Training.price],
                it[Training.active]
        )
      }
    }
    assertEquals(3, data.size)
    assertArraysEquals(arrayOf(2, "training 2", 1, Decimal("219.600").value, true), data[0])
    assertArraysEquals(arrayOf(3, "training 3", 2, Decimal("146.900").value, true), data[1])
    assertArraysEquals(arrayOf(4, "training 4", 1, Decimal("3129.700").value, true), data[2])
  }

  /**
   * click on the search operator command.
   * assert that the list of operators is displayed.
   * Then choose operator (less than) and fill into id field with "3",
   * click on the list command and check that list contains only records
   * with id < 3.
   */
  @Test
  fun `test search operator command`() {
    form.Operator.triggerCommand()

    // Check that the grid data is correct
    val grid = _get<DListDialog>()._get<ListTable>()

    grid.expect(arrayOf(
      arrayOf(VlibProperties.getString("operator_eq")),
      arrayOf(VlibProperties.getString("operator_lt")),
      arrayOf(VlibProperties.getString("operator_gt")),
      arrayOf(VlibProperties.getString("operator_le")),
      arrayOf(VlibProperties.getString("operator_ge")),
      arrayOf(VlibProperties.getString("operator_ne"))
    ))

    // Choose smaller operator
    grid.selectionModel.selectFromClient(grid.dataCommunicator.getItem(1))

    waitAndRunUIQueue(100)

    form.block.trainingID.edit(3)

    form.list.triggerCommand()

    val list = _get<DListDialog>()._get<ListTable>()

    list.expect(arrayOf(
      arrayOf("1", "training 1", "Java", "1.149,240", "yes", "informations training 1"),
      arrayOf("2", "training 2", "Galite", "219,600", "yes", "informations training 2")
    ))
  }

  /**
   * Check that the form is initially rendered.
   * Click on quit command and check that the form is not visible anymore.
   */
  @Test
  fun `test quit command`() {
    assertNotNull(form.findForm())

    form.quit.triggerCommand()

    assertNull(form.findForm())
  }

  /**
   * Click on help command.
   * Check that the help window is displayed
   */
  @Test
  fun `test helpForm command`() {
    form.helpForm.triggerCommand()

    _expectOne<DHelpViewer>()
  }

  fun `test shortcut`() {
    //TODO
    /*
       try to show help with F1 shortcut
     */
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
      transaction {
        initDatabase()
      }
    }
  }
}
