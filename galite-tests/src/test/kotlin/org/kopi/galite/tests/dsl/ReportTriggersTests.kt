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
package org.kopi.galite.tests.dsl

import java.math.BigDecimal
import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.report.triggers.avgDecimal
import org.kopi.galite.visual.report.triggers.avgInteger
import org.kopi.galite.visual.report.triggers.countInteger
import org.kopi.galite.visual.report.triggers.reportIdenticalValue
import org.kopi.galite.visual.report.triggers.serialInteger
import org.kopi.galite.visual.report.triggers.sumDecimal
import org.kopi.galite.visual.report.triggers.sumInteger
import org.kopi.galite.visual.report.triggers.sumNullDecimal
import org.kopi.galite.visual.report.triggers.sumNullInteger

class ReportTriggersTests: VApplicationTestBase() {
  fun init() : VReport {
    val report = ReportWithTriggers()
    val reportModel = report.model

    reportModel.initReport()
    return reportModel
  }
  @Test
  fun `test avgInteger trigger`() {
    val reportModel = init()

    assertEquals(23, reportModel.model.getRow(0)!!.data[0])
  }

  @Test
  fun `test sumInteger trigger`() {
    val reportModel = init()

    assertEquals(100, reportModel.model.getRow(0)!!.data[1])
  }

  @Test
  fun `test countInteger trigger`() {
    val reportModel = init()

    assertEquals(3, reportModel.model.getRow(0)!!.data[2])
  }

  @Test
  fun `test sumDecimal trigger`() {
    val reportModel = init()

    assertEquals(BigDecimal("298.70"), reportModel.model.getRow(0)!!.data[3])
  }

  @Test
  fun `test sumNullInteger trigger`() {
    val reportModel = init()

    assertEquals(null, reportModel.model.getRow(0)!!.data[4])
  }

  @Test
  fun `test sumNullDecimal trigger`() {
    val reportModel = init()

    assertEquals(null, reportModel.model.getRow(0)!!.data[5])
  }

  @Test
  fun `test reportIdenticalValue trigger`() {
    val reportModel = init()

    assertEquals(70, reportModel.model.getRow(0)!!.data[6])
  }

  @Test
  fun `test avgDecimal trigger`() {
    val reportModel = init()

    assertEquals(BigDecimal("99.57"), reportModel.model.getRow(0)!!.data[7])
  }

  @Test
  fun `test serialInteger trigger`() {
    val reportModel = init()

    assertEquals(3, reportModel.model.getRow(0)!!.data[8])
  }
}

class ReportWithTriggers : Report(title = "Report", locale = Locale.UK) {

  val age = field(INT(3)) {
    label = "Age"
    compute {
      avgInteger()
    }
  }

  val intSum = field(INT(3)) {
    label = "int Sum"
    compute {
      sumInteger()
    }
  }

  val count = field(INT(3)) {
    label = "count"
    compute {
      countInteger()
    }
  }

  val decimalSum = field(DECIMAL(20, 10)) {
    label = "decimal Sum"
    compute {
      sumDecimal()
    }
  }

  val intSumNull = field(INT(10)) {
    label = "int Sum Null"
    compute {
      sumNullInteger()
    }
  }

  val decimalSumNull = field(DECIMAL(20, 10)) {
    label = "decimal Sum Null"
    compute {
      sumNullDecimal()
    }
  }

  val identicalValue = field(INT(10)) {
    label = "identical Value"
    compute {
      reportIdenticalValue()
    }
  }

  val decimalAvg = field(DECIMAL(20, 10)) {
    label = "decimal avg"
    compute {
      avgDecimal()
    }
  }

  val serialInteger = field(INT(10)) {
    label = "serial Integer"
    compute {
      serialInteger()
    }
  }

  init {
    add {
      this[age] = 20
      this[intSum] = 20
      this[count] = 20
      this[decimalSum] = BigDecimal("129.7")
      this[identicalValue] = 70
      this[decimalAvg] = BigDecimal("129.7")
      this[serialInteger] = 70
    }
    add {
      this[age] = 20
      this[intSum] = 30
      this[count] = 30
      this[decimalSum] = BigDecimal("149.1")
      this[identicalValue] = 70
      this[decimalAvg] = BigDecimal("149.1")
      this[serialInteger] = 10
    }
    add {
      this[age] = 30
      this[intSum] = 50
      this[count] = 50
      this[decimalSum] = BigDecimal("19.9")
      this[identicalValue] = 70
      this[decimalAvg] = BigDecimal("19.9")
      this[serialInteger] = 90
    }
  }
}
