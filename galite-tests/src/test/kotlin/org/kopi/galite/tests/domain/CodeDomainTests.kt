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

package org.kopi.galite.tests.domain

import java.util.Locale

import kotlin.test.assertEquals
import kotlin.test.assertIs

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.chart.VIntegerCodeMeasure
import org.kopi.galite.visual.chart.VStringCodeDimension
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.form.VStringCodeField
import org.kopi.galite.visual.report.VStringCodeColumn

/**
 * Contains tests of code-domain creation and manipulation
 */
class CodeDomainTests: VApplicationTestBase() {

  /**
   * Tests the creation of a domain of a type code.
   */
  @Test
  fun domainCodeTest() {
    // Declaration of the domain with codes
    class IntTestType : CodeDomain<Long>() {
      init {
        "cde1" keyOf 1L
        "cde2" keyOf 2L
      }
    }
    // Creating an instance of the domain IntTestType
    val domain = IntTestType()
    // test code values
    val codes = domain.codes

    assertEquals(2, codes.size)
    assertEquals("id$0", codes[0].ident)
    assertEquals("cde1", codes[0].label)
    assertEquals(1L, codes[0].value)
    assertEquals("id$1", codes[1].ident)
    assertEquals("cde2", codes[1].label)
    assertEquals(2L, codes[1].value)
  }

  @Test
  fun buildFormFieldModel() {
    val testForm = TestForm()
    val model = testForm.model
    val block = model.blocks.single()
    val field = block.fields.single()

    assertIs<VStringCodeField>(field)
    assertIs<CodeDomain<String>>(testForm.block.field1.domain)
  }

  @Test
  fun buildReportFieldModel() {
    val testReport = TestReport()
    val model = testReport.model
    val reportColumn = model.getColumn(0)

    assertIs<VStringCodeColumn>(reportColumn)
    assertIs<CodeDomain<String>>(testReport.testField.domain)
  }

  @Test
  fun buildDimensionModel() {
    val testChart = TestChart()
    val model = testChart.model
    val dimension = model.getDimension(0)

    assertIs<VStringCodeDimension>(dimension)
    assertIs<CodeDomain<String>>(testChart.dimensionTest.domain)
  }

  @Test
  fun buildMeasureModel() {
    val testChart = TestChart()
    val model = testChart.model
    val measure = model.getMeasure(0)

    assertIs<VIntegerCodeMeasure>(measure)
    assertIs<IntegerType>(testChart.measureTest.domain)
  }
}

class StringType : CodeDomain<String>() {
  init {
    "OK" keyOf "Yes"
    "Nope" keyOf "No"
  }
}

class IntegerType : CodeDomain<Int>() {
  init {
    "10" keyOf 100
    "20" keyOf 200
    "30" keyOf 300
  }
}

class TestForm : Form() {
  override val locale: Locale = Locale.UK
  override val title: String = "Test Form"
  val block = insertBlock(TestBlock())

  inner class TestBlock : Block(1, 1, "Test Block") {
    val field1 = visit(StringType(), at(1, 1)) {
      label = "Initial field"
    }
  }
}

class TestReport : Report() {
  override val locale = Locale.UK
  override val title = "Test Report"

  val testField = field(StringType()) {
    label = "Test field"
  }
}

class TestChart : Chart() {
  override val locale = Locale.UK
  override val title = "Test Chart"

  val measureTest = measure(IntegerType()) {
    label = "Test Measure"
  }

  val dimensionTest = dimension(StringType()) {
    label = "Test Dimension"
  }
}
