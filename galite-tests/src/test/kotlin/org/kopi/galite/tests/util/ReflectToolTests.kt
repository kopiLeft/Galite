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
import org.kopi.galite.tests.form.FormSample
import org.kopi.galite.util.ReflectTool
import org.kopi.galite.tests.form.TestBlock

import kotlin.test.assertEquals

class ReflectToolTests {

  /**
   * This test checks the elements' values of positions index in the form blocks
   */
  @Test
  fun reflectToolBlockTest() {
    val testBlock = TestBlock()
    assertEquals("job", ReflectTool.blockFieldAt(testBlock, 3))
    assertEquals("id", ReflectTool.blockFieldAt(testBlock, 2))
    assertEquals("age", ReflectTool.blockFieldAt(testBlock, 0))
    assertEquals("i", ReflectTool.blockIndexAt(testBlock, 0))
    assertEquals("u", ReflectTool.blockTableAt(testBlock, 0))
  }

  /**
   * This test checks the actor and the menu elements' values of positions index in  forms
   */
  @Test
  fun reflectToolFormTest() {
    assertEquals("autoFill", ReflectTool.formActorAt(FormSample, 0))
    assertEquals("action", ReflectTool.formMenuAt(FormSample, 0))
  }
}
