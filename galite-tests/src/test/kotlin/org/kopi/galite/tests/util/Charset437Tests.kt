/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.util.Charset437
import org.kopi.galite.visual.util.Encoder437

class Charset437Tests : VApplicationTestBase() {
  val testChar: Charset437 = Charset437()
  val ibm437 = Charset.forName("IBM437")
  val testEncoder: Encoder437 = testChar.newEncoder() as Encoder437

  @Test
  fun encodeLoopTest1() {
    val testString = "test"
    val input: CharBuffer = CharBuffer.wrap(testString)
    val testByteBuffer = testEncoder.encode(input)
    val byteBuff: ByteBuffer = ByteBuffer.wrap(testString.toByteArray(ibm437))

    assertEquals(testByteBuffer, byteBuff)
  }

  @Test
  fun encodeLoopTest2() {
    val testString = "règlement"
    val input: CharBuffer = CharBuffer.wrap(testString)
    val testByteBuffer = testEncoder.encode(input)
    val byteBuff: ByteBuffer = ByteBuffer.wrap(testString.toByteArray(ibm437))

    assertEquals(testByteBuffer, byteBuff)
  }

  @Test
  fun encodeLoopTest3() {
    val testString = "?!&"
    val input: CharBuffer = CharBuffer.wrap(testString)
    val testByteBuffer = testEncoder.encode(input)
    val byteBuff: ByteBuffer = ByteBuffer.wrap(testString.toByteArray(ibm437))

    assertEquals(testByteBuffer, byteBuff)
  }
}
