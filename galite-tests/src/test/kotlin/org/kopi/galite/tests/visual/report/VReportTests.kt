package org.kopi.galite.tests.visual.report

import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.Query
import junit.framework.Assert
import org.junit.Test
import org.kopi.galite.domain.Domain
import org.kopi.galite.visual.field.Field
import org.kopi.galite.visual.report.Report
import org.kopi.galite.visual.report.VReport
import kotlin.test.assertEquals

class VReportTests {

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
  fun vreportDataTest(){
    var report = Report()
    var vreport = VReport(report)
    var reportGrid = vreport.reportGrid
    var dataProvider = reportGrid.dataProvider as ListDataProvider

    assertEquals(0, dataProvider.size(Query()) )

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
    assertEquals(2, dataProvider.size(Query()) )
  }

  class StringTestType : Domain<String>(5) {
    override val values = code {
      this["cde1"] = "test1"
    }
  }
}