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
package org.kopi.galite.tests.ui.vaadin.chart

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.tests.examples.TestTriggers
import org.kopi.galite.visual.ui.vaadin.visual.DActor
import org.jetbrains.exposed.sql.select
import org.kopi.galite.testing.expectInformationNotification
import org.kopi.galite.tests.examples.DocumentationChart
import org.kopi.galite.tests.examples.initDocumentationData
import org.kopi.galite.visual.ui.vaadin.chart.DAbstractChartType

import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._find

class DocumentationChartTests : GaliteVUITestBase() {
  val simpleChart = DocumentationChart().also { it.model }

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    simpleChart.open()
  }

  @Test
  fun `test simple chart`() {
    // Trigger the graph command
    simpleChart.graph.triggerCommand()
    // Check that the report is displayed
    _expectOne<DAbstractChartType>()
  }

  @Test
  fun `test chat command`() {
    // Trigger the graph command
    simpleChart.graph.triggerCommand()
    //click on chart command to see information notification
    val actors = _find<DActor>()

    // chart command
    actors.single { it.getModel().actorIdent == "chart" }._clickAndWait()

    expectInformationNotification("chart command")
  }

  @Test
  fun `test INIT trigger`() {
    // Trigger the graph command
    simpleChart.graph.triggerCommand()

    // check that INIT trigger insert value in tha database
    transaction {
      val value = TestTriggers.select{TestTriggers.id eq 5 }.last()[TestTriggers.INS]

      assertEquals("INIT Trigger", value)
    }
  }

  @Test
  fun `test PRECHART trigger`() {
    // Trigger the graph command
    simpleChart.graph.triggerCommand()

    // check that PRECHART trigger insert value in tha database
    transaction {
      val value = TestTriggers.select{TestTriggers.id eq 6 }.last()[TestTriggers.INS]

      assertEquals("PRECHART Trigger", value)
    }
  }

  @Test
  fun `test POSTCHART trigger`() {
    // Trigger the graph command
    simpleChart.graph.triggerCommand()

    //click on quit command to quit report and call POSTCHART trigger
    val actors = _find<DActor>()

    // quit command
    actors.single { it.getModel().actorIdent == "Quit" }._clickAndWait()

    // check that POSTCHART trigger insert value in tha database
    transaction {
      val value = TestTriggers.select{TestTriggers.id eq 7 }.last()[TestTriggers.INS]

      assertEquals("POSTCHART Trigger", value)
    }
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
