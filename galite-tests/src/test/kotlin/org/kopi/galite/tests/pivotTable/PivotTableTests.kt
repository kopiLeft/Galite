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
package org.kopi.galite.tests.pivotTable

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
import org.kopi.galite.visual.dsl.pivotTable.PivotTable

class PivotTableTests: VApplicationTestBase() {

  val SimplePivotTable = SimplePivotTable()

  /**
   * Tests that fields has been registered in the pivot table.
   */
  @Test
  fun pivotTableFieldsTest() {
    assertEquals(SimplePivotTable.fields[0], SimplePivotTable.name)
    assertEquals(SimplePivotTable.fields[1], SimplePivotTable.age)
  }

  /**
   * Tests that fields has been registered in the pivot table.
   */
  @Test
  fun PivotTableDataTest() {
    val rows = SimplePivotTable.getRowsForField(SimplePivotTable.name)
    assertEquals(listOf("Sami", "Sofia", "Sofia"), rows)

    val firstRow = SimplePivotTable.getRow(0)
    assertMapsEquals(mapOf(SimplePivotTable.name to "Sami",
                           SimplePivotTable.age to 22,
                           SimplePivotTable.profession to "Journalist",
                           SimplePivotTable.salary to BigDecimal("2000")
    ),
                     firstRow)

    val secondRow = SimplePivotTable.getRow(1)
    assertMapsEquals(mapOf(SimplePivotTable.name to "Sofia",
                           SimplePivotTable.age to 23,
                           SimplePivotTable.profession to "Dentist",
                           SimplePivotTable.salary to BigDecimal("2000.55")),
                     secondRow)

    val thirdRow = SimplePivotTable.getRow(2)
    assertMapsEquals(mapOf(SimplePivotTable.name to "Sofia",
                           SimplePivotTable.age to 25,
                           SimplePivotTable.profession to "Baker",
                           SimplePivotTable.salary to BigDecimal("2000.55")),
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

    val sourceFilePath = SimplePivotTable.javaClass.classLoader.getResource("").path +
            this.javaClass.`package`.name.replace(".", "/") + File.separatorChar
    SimplePivotTable.genLocalization()

    val generatedFile = File("${sourceFilePath}/SimplePivotTable-${SimplePivotTable.locale}.xml")
    assertEquals(true, generatedFile.exists())
    val document = builder.build(generatedFile)

    // Check that generated xml file contains fields localization
    val rootElement   = document.rootElement
    val nameField     = rootElement.children[0]
    val ageField      = rootElement.children[1]
    assertEquals("pivotTable", rootElement.name)
    assertEquals("SimplePivotTable", rootElement.getAttributeValue("title"))
    assertEquals("ANM_0", nameField.getAttributeValue("ident"))
    assertEquals("name", nameField.getAttributeValue("label"))
    assertEquals("ANM_1", ageField.getAttributeValue("ident"))
    assertEquals("age", ageField.getAttributeValue("label"))
  }
}

/**
 * Simple Pivot table with two fields.
 */
class SimplePivotTable : PivotTable(title = "SimplePivotTable", locale = Locale.UK) {

  val name = field(STRING(20)) {
    label = "name"
  }

  val age = field(INT(3)) {
    label = "age"
  }

  val profession = field(STRING(20)) {
    label = "profession"
  }

  val salary = field(DECIMAL(width = 10, scale = 5)) {
    label = "salary"
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
