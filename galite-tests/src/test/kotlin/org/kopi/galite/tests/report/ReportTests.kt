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
package org.kopi.galite.tests.report

import java.io.File
import java.math.BigDecimal
import java.util.Locale

import kotlin.test.assertEquals

import org.jdom2.input.SAXBuilder
import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.report.triggers.avgDecimal
import org.kopi.galite.visual.report.triggers.avgInteger

class ReportTests: VApplicationTestBase() {

  val SimpleReport = SimpleReport()

  /**
   * Tests that fields has been registered in the report.
   */
  @Test
  fun reportFieldsTest() {
    assertEquals(SimpleReport.fields[0], SimpleReport.name)
    assertEquals(SimpleReport.fields[1], SimpleReport.age)
  }

  /**
   * Tests that fields has been registered in the report.
   */
  @Test
  fun reportDataTest() {
    val rows = SimpleReport.getRowsForField(SimpleReport.name)
    assertEquals(listOf("Sami", "Sofia", "Sofia"), rows)

    val firstRow = SimpleReport.getRow(0)
    assertMapsEquals(mapOf(SimpleReport.name to "Sami",
                           SimpleReport.age to 22,
                           SimpleReport.profession to "Journalist",
                           SimpleReport.salary to BigDecimal("2000")
    ),
                     firstRow)

    val secondRow = SimpleReport.getRow(1)
    assertMapsEquals(mapOf(SimpleReport.name to "Sofia",
                           SimpleReport.age to 23,
                           SimpleReport.profession to "Dentist",
                           SimpleReport.salary to BigDecimal("2000.55")),
                     secondRow)

    val thirdRow = SimpleReport.getRow(2)
    assertMapsEquals(mapOf(SimpleReport.name to "Sofia",
                           SimpleReport.age to 25,
                           SimpleReport.profession to "Baker",
                           SimpleReport.salary to BigDecimal("2000.55")),
                     thirdRow)
  }

  /**
   * Tests that fields localization is generated to the xml file
   */
  @Test
  fun fieldLocalizationTest() {
    val builder = SAXBuilder()
    val tempDir = createTempDir("galite", "")
    tempDir.deleteOnExit()

    val sourceFilePath = SimpleReport.javaClass.classLoader.getResource("").path +
            this.javaClass.`package`.name.replace(".", "/") + File.separatorChar
    SimpleReport.genLocalization()

    val generatedFile = File("${sourceFilePath}/SimpleReport-${SimpleReport.locale}.xml")
    assertEquals(true, generatedFile.exists())
    val document = builder.build(generatedFile)

    // Check that generated xml file contains fields localization
    val rootElement   = document.rootElement
    val actionMenu    = rootElement.children[0]
    val greetingActor = rootElement.children[1]
    val nameField     = rootElement.children[6]
    val ageField      = rootElement.children[7]
    assertEquals("report", rootElement.name)
    assertEquals("SimpleReport", rootElement.getAttributeValue("title"))
    assertEquals("actor0", actionMenu.getAttributeValue("ident"))
    assertEquals("Action", actionMenu.getAttributeValue("label"))
    assertEquals("actor0", greetingActor.getAttributeValue("ident"))
    assertEquals("Greeting", greetingActor.getAttributeValue("label"))
    assertEquals("ANM_0", nameField.getAttributeValue("ident"))
    assertEquals("name", nameField.getAttributeValue("label"))
    assertEquals("The user name", nameField.getAttributeValue("help"))
    assertEquals("ANM_1", ageField.getAttributeValue("ident"))
    assertEquals("age", ageField.getAttributeValue("label"))
    assertEquals("The user age", ageField.getAttributeValue("help"))
  }
}

/**
 * Simple Report with two fields.
 */
class SimpleReport : Report(title = "SimpleReport", locale = Locale.UK) {

  val preReport = trigger(PREREPORT) {
    println("---------PREREPORT TRIGGER-------------")
  }

  val postReport = trigger(POSTREPORT) {
    println("---------POSTREPORT TRIGGER-------------")
  }

  val action = menu("Action")

  val greeting = actor(
          menu = action,
          label = "Greeting",
          help = "Click me to show greeting",
  ) {
    key  =  Key.F1
    icon =  Icon.ASK
  }

  val csv = actor(
          menu = action,
          label = "CSV",
          help = "Obtenir le format CSV",
  ) {
    key  =  Key.F8
    icon =  Icon.EXPORT_CSV
  }

  val xls = actor(
          menu = action,
          label = "XLS",
          help = "Obtenir le format Excel (XLS)",
  ) {
    key  =  Key.SHIFT_F8
    icon =  Icon.EXPORT_XLSX
  }

  val xlsx = actor(
          menu = action,
          label = "XLSX",
          help = "Obtenir le format Excel (XLSX)",
  ) {
    key  =  Key.SHIFT_F8
    icon =  Icon.EXPORT
  }

  val pdf = actor(
          menu = action,
          label = "PDF",
          help = "Obtenir le format PDF",
  ) {
    key  =  Key.F9
    icon =  Icon.EXPORT_PDF
  }

  val cmdCSV = command(item = csv) {
    model.export(VReport.TYP_CSV)
  }

  val cmdPDF = command(item = pdf) {
    model.export(VReport.TYP_PDF)
  }

  val cmdXLS = command(item = xls) {
    model.export(VReport.TYP_XLS)
  }

  val cmdXLSX = command(item = xlsx) {
    model.export(VReport.TYP_XLSX)
  }

  val cmd = command(item = greeting) {
    model.showHelp()
  }

  val name = field(STRING(20)) {
    label = "name"
    help = "The user name"
    align = FieldAlignment.LEFT
    group = age
    format { value ->
      value.uppercase()
    }
  }

  val age = field(INT(3)) {
    label = "age"
    help = "The user age"
    align = FieldAlignment.LEFT
    compute {
      // Computes the average of ages
      avgInteger()
    }
  }

  val profession = field(STRING(20)) {
    label = "profession"
    help = "The user profession"
  }

  val salary = field(DECIMAL(width = 10, scale = 5)) {
    label = "salary"
    help = "The user salary"
    align = FieldAlignment.LEFT
    compute {
      // Computes the average of ages
      avgDecimal()
    }
  }

  init {
    add {
      this[name] = "Sami"
      this[age] = 22
      this[profession] = "Journalist"
      this[salary] = BigDecimal("2000")
    }
    add {
      this[name] = "Sofia"
      this[age] = 23
      this[profession] = "Dentist"
      this[salary] = BigDecimal("2000.55")
    }
    add {
      this[age] = 25
      this[profession] = "Baker"
      this[name] = "Sofia"
      this[salary] = BigDecimal("2000.55")
    }
  }
}
