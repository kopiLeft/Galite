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

package org.kopi.galite.tests.util

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.common.TestBase
import org.kopi.galite.visual.util.LineBreaker

class LineBreakerTests : TestBase() {
  val source = "This text contains\ncarriage\nreturn."
  val sourceWithoutCarriage = "This text contains carriage return."

  @Test
  fun textToModelTest() {
    assertEquals("This text contains  carriagereturn.", LineBreaker.textToModel(source, 4, 3))
    assertEquals("This text contains", LineBreaker.textToModel(source, 1, 1))
    assertEquals("This text containscarriage", LineBreaker.textToModel(source, 2, 2))
    assertEquals("This text containscarriagereturn.", LineBreaker.textToModel(source, 2, 5))
    assertEquals("This text containscarriage ", LineBreaker.textToModel(source, 9, 2))
  }

  @Test
  fun modelToTextTest() {
    assertEquals("This text\n contains\n carriage\n return.", LineBreaker.modelToText(sourceWithoutCarriage, 9))
  }

  @Test
  fun addBreakForWidth() {
    assertEquals("\nThis \ntext \ncontains \ncarriage \nreturn.", LineBreaker.addBreakForWidth(sourceWithoutCarriage, 1))
    assertEquals("\nThis \ntext \ncontains \ncarriage \nreturn.", LineBreaker.addBreakForWidth(sourceWithoutCarriage, 4))
  }

  @Test
  fun splitForWidthTest() {
    assertArraysEquals(arrayOf("", "This ", "text ", "contains ", "carriage ", "return."), LineBreaker.splitForWidth(sourceWithoutCarriage, 4))
  }
}
