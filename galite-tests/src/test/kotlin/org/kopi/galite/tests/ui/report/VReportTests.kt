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

package org.kopi.galite.tests.ui.report

import java.util.Locale

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.report.Report
import org.kopi.galite.tests.ui.vaadin.base.UITestBase
import org.kopi.galite.ui.vaadin.report.DReport

class VReportTests: UITestBase() {

  @Test
  fun testSimpleReport() {
    // TODO
  }

  @Before
  fun createRoutes() {
    setupRoutes()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setupVaadin() {
      discoverRooterClass(SimpleReportTest::class.java)
    }
  }
}

@Route("VReport")
class SimpleReportTest : VerticalLayout() {

  init {
    val report = SimpleReport()
    val vreport = DReport(report.model)
    val verticalLayout = VerticalLayout()
    verticalLayout.add(vreport)
    add(verticalLayout)
  }

  /**
   * Simple report with two fields
   */
  class SimpleReport : Report() {
    override val locale = Locale.UK

    override val title = "SimpleReport"

    val field1 = field(StringTestType()) {
      label = "field1"
    }
    val field2 = field(LongTestType()) {
      label = "field2"
    }

    init {
      add {
        this[field1] = "test1"
        this[field2] = 64L
      }
      add {
        this[field1] = "test2"
        this[field2] = 32L
      }
    }
  }

  /**
   * String type Domain
   */
  class StringTestType : CodeDomain<String>() {
    init {
      "cde1" keyOf "test1"
    }
  }

  /**
   * Long type Domain
   */
  class LongTestType : CodeDomain<Long>() {
    init {
      "cde1" keyOf 1
    }
  }
}
