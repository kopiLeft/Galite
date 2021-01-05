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

import java.io.File
import java.util.Locale

import org.jdom2.input.SAXBuilder

import org.junit.Test

import org.kopi.galite.common.POSTREPORT
import org.kopi.galite.common.PREREPORT
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.VCCDepthFirstCircuitN
import org.kopi.galite.report.VCellFormat
import org.kopi.galite.report.VReportRow
import org.kopi.galite.tests.VApplicationTestBase

import kotlin.test.assertEquals

class ReportTests: VApplicationTestBase() {

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
    assertEquals(mapOf(SimpleReport.name to "Sami", SimpleReport.age to 22, SimpleReport.profession to "Journalist"), firstRow)

    val secondRow = SimpleReport.getRow(1)
    assertEquals(mapOf(SimpleReport.name to "Sofia", SimpleReport.age to 23, SimpleReport.profession to "Dentist"), secondRow)

    val thirdRow = SimpleReport.getRow(2)
    assertEquals(mapOf(SimpleReport.name to "Sofia", SimpleReport.age to 25, SimpleReport.profession to "Baker"), thirdRow)
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
            this.javaClass.packageName.replace(".", "/") + File.separatorChar
    SimpleReport.genLocalization()

    val generatedFile = File("${sourceFilePath}/SimpleReport-${SimpleReport.locale}.xml")
    assertEquals(true, generatedFile.exists())
    val document = builder.build(generatedFile)

    // Check that generated xml file contains fields localization
    val rootElement   = document.rootElement
    val actionMenu    = rootElement.children[0]
    val greetingActor = rootElement.children[1]
    val nameField     = rootElement.children[2]
    val ageField      = rootElement.children[3]
    assertEquals("report", rootElement.name)
    assertEquals("SimpleReport", rootElement.getAttributeValue("title"))
    assertEquals("Action", actionMenu.getAttributeValue("ident"))
    assertEquals("Action", actionMenu.getAttributeValue("label"))
    assertEquals("greeting", greetingActor.getAttributeValue("ident"))
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
object SimpleReport : Report() {
  override val locale = Locale.FRANCE

  override val title = "SimpleReport"

  val preReport = trigger(PREREPORT) {
    println("---------PREREPORT TRIGGER-------------")
  }

  val postReport = trigger(POSTREPORT) {
    println("---------POSTREPORT TRIGGER-------------")
  }

  val action = menu("Action")

  val greeting = actor(
          ident = "greeting",
          menu = action,
          label = "Greeting",
          help = "Click me to show greeting",
  ) {
    key  =  Key.F1          // key is optional here
    icon =  "ask"  // icon is optional here
  }

  val cmd = command(item = greeting) {
    action = {
      println("----------- Hello Galite ----------------")
      println("----------- I will show you help ----------------")
      model.showHelp()
    }
  }

  val name = field(Domain<String>(20)) {
    label = "name"
    help = "The user name"
    align = FieldAlignment.LEFT
    group = { age }
  }

  val age = field(Domain<Int>(3)) {
    label = "age"
    help = "The user age"
    align = FieldAlignment.LEFT
  }

  val profession = field(Domain<String>(20)) {
    label = "profession"
    help = "The user profession"
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val computed = field(Domain<Int>(4)) {
    label = "age2"
    help = "The age x 2"
    compute {
      object : VCCDepthFirstCircuitN() {
        override fun evalNode(row: VReportRow, column: Int): Any {
          return row.getValueAt(column) as Int * 2
        }

        override fun evalLeaf(row: VReportRow, column: Int): Any {
          return evalNode(row, column)
        }
      }
    }
  }

  init {
    add {
      this[name] = "Sami"
      this[age] = 22
      this[profession] = "Journalist"
    }
    add {
      this[name] = "Sofia"
      this[age] = 23
      this[profession] = "Dentist"
    }
    add {
      this[age] = 25
      this[profession] = "Baker"
      this[name] = "Sofia"
    }
  }
}
