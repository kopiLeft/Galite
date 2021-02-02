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

package org.kopi.galite.tests

import org.kopi.galite.base.UComponent
import org.kopi.galite.chart.Chart
import org.kopi.galite.chart.UChart
import org.kopi.galite.chart.VChart
import org.kopi.galite.report.MReport
import org.kopi.galite.report.Report
import org.kopi.galite.report.UReport
import org.kopi.galite.report.VReport
import kotlin.test.assertEquals

/**
 * TestBase class for all tests.
 */
open class TestBase {

  /**
   * Tests operation on a chart.
   */
  fun withChart(chartInit: Chart.() -> Unit) {
    val chart = object : Chart() {
      override val title: String = "test"
    }
    chart.chartInit()
  }

  open fun getReportDisplay(model: VReport): UComponent? = null

  open fun getChartDisplay(model: VChart): UComponent? = null

  /**
   * Tests operation on a report.
   *
   * @param report The report to test
   * @param operations operations to apply on the report
   */
  fun <T: Report> withReport(report: T, operations: (T.(model: VReport) -> Unit)? = null) {
    val display = getReportDisplay(report.model) ?: throw Exception("Display is null")

    report.model.setDisplay(
            (display as UReport)
    )

    if (operations != null) {
      report.operations(report.model)
    }
    report.model.model = MReport()
  }

  /**
   * Tests operation on a chart.
   *
   * @param chart The chart to test
   * @param operations operations to apply on the chart
   */
  fun <T: Chart> withChart(chart: T, operations: (T.(model: VChart) -> Unit)? = null) {
    val display = getChartDisplay(chart.model) ?: throw Exception("Display is null")

    chart.model.setDisplay(
            (display as UChart)
    )

    if (operations != null) {
      chart.operations(chart.model)
    }
  }

  // ----------------------------------------------------------------------
  // Testing extension methods
  // ----------------------------------------------------------------------

  fun <T> assertCollectionsEquals(expected: Collection<T>, actual: Collection<T>) {
    assertEquals(expected.size, actual.size, "Size mismatch between the collections")

    expected.forEachIndexed { index, expectedElement ->
      assertEquals(expectedElement, actual.elementAt(index))
    }
  }

  fun <T> assertArraysEquals(expected: Array<T>, actual: Array<T>) {
    assertEquals(expected.size, actual.size, "Size mismatch between the arrays")

    expected.forEachIndexed { index, expectedElement ->
      assertEquals(expectedElement, actual.elementAt(index))
    }
  }
}
