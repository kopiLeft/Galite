/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CharsetEncoder
import java.nio.charset.CoderResult

/**
 * Charset used to convert from UTF-16 to IBM437
 */
class Charset437 : Charset("437", null) {
  override operator fun contains(csd: Charset?): Boolean {
    return false
  }

  override fun newDecoder(): CharsetDecoder {
    return Decoder437(this)
  }

  override fun newEncoder(): CharsetEncoder {
    return Encoder437(this)
  }
}

/**
 * Decoder not implemented.
 */
class Decoder437(cs: Charset) : CharsetDecoder(cs, 1F, 1f) {
  // ----------------------------------------------------------------------
  // DECODING
  // ----------------------------------------------------------------------
  override fun decodeLoop(input: ByteBuffer, out: CharBuffer): CoderResult {
    while (input.hasRemaining() && out.hasRemaining()) {
      out.put('?')
    }
    return if (input.hasRemaining()) {
      CoderResult.OVERFLOW
    } else CoderResult.UNDERFLOW
  }
}

/**
 * Convert from UTF-16 to the charset 437.
 */
class Encoder437(cs: Charset?) : CharsetEncoder(cs, 1f, 1f) {
  // ----------------------------------------------------------------------
  // ENCODING
  // ----------------------------------------------------------------------
  override fun encodeLoop(input: CharBuffer, out: ByteBuffer): CoderResult {
    while (input.hasRemaining() && out.hasRemaining()) {
      val c = input.get()

      // the char we get is only 1 byte value.
      if (c.toInt() > 255) {
        return CoderResult.unmappableForLength(input.length)
      } else {
        out.put(convert(c))
      }
    }
    return if (input.hasRemaining()) {
      CoderResult.OVERFLOW
    } else CoderResult.UNDERFLOW
  }

  private fun convert(c: Char): Byte {
    return (if (c.toInt() >= 128) conversionTable[c.toInt() - 128] else c).toByte()
  }

  companion object {
    private val conversionTable: CharArray = intArrayOf(
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            255, 173, 155, 156, 128, 157, 0, 0,
            0, 196, 166, 174, 170, 0, 0, 0,
            248, 241, 253, 0, 0, 230, 0, 250,
            0, 0, 167, 175, 172, 171, 0, 168,
            0, 0, 0, 0, 142, 143, 146, 128,
            0, 144, 0, 0, 0, 0, 0, 0,
            0, 165, 0, 0, 0, 0, 153, 0,
            0, 0, 0, 0, 154, 0, 0, 225,
            133, 160, 131, 0, 132, 134, 145, 135,
            138, 130, 136, 137, 141, 161, 140, 139,
            0, 164, 149, 162, 147, 0, 148, 246,
            0, 151, 163, 150, 129, 0, 0, 152
    ).map { it.toChar() }.toCharArray()
  }
}
