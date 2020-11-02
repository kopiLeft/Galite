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

package org.kopi.galite.tests.report

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.junit.Before
import org.junit.BeforeClass
import org.kopi.galite.report.MReport
import org.kopi.galite.report.VStringColumn
import org.kopi.galite.tests.ui.base.UITestBase
import org.kopi.galite.tests.ui.report.DTable
import org.kopi.galite.ui.report.VTable
import org.springframework.boot.SpringApplication


class DTableTests : UITestBase() {


  @Before
  fun createRoutes() {
    setupRoutes()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setupVaadin() {
      discoverRooterClass(SimpleTableTest::class.java)
    }
  }

  @Route("table")
  class SimpleTableTest() : VerticalLayout() {
    val cTable = createTable()

    val table = DTable(cTable)

    init {
      add(table)
    }

    fun createTable(): VTable {
      val report = MReport()
      report.columns = arrayOf(VStringColumn("firstName", 1, 2, 3, null, 100, 100, null),
              VStringColumn("lastName", 1, 2, 3, null, 100, 100, null))
      report.addLine(arrayOf("sarra", "nsir"))
      report.addLine(arrayOf("safia", "nsir"))
      return VTable(report)
    }
  }

}
