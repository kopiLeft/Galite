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
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.form.dsl.FieldAlignment
import org.kopi.galite.visual.form.dsl.Form
import org.kopi.galite.visual.form.dsl.FormBlock
import org.kopi.galite.visual.form.dsl.insertBlock

class FormDSLTests: VApplicationTestBase() {
  @Test
  fun `test generated model from a basic form`() {
    val form = BasicForm()
    val model = form.model
    assertEquals(form.locale, model.locale)
    assertEquals(form.title, model.getTitle())
  }

  @Test
  fun `test a simple form block`() {
    val form = FormWithOneSimpleBlock()
    val model = form.model

    assertCollectionsEquals(form.formBlocks, mutableListOf(form.block))
    assertEquals(1, model.blocks.size)

    val blockModel = model.blocks.single()

    assertEquals(form.block.title, blockModel.title)
    assertEquals(form.block.buffer, blockModel.bufferSize)
    assertEquals(form.block.visible, blockModel.displaySize)
    assertEquals(null, blockModel.alignment)
  }

  @Test
  fun `test a form field`() {
    val form = FormWithOneSimpleBlock()
    val block = form.model.blocks.single()
    val model = block.fields.single()

    assertEquals(form.block.idClt.label, model.label)
    assertEquals(form.block.idClt.help, model.toolTip)
    assertEquals(block, model.block)
    assertEquals(FieldAlignment.RIGHT.value, model.align)
  }
}

class BasicForm : Form() {
  override val locale = Locale.UK
  override val title = "Clients"
}

class FormWithOneSimpleBlock : Form() {
  override val locale = Locale.UK
  override val title = "Clients"
  val page = page("title")
  val block = page.insertBlock(SimpleBlock())

  inner class SimpleBlock : FormBlock(1, 1, "SimpleBlock") {
    override val help = "Information about the block"
    val idClt = visit(domain = INT(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
    }
  }
}
