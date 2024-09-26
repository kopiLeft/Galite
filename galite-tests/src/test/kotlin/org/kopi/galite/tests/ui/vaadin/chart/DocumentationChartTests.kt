/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.tests.ui.vaadin.chart

import kotlin.test.assertEquals

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.github.mvysny.kaributesting.v10._expectOne

import org.jetbrains.exposed.sql.selectAll

import org.kopi.galite.testing.expectInformationNotification
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.examples.DocumentationChart
import org.kopi.galite.tests.examples.DocumentationChartC
import org.kopi.galite.tests.examples.TestTriggers
import org.kopi.galite.tests.examples.initDocumentationData
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.ui.vaadin.chart.DAbstractChartType

class DocumentationChartTests : GaliteVUITestBase() {
  val simpleChartForm = DocumentationChart()
  val chart = DocumentationChartC()

  @Before
  fun `login to the App`() {
    login()

    transaction {
      initDocumentationData()
    }
    // Open the form
    simpleChartForm.open()
  }

  @After
  fun `close pool connection`() {
    ApplicationContext.getDBConnection()?.poolConnection?.close()
  }

  @Test
  fun `test simple chart`() {
    // Trigger the graph command
    simpleChartForm.graph.triggerCommand()
    // Check that the report is displayed
    _expectOne<DAbstractChartType>()
  }

  @Test
  fun `test chart command`() {
    // Trigger the graph command
    simpleChartForm.graph.triggerCommand()

    //click on chart command to see information notification
    chart.chartActor.triggerCommand()

    expectInformationNotification("chart command")
  }

  @Test
  fun `test INITCHART trigger`() {
    // Trigger the graph command
    simpleChartForm.graph.triggerCommand()

    // check that INIT trigger insert value in tha database
    transaction {
      val value = TestTriggers.selectAll().where{TestTriggers.id eq 5 }.last()[TestTriggers.INS]

      assertEquals("INITCHART Trigger", value)
    }
  }

  @Test
  fun `test PRECHART trigger`() {
    // Trigger the graph command
    simpleChartForm.graph.triggerCommand()

    // check that PRECHART trigger insert value in tha database
    transaction {
      val value = TestTriggers.selectAll().where{TestTriggers.id eq 6 }.last()[TestTriggers.INS]

      assertEquals("PRECHART Trigger", value)
    }
  }

  @Test
  fun `test POSTCHART trigger`() {
    // Trigger the graph command
    simpleChartForm.graph.triggerCommand()

    //click on quit command to quit report and call POSTCHART trigger
    chart.quit.triggerCommand()

    // check that POSTCHART trigger insert value in tha database
    transaction {
      val value = TestTriggers.selectAll().where{TestTriggers.id eq 7 }.last()[TestTriggers.INS]

      assertEquals("POSTCHART Trigger", value)
    }
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
