/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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

import com.github.mvysny.kaributesting.v10._expectOne

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.select
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.testing.expectInformationNotification
import org.kopi.galite.tests.examples.*
import org.kopi.galite.visual.ui.vaadin.pivottable.DPivotTable

class DocumentationPivotTableTests : GaliteVUITestBase() {
  val simplePivotTableForm = DocumentationPivotTable()
  val pivotTable = DocumentationPivotTableP()

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    simplePivotTableForm.open()
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
      val value = TestTriggers.select{TestTriggers.id eq 5 }.last()[TestTriggers.INS]

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
      transaction {
        initModules()
        initDocumentationData()
      }
    }
  }
}
