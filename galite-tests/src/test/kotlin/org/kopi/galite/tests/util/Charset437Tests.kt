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
import org.kopi.galite.util.Charset437
import org.kopi.galite.util.Encoder437
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import kotlin.test.assertEquals

class Charset437Tests {
  var testChar: Charset437 = Charset437()
  var utf16 = Charset.forName("UTF-16")
  var ibm437 = Charset.forName("IBM437")
  var testEncoder: Encoder437 = testChar.newEncoder() as Encoder437

  @Test
  fun encodeLoopTest1() {
    val testString = "c"
    var input: CharBuffer = CharBuffer.wrap(testString)
    var testByteBuffer = testEncoder.encode(input)
    var byteBuff: ByteBuffer = ByteBuffer.wrap(testString.toByteArray(ibm437))

    assertEquals(true, testByteBuffer.equals(byteBuff))
  }

  @Test
  fun encodeLoopTest2() {
    val testString = "test"
    var input: CharBuffer = CharBuffer.wrap(testString)
    var testByteBuffer = testEncoder.encode(input)
    var byteBuff: ByteBuffer = ByteBuffer.wrap(testString.toByteArray(ibm437))

    assertEquals(true, testByteBuffer.equals(byteBuff))
  }

  @Test
  fun encodeLoopTest3() {
    val testString = "règlement"
    var input: CharBuffer = CharBuffer.wrap(testString)
    var testByteBuffer = testEncoder.encode(input)
    var byteBuff: ByteBuffer = ByteBuffer.wrap(testString.toByteArray(ibm437))

    assertEquals(true, testByteBuffer.equals(byteBuff))
  }

  @Test
  fun encodeLoopTest4() {
    val testString = "?!&"
    var input: CharBuffer = CharBuffer.wrap(testString)
    var testByteBuffer = testEncoder.encode(input)
    var byteBuff: ByteBuffer = ByteBuffer.wrap(testString.toByteArray(ibm437))

    assertEquals(true, testByteBuffer.equals(byteBuff))
  }
}
