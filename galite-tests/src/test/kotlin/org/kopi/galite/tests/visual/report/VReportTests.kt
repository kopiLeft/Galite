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

package org.kopi.galite.tests.visual.report

import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.Query
import io.github.sukgu.Shadow
import org.junit.Test
import org.kopi.galite.TestBase
import org.kopi.galite.domain.Domain
import org.kopi.galite.visual.report.Report
import org.kopi.galite.visual.report.VReport
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class VReportTests : TestBase() {

  /**
   * Tests that column has been added in the report.
   */
  @Test
  fun addColumnsTest() {
    var report = Report()
    var vreport = VReport(report)
    var reportGrid = vreport.reportGrid

    assertEquals(0, reportGrid.columns.size)

    report.field(StringTestType()) {
      label = "field1"
    }
    vreport.addColumns(report.fields)
    assertEquals(1, reportGrid.columns.size)

    var testkey = reportGrid.columns.map { column -> column.key }.contains("field1")
    assertEquals(true, testkey)

    testkey = reportGrid.columns.map { column -> column.key }.contains("unrealfield")
    assertEquals(false, testkey)
  }

  /**
   * Tests that data has been added in the report.
   */
  @Test
  fun vreportDataTest() {
    var report = Report()
    var vreport = VReport(report)
    var reportGrid = vreport.reportGrid
    var dataProvider = reportGrid.dataProvider as ListDataProvider

    assertEquals(0, dataProvider.size(Query()))

    val field = report.field(StringTestType()) {
      label = "field1"
    }
    vreport.addColumns(report.fields)

    report.add {
      this[field] = "val1"
    }
    report.add {
      this[field] = "val2"
    }
    assertEquals(2, dataProvider.size(Query()))
  }

  /**
   * Tests that data has been added in visual the report.
   * You should run the application before this test
   */
  @Test
  fun gridShow() {
    val driver = driver!!
    driver.get("http://localhost:8080/ReportDemo")
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
    val shadow = Shadow(driver)

    /* Test the existence of the first column*/
    val header1 = shadow.findElement(buildSelector(tag = "vaadin-grid-cell-content", criteria = *arrayOf("slot" to "vaadin-grid-cell-content-0")))
    assertEquals("field1", header1.text)

    /* Test the existence of the second column*/
    val header2 = shadow.findElement(buildSelector(tag = "vaadin-grid-cell-content", criteria = *arrayOf("slot" to "vaadin-grid-cell-content-5")))
    assertEquals("field2", header2.text)

    /* Test the existence of the first value*/
    val cell0 = shadow.findElement(buildSelector(tag = "vaadin-grid-cell-content", criteria = *arrayOf("slot" to "vaadin-grid-cell-content-1")))
    assertEquals("test1", cell0.text)

    /* Test the existence of the fourth value*/
    val cell4 = shadow.findElement(buildSelector(tag = "vaadin-grid-cell-content", criteria = *arrayOf("slot" to "vaadin-grid-cell-content-4")))
    assertEquals("32", cell4.text)
  }

  class StringTestType : Domain<String>(5) {
    override val values = code {
      this["cde1"] = "test1"
    }
  }
}