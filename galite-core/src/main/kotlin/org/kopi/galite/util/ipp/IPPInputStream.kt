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

package org.kopi.galite.util.ipp

import org.kopi.galite.util.base.InconsistencyException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.experimental.or

class IPPInputStream(var inputStream: InputStream) {

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  fun peekByte(): Byte {
    verify()
    inputStream.mark(1)
    val read: Int = read()
    inputStream.reset()
    return read.toByte()
  }

  fun peekShortAfterFirstByte(): Short {
    var i = 0

    verify()

    inputStream.mark(3)
    read()
    i = i or (read() shl 8)
    i = i or read()
    inputStream.reset()
    return i.toShort()
  }

  fun readByte(): Byte {
    return read().toByte()
  }

  fun readShort(): Short {
    var i: Short = 0
    i = i or (read() shl 8).toShort()
    i = i or read().toShort()
    return i
  }

  fun readInteger(): Int {
    var i = 0
    i = i or (read() shl 24)
    i = i or (read() shl 16)
    i = i or (read() shl 8)
    i = i or read()
    return i
  }

  fun readString(length: Int): String? {
    val buf = ByteArray(length)
    val nread = inputStream.read(buf, 0, length)
    if (nread != length) {
      throw IOException("Error reading socket: unexpected end of transmission")
    }
    return String(buf)
  }

  fun readLine(): String? {
    val sb = StringBuffer()
    var c: Int
    var end = false
    while (!end) {
      c = read()
      if (c == -1 || c == '\n'.toInt()) {
        end = true
      } else if (c != '\r'.toInt()) {
        sb.append(c.toChar())
      }
    }
    return sb.toString()
  }

  fun readArray(): ByteArray? {
    val buf = ByteArray(1024)
    val outputStream = ByteArrayOutputStream()
    var nread = 0
    while (inputStream.read(buf).also { nread = it } > 0) {
      outputStream.write(buf, 0, nread)
    }
    return outputStream.toByteArray()
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  private fun read(): Int {
    val c = inputStream.read()
    if (c == -1) {
      throw IOException("Error reading socket: unexpected end of transmission")
    }
    return c
  }

  private fun verify() {
    if (!inputStream.markSupported()) {
      throw InconsistencyException("Mark is not supported")
    }
  }
}
