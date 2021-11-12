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

import kotlin.test.assertEquals
import kotlin.test.assertIs

import java.util.Locale
import org.junit.Test
import org.kopi.galite.visual.chart.VIntegerCodeMeasure
import org.kopi.galite.visual.chart.VStringCodeDimension
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.form.VStringCodeField
import org.kopi.galite.visual.report.VStringCodeColumn

/**
 * Contains tests of code-domain creation and manipulation
 */
class CodeDomainTests {


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
        val vField = testForm.block.field1.domain.buildFormFieldModel(testForm.block.field1)

        assertIs<VStringCodeField>(vField)
        assertIs<CodeDomain<String>>(testForm.block.field1.domain)
    }

    @Test
    fun buildReportFieldModel() {
        val testReport = TestReport()
        val vReportField = testReport.testField.domain.buildReportFieldModel(testReport.testField, null, null)

        assertIs<VStringCodeColumn>(vReportField)
        assertIs<CodeDomain<String>>(testReport.testField.domain)
    }

    @Test
    fun buildDimensionModel(){
        val testChart = TestChart()
        val vDimension = testChart.dimensionTest.domain.buildDimensionModel(testChart.dimensionTest, null)

        assertIs<VStringCodeDimension>(vDimension)
        assertIs<CodeDomain<String>>(testChart.dimensionTest.domain)
    }

    @Test
    fun buildMeasureModel(){
        val testChart = TestChart()
        val vDimension = testChart.measureTest.domain.buildMeasureModel(testChart.measureTest, null)

        assertIs<VIntegerCodeMeasure>(vDimension)
        assertIs<CodeDomain<String>>(testChart.dimensionTest.domain)
    }
}

class StringType : CodeDomain<String>() {
    init {
        "OK" keyOf "Yes"
        "Nope" keyOf "No"
    }
}

class IntegerType: CodeDomain<Int>(){
    init {
        "10" keyOf 100
        "20" keyOf 200
        "30" keyOf 300
    }
}

class TestBlock(buffer: Int, visible: Int, title: String) : FormBlock(buffer, visible, title) {
    val field1 = visit(StringType(), at(1, 1)) {
        label = "Initial field"
    }
}

class TestForm() : DictionaryForm() {
    override val locale: Locale = Locale.UK
    override val title: String = "Test Form"
    val block = insertBlock(TestBlock(1, 1, "Test Block"))
}

class TestReport(): Report(){

    override val locale = Locale.UK
    override val title = "Test Report"

    val testField = field(StringType()) {
        label = "Test field"
    }
}

class TestChart: Chart() {
    override val locale = Locale.UK
    override val title = "Test Chart"

    val measureTest = measure(IntegerType()) {
        label = "Test Measure"
    }

    val dimensionTest = dimension(StringType()) {
        label = "Test Dimension"

    }

    init {
        dimensionTest.add("OK") {
            this[measureTest] = 100
        }
        dimensionTest.add("Nope"){
            this[measureTest] = 200
        }

    }
}
