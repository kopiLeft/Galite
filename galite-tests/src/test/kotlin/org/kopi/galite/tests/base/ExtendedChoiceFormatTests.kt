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

package org.kopi.galite.tests.base

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.visual.base.ExtendedChoiceFormat
import org.kopi.galite.visual.base.ExtendedMessageFormat

class ExtendedChoiceFormatTests {

  val extendedChoiceFormat = ExtendedChoiceFormat(doubleArrayOf(0.0, 0.5, 1.0), arrayOf("zero", "half", "one"), true)

  @Test
  fun testFormat() {
    assertEquals("one", extendedChoiceFormat.format(1.0))
    assertEquals("one", extendedChoiceFormat.format(1))
    assertEquals("half", extendedChoiceFormat.format(0.5))
    assertEquals("zero", extendedChoiceFormat.format(0))
    assertEquals("zero", extendedChoiceFormat.format(0.0))
    assertEquals("one", extendedChoiceFormat.format(2.0))
    assertEquals("one", extendedChoiceFormat.format(3.0))
  }

  @Test
  fun testFormatObject() {
    // internally .format will call .formatObject if the value isn't a Number
    assertEquals("one", extendedChoiceFormat.format("1.0"),)
    assertEquals("one", extendedChoiceFormat.format("0.0"),)
    assertEquals("one", extendedChoiceFormat.format(object {}),)
    assertEquals("zero", extendedChoiceFormat.format(ExtendedMessageFormat.NULL_REPRESENTATION))
  }
}
