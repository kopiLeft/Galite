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

import java.io.IOException

class IPPHttpHeader {
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  constructor(printerName: String, contentLength: Int) {
    name = printerName
    size = contentLength
  }

  constructor(iPPInputStream: IPPInputStream) {
    var line: String?
    line = iPPInputStream.readLine()
    if (line != null) {
      val lineFields = line.split(" ").toTypedArray()
      if (lineFields.size < 2 ||
              lineFields[1].toInt() != HTTP_OK) {
        throw IOException("Http error")
      }
    } else {
      throw IOException("Http error")
    }
    while (line != null && line.isNotEmpty()) {
      line = iPPInputStream.readLine()
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  fun write(os: IPPOutputStream) {
    os.writeString("POST $name HTTP/1.0\r\n")
    os.writeString("Content-type: application/ipp\r\n")
    os.writeString("""
  Host: localhost
  
  """.trimIndent())
    os.writeString("Content-length: $size\r\n")
    os.writeString("\r\n")
  }

  private var name: String? = null
  private var size = 0

  companion object {
    // --------------------------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------------------------
    private const val HTTP_OK = 200
  }
}
