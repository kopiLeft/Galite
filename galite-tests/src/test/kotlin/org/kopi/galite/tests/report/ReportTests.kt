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
import org.kopi.galite.domain.Domain
import org.kopi.galite.report.Report
import org.kopi.galite.tests.ApplicationTestBase
import kotlin.test.assertEquals

class ReportTests: ApplicationTestBase() {

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
    assertEquals(listOf("Sami", "Safia"), rows)

    val firstRow = SimpleReport.getRow(0)
    assertEquals(mapOf(SimpleReport.name to "Sami", SimpleReport.age to 22), firstRow)

    val secondRow = SimpleReport.getRow(1)
    assertEquals(mapOf(SimpleReport.name to "Safia", SimpleReport.age to 23), secondRow)
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
    val rootElement = document.rootElement
    val nameField   = rootElement.children[0]
    val ageField    = rootElement.children[1]
    assertEquals("report", rootElement.name)
    assertEquals("SimpleReport", rootElement.getAttributeValue("title"))
    assertEquals("name", nameField.getAttributeValue("ident"))
    assertEquals("name", nameField.getAttributeValue("label"))
    assertEquals("The user name", nameField.getAttributeValue("help"))
    assertEquals("age", ageField.getAttributeValue("ident"))
    assertEquals("age", ageField.getAttributeValue("label"))
    assertEquals("The user age", ageField.getAttributeValue("help"))
  }

  /**
   * Simple Report with two fields.
   */
  object SimpleReport : Report() {
    override val locale = Locale.FRANCE

    override val title = "SimpleReport"

    override val reportCommands = true

    val name = field(StringTestType()) {
      label = "name"
      help = "The user name"
    }

    val age = field(IntTestType()) {
      label = "age"
      help = "The user age"
    }

    init {
      add {
        this[name] = "Sami"
        this[age] = 22
      }
      add {
        this[name] = "Safia"
        this[age] = 23
      }
    }
  }

  class StringTestType : Domain<String>(5, String::class) {
    override val type = code {
      this["cde1"] = "test1"
    }
  }

  class IntTestType : Domain<Int>(3, Int::class) {
    override val type = code {
      this["cde1"] = 1
    }
  }
}
