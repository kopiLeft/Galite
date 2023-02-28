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
package org.kopi.galite.tests.ui.vaadin.field

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing._enter
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.form.Days
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TIME
import org.kopi.galite.visual.domain.TIMESTAMP
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.form.Block
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.ui.vaadin.field.TextField
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorField
import org.kopi.galite.visual.VColor

class FieldColorsTests: GaliteVUITestBase() {

  val form = FormWithColoredFields()

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
  }

  @Test
  fun `test color change in simple block fields`() {
    val field = (form.simpleBlock.stringField.findField() as TextField).inputField

    assertEquals(null, field.style["background-color"])
    assertEquals(null, field.style["color"])

    form.colorFields.triggerCommand()

    assertEquals("rgb(255,0,0) !important", field.style["background-color"])
    assertEquals("rgb(0,0,255) !important", field.style["color"])
  }

  @Test
  fun `test color change in multiblock fields`() {
    val field = form.multiBlock.stringField.findField() as GridEditorField

    form.multiBlock._enter()

    assertEquals(null, field.style["background-color"])
    assertEquals(null, field.style["color"])

    form.colorFields.triggerCommand()

    assertEquals("rgb(255,0,0) !important", field.style["background-color"])
    assertEquals("rgb(0,0,255) !important", field.style["color"])
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        initModules()
      }
    }
  }
}

class FormWithColoredFields: Form(title = "") {
  val action = menu("Action")

  val autoFill = actor(
    menu = action,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )

  val colorFields = actor(
    menu = action,
    label = "Color fields",
    help = "Color fields",
  ) {
    icon = Icon.PRINT
  }

  init {
    command(item = colorFields) {
      simpleBlock.stringField.setColor(0, VColor.BLUE, VColor.RED)
      simpleBlock.intField.setColor(0, VColor.BLUE, VColor.RED)
      simpleBlock.dateField.setColor(0, VColor.BLUE, VColor.RED)
      simpleBlock.timeField.setColor(0, VColor.YELLOW, VColor.RED)
      simpleBlock.timestampField.setColor(0, VColor.WHITE, VColor.RED)
      simpleBlock.booleanField.setColor(0, VColor.BLUE, VColor.RED)
      simpleBlock.checkboxField.setColor(0, VColor.GREEN, VColor.RED)


      multiBlock.stringField.setColor(0, VColor.BLUE, VColor.RED)
      multiBlock.intField.setColor(0, VColor.BLUE, VColor.RED)
      multiBlock.dateField.setColor(0, VColor.BLUE, VColor.RED)
      multiBlock.timeField.setColor(0, VColor.YELLOW, VColor.RED)
      multiBlock.timestampField.setColor(0, VColor.WHITE, VColor.RED)
      multiBlock.booleanField.setColor(0, VColor.BLUE, VColor.RED)
      multiBlock.checkboxField.setColor(0, VColor.GREEN, VColor.RED)
    }
  }

  val simpleBlock = insertBlock(SimpleBlock())
  val multiBlock = insertBlock(MultiBlock())

  inner class SimpleBlock: Block("", 1, 1) {
    val stringField = visit(STRING(10), position = at(1, 1)) {}
    val intField = visit(INT(10), position = at(1, 2)) {}
    val dateField = visit(DATE, position = at(2, 1)) {}
    val timeField = visit(TIME, position = at(2, 2)) {}
    val timestampField = visit(TIMESTAMP, position = at(2, 3)) {}
    val checkboxField = visit(domain = Days, position = at(3, 1)) {}
    val booleanField = visit(BOOL, position = at(4, 1)) {}
    // TODO: test actor field
  }

  inner class MultiBlock: Block("", 10, 10) {
    val stringField = visit(STRING(10), position = at(1, 1)) {}
    val intField = visit(INT(10), position = at(1, 2)) {}
    val dateField = visit(DATE, position = at(2, 1)) {}
    val timeField = visit(TIME, position = at(2, 2)) {}
    val timestampField = visit(TIMESTAMP, position = at(2, 3)) {}
    val checkboxField = visit(domain = Days, position = at(3, 1)) {}
    val booleanField = visit(BOOL, position = at(4, 1)) {}

    // TODO: test actor field
  }
}
