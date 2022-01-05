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
package org.kopi.galite.tests.ui.vaadin.field

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.enter
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.ui.vaadin.field.TextField
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorField
import org.kopi.galite.visual.visual.VColor

class FieldColorsTests: GaliteVUITestBase() {

  val form = FormWithColoredFields().also { it.model }

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

    form.multiBlock.enter()

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

class FormWithColoredFields: Form() {
  override val title: String = ""
  val action = menu("Action")

  val colorFields = actor(
    ident = "ColorFields",
    menu = action,
    label = "Color fields",
    help = "Color fields",
  ) {
    icon = Icon.PRINT
  }

  init {
    command(item = colorFields) {
      simpleBlock.stringField.setColor(0, VColor.BLUE, VColor.RED)
      multiBlock.stringField.setColor(0, VColor.BLUE, VColor.RED)
    }
  }

  val simpleBlock = insertBlock(SimpleBlock())
  val multiBlock = insertBlock(MultiBlock())

  inner class SimpleBlock: Block(1, 1, "") {
    val stringField = visit(STRING(10), position = at(1, 1)) {}
    // TODO: test actor field
  }

  inner class MultiBlock: Block(10, 10, "") {
    val stringField = visit(STRING(10), position = at(1, 1)) {}
    // TODO: test actor field
  }
}
