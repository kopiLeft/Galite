/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.tests.pivottable

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
import org.kopi.galite.visual.dsl.pivottable.Dimension.Position
import org.kopi.galite.visual.dsl.pivottable.PivotTable

class PivotTableTests: VApplicationTestBase() {

  val Simplepivottable = SimplePivotTable()

  /**
   * Tests that fields has been registered in the pivot table.
   */
  @Test
  fun pivotTableFieldsTest() {
    assertEquals(Simplepivottable.dimensions[0], Simplepivottable.name)
    assertEquals(Simplepivottable.dimensions[1], Simplepivottable.age)
  }

  /**
   * Tests that fields has been registered in the pivot table.
   */
  @Test
  fun pivotTableDataTest() {
    val rows = Simplepivottable.getRowsForField(Simplepivottable.name)
    assertEquals(listOf("Sami", "Sofia", "Sofia"), rows)

    val firstRow = Simplepivottable.getRow(0)
    assertMapsEquals(mapOf(Simplepivottable.name to "Sami",
                           Simplepivottable.age to 22,
                           Simplepivottable.profession to "Journalist",
                           Simplepivottable.salary to BigDecimal("2000")),
                     firstRow)

    val secondRow = Simplepivottable.getRow(1)
    assertMapsEquals(mapOf(Simplepivottable.name to "Sofia",
                           Simplepivottable.age to 23,
                           Simplepivottable.profession to "Dentist",
                           Simplepivottable.salary to BigDecimal("2000.55")),
                     secondRow)

    val thirdRow = Simplepivottable.getRow(2)
    assertMapsEquals(mapOf(Simplepivottable.name to "Sofia",
                           Simplepivottable.age to 25,
                           Simplepivottable.profession to "Baker",
                           Simplepivottable.salary to BigDecimal("2000.55")),
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

    val sourceFilePath = Simplepivottable.javaClass.classLoader.getResource("").path +
            this.javaClass.`package`.name.replace(".", "/") + File.separatorChar
    Simplepivottable.genLocalization()

    val generatedFile = File("${sourceFilePath}/SimplePivotTable-${Simplepivottable.locale}.xml")
    assertEquals(true, generatedFile.exists())
    val document = builder.build(generatedFile)

    // Check that generated xml file contains fields localization
    val rootElement   = document.rootElement
    val actionMenu    = rootElement.children[0]
    val nameField     = rootElement.children[1]
    val ageField      = rootElement.children[2]
    assertEquals("pivotTable", rootElement.name)
    assertEquals("SimplePivotTable", rootElement.getAttributeValue("title"))
    assertEquals("actor0", actionMenu.getAttributeValue("ident"))
    assertEquals("Action", actionMenu.getAttributeValue("label"))
    assertEquals("DIMENSION_0", nameField.getAttributeValue("ident"))
    assertEquals("name", nameField.getAttributeValue("label"))
    assertEquals("The user name", nameField.getAttributeValue("help"))
    assertEquals("DIMENSION_1", ageField.getAttributeValue("ident"))
    assertEquals("age", ageField.getAttributeValue("label"))
    assertEquals("The user age", ageField.getAttributeValue("help"))
  }
}

/**
 * Simple pivot table with two fields.
 */
class SimplePivotTable : PivotTable(title = "SimplePivotTable", locale = Locale.UK) {

  val init = trigger(INIT) {
    println("---------INIT TRIGGER-------------")
  }

  val action = menu("Action")

  val name = dimension(STRING(20), Position.COLUMN) {
    label = "name"
    help = "The user name"
  }

  val age = dimension(INT(3), Position.ROW) {
    label = "age"
    help = "The user age"
  }

  val profession = dimension(STRING(20), Position.NONE) {
    label = "profession"
    help = "The user profession"
  }

  val salary = measure(DECIMAL(width = 10, scale = 5)) {
    label = "salary"
    help = "The user salary"
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
