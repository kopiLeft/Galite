/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.tests.ui.vaadin.pivottable

import kotlin.test.assertEquals

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import com.github.mvysny.kaributesting.v10._expectOne

import org.jetbrains.exposed.sql.selectAll

import org.kopi.galite.testing.expectInformationNotification
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.*
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.ui.vaadin.pivottable.DPivotTable

class DocumentationPivotTableTests : GaliteVUITestBase() {
  lateinit var simplePivotTableForm: DocumentationPivotTable
  lateinit var pivotTable: DocumentationPivotTableP

  @Before
  fun `login to the App`() {
    login()

    // Initialize form and pivot table
    simplePivotTableForm = DocumentationPivotTable()
    pivotTable = DocumentationPivotTableP()

    // Open the form
    simplePivotTableForm.open()
  }

  @After
  fun `close pool connection`() {
    ApplicationContext.getDBConnection()?.poolConnection?.close()
  }

  @Test
  fun `test simple pivot table`() {
    // Trigger the pivot table command
    simplePivotTableForm.pivotTable.triggerCommand()
    // Check that the pivot table is displayed
    _expectOne<DPivotTable>()
  }

  @Test
  fun `test pivot table command`() {
    // Trigger the graph command
    simplePivotTableForm.pivotTable.triggerCommand()

    //click on chart command to see information notification
    pivotTable.helpForm.triggerCommand()

    expectInformationNotification("Pivot table command")
  }

  @Test
  fun `test INIT trigger`() {
    // Trigger the pivot table command
    simplePivotTableForm.pivotTable.triggerCommand()

    // check that INIT trigger insert value in tha database
    transaction {
      val value = TestTriggers.selectAll().where{TestTriggers.id eq 5 }.last()[TestTriggers.INS]

      assertEquals("INIT Trigger", value)
    }
  }

  /**
   * Checks that export pivot table as CSV working correctly.
   */
  @Test
  @Ignore
  fun exportFile_CSV() {
    TODO()
  }

  /**
   * Checks that export pivot table as PDF working correctly.
   */
  @Test
  @Ignore
  fun exportFile_PDF() {
    TODO()
  }

  /**
   * Checks that export pivot table as PNG working correctly.
   */
  @Test
  @Ignore
  fun exportFile_PNG() {
    TODO()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      org.jetbrains.exposed.sql.transactions.transaction(connection.dbConnection) {
        initModules()
        initDocumentationData()
      }
    }
  }
}
