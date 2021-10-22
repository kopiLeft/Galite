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
package org.kopi.galite.tests.ui.vaadin.triggers

import java.util.Locale

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.open
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.testing.click
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FormBlock

class TriggersTests : GaliteVUITestBase() {

  val form = FormToTestTriggers().also { it.model }

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
  }

  /**
   * put value in the first field, leave it
   * and check that a second field contains data from POSTCHG field trigger
   */
  @Test
  fun `test POSTCHG field trigger`() {
    val field = form.salesSimpleBlock.description.findField()

    assertEquals("description value", field.value)
    form.salesSimpleBlock.idClt.edit(20)
    form.salesSimpleBlock.information.click()
    assertEquals("sales description", field.value)
  }

  /**
   * assert that the field contains data from the VALUE field trigger
   */
  @Test
  fun `test VALUE field trigger`() {
    val field = form.salesSimpleBlock.information.findField()

    assertEquals("sales information", field.value)
  }

  /**
   * assert that the field contains data from the INIT block trigger
   */
  @Test
  fun `test INIT block trigger`() {
    val field = form.salesSimpleBlock.description.findField()

    assertEquals("description value", field.value)
  }

  /**
   * assert that the field contains data from the PREBLK block trigger
   */
  @Test
  fun `test PREBLK block trigger`() {
    val field = form.block.idClt.findField()

    assertEquals("50", field.value)
  }

  @Ignore
  @Test
  fun `test ACTION field trigger`() {
    val field = form.salesSimpleBlock.description.findField()

    assertEquals("description value", field.value)
    form.salesSimpleBlock.action.click()
    assertEquals("item description", field.value)
  }

  @Test
  fun `test DEFAULT trigger`() {
    form.salesSimpleBlock.idClt.click()
    val fieldEditedByBlock = form.salesSimpleBlock.defaultValueFromBlock.findField()
    val field = form.salesSimpleBlock.defaultValueFromField.findField()

    assertEquals("Setting by block", fieldEditedByBlock.value)
    assertEquals("Default field value", field.value)
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        org.kopi.galite.tests.examples.initModules()
      }
    }
  }
}

class FormToTestTriggers : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Form to test triggers"
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )

  val block = insertBlock(Clients()) {
    trigger(PREBLK) {
      idClt.value = 50
    }
  }
  val salesSimpleBlock = insertBlock(SalesSimpleBlock()) {
    trigger(PREBLK) {
      vBlock.setDefault()
    }
    trigger(DEFAULT) {
      defaultValueFromBlock.value = "Setting by block"
    }

    trigger(INIT) {
      description.value = "description value"
    }
  }

  inner class Clients : FormBlock(1, 1, "Clients") {
    val idClt = visit(domain = INT(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
    }
  }

  inner class SalesSimpleBlock : FormBlock(1, 1, "Sales") {
    val idClt = visit(domain = INT(5), position = at(1, 1..2)) {
      label = "ID"
      help = "The item id"
      trigger(POSTCHG) {
        description.value = "sales description"
      }
    }
    val description = visit(domain = STRING(25), position = at(2, 1)) {
      label = "Description"
      help = "The item description"
    }
    val information = visit(domain = STRING(25), position = at(2, 2)) {
      label = "information"
      help = "The item information"
      trigger(VALUE) {
        "sales information"
      }
    }
    val action = visit(domain =  STRING(25), position = at(3, 1)) {
      label = "action"
      help = "The item action"
      trigger(ACTION) {
        description.value = "item description"
      }
    }
    val defaultValueFromBlock = visit(domain =  STRING(25), position = at(4, 1)) {
      label = "default value from block"
    }
    val defaultValueFromField = visit(domain =  STRING(25), position = at(5, 1)) {
      label = "default value from field"
      trigger(DEFAULT) {
        this.value = "Default field value"
      }
    }
  }
}
