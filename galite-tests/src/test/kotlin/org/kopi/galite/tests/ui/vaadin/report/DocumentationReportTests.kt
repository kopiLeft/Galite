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
package org.kopi.galite.tests.ui.vaadin.report

import kotlin.test.assertEquals

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.expectRow

import org.jetbrains.exposed.sql.selectAll

import org.kopi.galite.testing._clickCell
import org.kopi.galite.testing.expect
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.testing.waitAndRunUIQueue
import org.kopi.galite.tests.examples.DocumentationReport
import org.kopi.galite.tests.examples.DocumentationReportTriggers
import org.kopi.galite.tests.examples.DocumentationReportTriggersR
import org.kopi.galite.tests.examples.TestTriggers
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.examples.initReportDocumentationData
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.ui.vaadin.report.DReport
import org.kopi.galite.visual.ui.vaadin.report.DTable

class DocumentationReportTests : GaliteVUITestBase() {
  val simpleReportForm = DocumentationReport()
  val triggersReportForm = DocumentationReportTriggers()
  val triggersReport = DocumentationReportTriggersR()

  @Before
  fun `login to the App`() {
    login()

    // init report data
    transaction {
      initReportDocumentationData()
    }
  }

  @After
  fun `close pool connection`() {
    ApplicationContext.getDBConnection()?.poolConnection?.close()
  }

  @Test
  fun `test simple report`() {
    // Open the forms
    simpleReportForm.open()
    // Trigger the report command
    simpleReportForm.report.triggerCommand()

    // Check that the report is displayed
    _expectOne<DReport>()
  }

  @Test
  fun `test format upper`() {
    // Open the forms
    simpleReportForm.open()
    // Trigger the report command
    simpleReportForm.report.triggerCommand()

    val reportTable = _get<DReport>().getTable() as DTable

    // assert that first column is convert to upper case
    reportTable.expectRow(1, "AHMED", "", "", "", "")
    reportTable.expectRow(2, "SALAH", "", "", "", "")
  }

  @Test
  fun `test format lower`() {
    // Open the forms
    simpleReportForm.open()
    // Trigger the report command
    simpleReportForm.report.triggerCommand()

    val reportTable = _get<DReport>().getTable() as DTable

    reportTable._clickCell(1, 0, 2)
    // assert that second column is convert to upper lower
    reportTable.expectRow(2, "", "cherif", "", "", "")
    reportTable.expectRow(3, "", "malouli", "", "", "")
  }

  @Test
  fun `test report group`() {
    // Open the forms
    simpleReportForm.open()
    // Trigger the report command
    simpleReportForm.report.triggerCommand()

    val reportTable = _get<DReport>().getTable() as DTable

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("AHMED", "", "", "", ""),
      arrayOf("SALAH", "", "", "", "")
    ))

    reportTable._clickCell(2, 0, 2)
    reportTable._clickCell(1, 0, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("AHMED", "", "", "", ""),
      arrayOf("", "cherif", "", "", ""),
      arrayOf("", "malouli", "", "", ""),
      arrayOf("SALAH", "", "", "", ""),
      arrayOf("", "mouelhi", "", "", "")
    ))

    reportTable._clickCell(5, 1, 2)
    reportTable._clickCell(3, 1, 2)
    reportTable._clickCell(2, 1, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("AHMED", "", "", "", ""),
      arrayOf("", "cherif", "", "", ""),
      arrayOf("", "", "Cherif", "", ""),
      arrayOf("", "malouli", "", "", ""),
      arrayOf("", "", "Malouli", "", ""),
      arrayOf("SALAH", "", "", "", ""),
      arrayOf("", "mouelhi", "", "", ""),
      arrayOf("", "", "MOUELHI", "", "")
    ))

    reportTable._clickCell(8, 2, 2)
    reportTable._clickCell(5, 2, 2)
    reportTable._clickCell(3, 2, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("AHMED", "", "", "", ""),
      arrayOf("", "cherif", "", "", ""),
      arrayOf("", "", "Cherif", "", ""),
      arrayOf("", "", "", "30", ""),
      arrayOf("", "malouli", "", "", ""),
      arrayOf("", "", "Malouli", "", ""),
      arrayOf("", "", "", "40", ""),
      arrayOf("SALAH", "", "", "", ""),
      arrayOf("", "mouelhi", "", "", ""),
      arrayOf("", "", "MOUELHI", "", ""),
      arrayOf("", "", "", "30", "")
    ))

    reportTable._clickCell(2, 0, 2)
    reportTable._clickCell(3, 0, 2)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("AHMED", "", "", "", ""),
      arrayOf("SALAH", "", "", "", "")
    ))
  }

  @Test
  fun `test report changing separator position`() {
    // Open the forms
    simpleReportForm.open()
    // Trigger the report command
    simpleReportForm.report.triggerCommand()

    val reportTable = _get<DReport>().getTable() as DTable

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("AHMED", "", "", "", ""),
      arrayOf("SALAH", "", "", "", "")
    ))

    val columns = reportTable.columns
    val list = mutableListOf(*columns.toTypedArray())

    list.removeLast()
    reportTable.setColumnOrder(columns.last(), *(list).toTypedArray())

    waitAndRunUIQueue(10)

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", ""),
      arrayOf("", "AHMED", "cherif", "Cherif", "30"),
      arrayOf("", "AHMED", "malouli", "Malouli", "40"),
      arrayOf("", "SALAH", "mouelhi", "MOUELHI", "30")
    ))
  }

  @Test
  fun `test filed triggers`() {
    // Open the forms
    triggersReportForm.open()
    // Trigger the report command
    triggersReportForm.report.triggerCommand()

    val reportTable = _get<DReport>().getTable() as DTable

    // assert that the result of (sum int, avg int, sum decimal, avg decimal) triggers is true
    reportTable.expect(arrayOf(
      arrayOf("", "", "30", "60", "61,00", "30,50", "", ""),
      arrayOf("Ahmed", "Mouelhi", "40", "20", "40,50", "20,50", "Sunday", ""),
      arrayOf("Sami", "Malouli", "20", "40", "20,50", "40,50", "Monday", "")
    ))
  }

  @Test
  fun `test PREREPORT trigger`() {
    // Open the forms
    triggersReportForm.open()
    // Trigger the report command
    triggersReportForm.report.triggerCommand()

    // check that PREREPORT form trigger insert value in tha database
    transaction {
      val value = TestTriggers.selectAll().last()[TestTriggers.INS]

      assertEquals("PREREPORT Trigger", value)
    }
  }

  @Test
  fun `test POSTREPORT trigger`() {
    // Open the forms
    triggersReportForm.open()
    // Trigger the report command
    triggersReportForm.report.triggerCommand()

    //click on quit command to quit report and call POSTREPORT trigger
    triggersReport.quit.triggerCommand()

    // check that POSTREPORT form trigger insert value in tha database
    transaction {
      val value = TestTriggers.selectAll().last()[TestTriggers.INS]

      assertEquals("POSTREPORT Trigger", value)
    }
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      org.jetbrains.exposed.sql.transactions.transaction(connection.dbConnection) {
        initModules()
      }
    }
  }
}
