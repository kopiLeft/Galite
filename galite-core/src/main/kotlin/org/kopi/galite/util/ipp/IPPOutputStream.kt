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

import java.io.OutputStream
import java.nio.charset.Charset

class IPPOutputStream// --------------------------------------------------------------------
// CONSTRUCTORS
// --------------------------------------------------------------------
(private var os: OutputStream) {
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

// --------------------------------------------------------------------
// ACCESSORS
// --------------------------------------------------------------------

  // open fun writeByte(b: Int): Unit {
// os?.write((b.and(0xff)))
//}
  fun writeByte(b: Int) {
    os.write((b and 0xff))
  }
  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  fun writeShort(s: Int) {
    os.write(s and 0xff00 shr 8)
    os.write(s and 0xff)
  }


  fun writeInteger(i: Int) {
    os.write(i and -0x1000000 shr 24)
    os.write(i and 0xff0000 shr 16)
    os.write((i and 0xff00 shr 8))
    os.write((i and 0xff))
  }


  fun writeString(s: String) {
    val charset = Charset.forName("iso-8859-1")
    for (element in s) {
      os.write(element.toInt())
    }
  }

  fun writeArray(array: ByteArray) {
    os.write(array, 0, array.size)
  }
}
