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

package org.kopi.galite.tests.util.base

import org.kopi.galite.tests.common.TestBase
import org.kopi.galite.visual.util.base.InconsistencyException
import org.kopi.galite.visual.util.base.Utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UtilsTests: TestBase() {
  /**
   * Tests Utils.formatInteger method
   */
  @Test
  fun formatIntegerTest() {
    // case 1
    var newFormat = Utils.formatInteger(1, 20)
    assertEquals("00000000000000000001", newFormat)

    // case 2
    newFormat = Utils.formatInteger(100, 20)
    assertEquals("00000000000000000100", newFormat)

    // case 3
    newFormat = Utils.formatInteger(1000, 2)
    assertEquals("1000", newFormat)
  }

  /**
   * Tests Utils.verifyAssertion method
   *
   * Doesn't do any action if the assertion is valid
   * and throws InconsistencyException otherwise
   */
  @Test
  fun verifyAssertionTest() {
    // case 1: A very simple correct assertion
    Utils.verify(2 > 1, "2 must be greater than 1")

    // case 2 : A false assertion
    val exception = assertFailsWith<InconsistencyException> {
      // A false assertion
      Utils.verify(1 > 2, "2 must be greater than 1")
    }
    assertEquals("2 must be greater than 1", exception.message)
  }

  /**
   * Tests Utils.splitQualifiedName method
   */
  @Test
  fun splitQualifiedNameTest() {
    // case 1
    var split = Utils.splitQualifiedName("abc/def/ghi")
    assertArraysEquals(arrayOf("abc/def", "ghi"), split)

    // case 2
    split = Utils.splitQualifiedName("ghi")
    assertArraysEquals(arrayOf("", "ghi"), split)

    // case 3
    split = Utils.splitQualifiedName("")
    assertArraysEquals(arrayOf("", ""), split)

    // case 4
    split = Utils.splitQualifiedName("/def")
    assertArraysEquals(arrayOf("", "def"), split)

    // case 5
    split = Utils.splitQualifiedName("def/")
    assertArraysEquals(arrayOf("def", ""), split)

    // case 6
    split = Utils.splitQualifiedName("/")
    assertArraysEquals(arrayOf("", ""), split)

    // case 7
    split = Utils.splitQualifiedName("//")
    assertArraysEquals(arrayOf("/", ""), split)
  }

  /**
   * Tests Utils.subtring method
   */
  @Test
  fun subtringTest() {
    // case 1
    var substring = Utils.substring("abcdefghi", 0, 3)
    assertEquals("abc", substring)

    // case 2
    substring = Utils.substring("abcdefghi", 1, 4)
    assertEquals("bcd", substring)

    // case 3
    substring = Utils.substring("abcdefghi", 0, 20)
    assertEquals("abcdefghi", substring)

    // case 4
    substring = Utils.substring("abcdefghi", 10, 11)
    assertEquals("", substring)

    // case 5
    substring = Utils.substring(null, 1, 11)
    assertEquals("", substring)
  }
}
