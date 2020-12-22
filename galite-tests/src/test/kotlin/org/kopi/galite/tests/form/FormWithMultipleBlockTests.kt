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
package org.kopi.galite.tests.form

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.tests.JApplicationTestBase

class FormWithMultipleBlockTests : JApplicationTestBase() {

  @Test
  fun sourceFormTest() {
    val formModel = FormWithMultipleBlock.model
    assertEquals(FormWithMultipleBlock::class.qualifiedName!!.replace(".", "/"), formModel.source)
  }

  @Test
  fun ensureFormWithMultipleBlockDoesntCrash() {
    Application.run(formName = FormWithMultipleBlock)
  }

  @Test
  fun multipleBlockTest() {
    val formModel = FormWithMultipleBlock.model

    assertEquals(formModel.getBlock(1).bufferSize, 100)
    assertEquals(formModel.getBlock(1).displaySize, 100)
    assertEquals(formModel.getBlock(1).displayedFields, 1)

    val nameField = formModel.getBlock(1).fields[1]
    assertEquals(1, nameField.position!!.column)
    assertEquals(1, nameField.position!!.columnEnd)
    assertEquals(1, nameField.position!!.line)
    assertEquals(1, nameField.position!!.lineEnd)
    assertEquals(1, nameField.position!!.chartPos)
  }
}
