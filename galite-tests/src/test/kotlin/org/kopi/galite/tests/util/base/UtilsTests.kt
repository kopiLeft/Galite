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

package org.kopi.galite.tests.util.base

import org.kopi.galite.tests.TestBase
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.util.base.Utils

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
    var newFormat = utils.formatInteger(1, 20)
    assertEquals("00000000000000000001", newFormat)

    // case 2
    newFormat = utils.formatInteger(100, 20)
    assertEquals("00000000000000000100", newFormat)

    // case 3
    newFormat = utils.formatInteger(1000, 2)
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
    utils.verify(2 > 1, "2 must be greater than 1")

    // case 2 : A false assertion
    val exception = assertFailsWith<InconsistencyException> {
      // A false assertion
      utils.verify(1 > 2, "2 must be greater than 1")
    }
    assertEquals("2 must be greater than 1", exception.message)
  }

  /**
   * Tests Utils.splitQualifiedName method
   */
  @Test
  fun splitQualifiedNameTest() {
    // case 1
    var split = utils.splitQualifiedName("abc/def/ghi")
    assertArraysEquals(arrayOf("abc/def", "ghi"), split)

    // case 2
    split = utils.splitQualifiedName("ghi")
    assertArraysEquals(arrayOf("", "ghi"), split)

    // case 3
    split = utils.splitQualifiedName("")
    assertArraysEquals(arrayOf("", ""), split)

    // case 4
    split = utils.splitQualifiedName("/def")
    assertArraysEquals(arrayOf("", "def"), split)

    // case 5
    split = utils.splitQualifiedName("def/")
    assertArraysEquals(arrayOf("def", ""), split)

    // case 6
    split = utils.splitQualifiedName("/")
    assertArraysEquals(arrayOf("", ""), split)

    // case 7
    split = utils.splitQualifiedName("//")
    assertArraysEquals(arrayOf("/", ""), split)
  }

  /**
   * Tests Utils.subtring method
   */
  @Test
  fun subtringTest() {
    // case 1
    var substring = utils.substring("abcdefghi", 0, 3)
    assertEquals("abc", substring)

    // case 2
    substring = utils.substring("abcdefghi", 1, 4)
    assertEquals("bcd", substring)

    // case 3
    substring = utils.substring("abcdefghi", 0, 20)
    assertEquals("abcdefghi", substring)

    // case 4
    substring = utils.substring("abcdefghi", 10, 11)
    assertEquals("", substring)

    // case 5
    substring = utils.substring(null, 1, 11)
    assertEquals("", substring)
  }

  // Util instance to use in tests.
  private val utils = Utils()
}
