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
package org.kopi.galite.tests.util

import org.junit.Test
import org.kopi.galite.util.ReflectTool
import org.kopi.galite.tests.form.TestBlock
import org.kopi.galite.tests.form.TestForm
import kotlin.test.assertEquals

class ReflectToolTests {

  /**
   * This test checks the elements' values of positions index in the form blocks
   */
  @Test
  fun reflectToolBlockTest() {
    assertEquals("name", ReflectTool.blockFieldAt(TestBlock, 2))
    assertEquals("id", ReflectTool.blockFieldAt(TestBlock, 1))
    assertEquals("age", ReflectTool.blockFieldAt(TestBlock, 0))
    assertEquals("i", ReflectTool.blockIndexAt(TestBlock, 0))
    assertEquals("u", ReflectTool.blockTableAt(TestBlock, 0))
  }

  /**
   * This test checks the actor and the menu elements' values of positions index in  forms
   */
  @Test
  fun reflectToolFormTest() {
    assertEquals("graph", ReflectTool.formActorAt(TestForm, 0))
    assertEquals("action", ReflectTool.formMenuAt(TestForm, 0))
  }
}
