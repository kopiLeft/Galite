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
package org.kopi.galite.tests.dsl

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.form.FormWithAlignedBlock
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.form.FieldAlignment
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.insertBlock
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.Triggers
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.type.Decimal

class ReportTriggersTests: VApplicationTestBase() {
  fun init() : VReport {
    val report = ReportWithTriggers()
    val reportModel = report.model

    reportModel.initReport()
    return reportModel
  }
  @Test
  fun `test avgInteger triggers`() {
    val reportModel = init()

    assertEquals(23, reportModel.model.getRow(0)!!.data[0])
  }

  @Test
  fun `test sumInteger triggers`() {
    val reportModel = init()

    assertEquals(100, reportModel.model.getRow(0)!!.data[1])
  }

  @Test
  fun `test countInteger triggers`() {
    val reportModel = init()

    assertEquals(3, reportModel.model.getRow(0)!!.data[2])
  }

  @Test
  fun `test sumDecimal triggers`() {
    val reportModel = init()

    assertEquals(Decimal("298.7"), reportModel.model.getRow(0)!!.data[3])
  }

  @Test
  fun `test sumNullInteger triggers`() {
    val reportModel = init()

    assertEquals(null, reportModel.model.getRow(0)!!.data[4])
  }

  @Test
  fun `test sumNullDecimal triggers`() {
    val reportModel = init()

    assertEquals(null, reportModel.model.getRow(0)!!.data[5])
  }

  @Test
  fun `test reportIdenticalValue triggers`() {
    val reportModel = init()

    assertEquals(70, reportModel.model.getRow(0)!!.data[6])
  }

  @Test
  fun `test avgDecimal triggers`() {
    val reportModel = init()

    assertEquals(Decimal("99.57"), reportModel.model.getRow(0)!!.data[7])
  }

  @Test
  fun `test serialInteger triggers`() {
    val reportModel = init()

    assertEquals(3, reportModel.model.getRow(0)!!.data[8])
  }
}

class ReportWithTriggers : Report() {
  override val locale = Locale.UK
  override val title = "Report"

  val age = field(INT(3)) {
    label = "Age"
    compute {
      Triggers.avgInteger(this)
    }
  }

  val intSum = field(INT(3)) {
    label = "int Sum"
    compute {
      Triggers.sumInteger(this)
    }
  }

  val count = field(INT(3)) {
    label = "count"
    compute {
      Triggers.countInteger(this)
    }
  }

  val decimalSum = field(DECIMAL(20, 10)) {
    label = "decimal Sum"
    compute {
      Triggers.sumDecimal(this)
    }
  }

  val intSumNull = field(INT(10)) {
    label = "int Sum Null"
    compute {
      Triggers.sumNullInteger(this)
    }
  }

  val decimalSumNull = field(DECIMAL(20, 10)) {
    label = "decimal Sum Null"
    compute {
      Triggers.sumNullDecimal(this)
    }
  }

  val identicalValue = field(INT(10)) {
    label = "identical Value"
    compute {
      Triggers.reportIdenticalValue(this)
    }
  }

  val decimalAvg = field(DECIMAL(20, 10)) {
    label = "decimal avg"
    compute {
      Triggers.avgDecimal(this)
    }
  }

  val serialInteger = field(INT(10)) {
    label = "serial Integer"
    compute {
      Triggers.serialInteger(this)
    }
  }

  init {
    add {
      this[age] = 20
      this[intSum] = 20
      this[count] = 20
      this[decimalSum] = Decimal("129.7")
      this[identicalValue] = 70
      this[decimalAvg] = Decimal("129.7")
      this[serialInteger] = 70
    }
    add {
      this[age] = 20
      this[intSum] = 30
      this[count] = 30
      this[decimalSum] = Decimal("149.1")
      this[identicalValue] = 70
      this[decimalAvg] = Decimal("149.1")
      this[serialInteger] = 10
    }
    add {
      this[age] = 30
      this[intSum] = 50
      this[count] = 50
      this[decimalSum] = Decimal("19.9")
      this[identicalValue] = 70
      this[decimalAvg] = Decimal("19.9")
      this[serialInteger] = 90
    }
  }
}
