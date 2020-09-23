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

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A class that simplifies file writing
 */
class PlatformFileWriter(val dataStream: OutputStream,
                         val encoding: String,
                         val lineSeparator: String) {

  /**
   * Sets the number of copy to print
   */
  constructor(file: File,
              encoding: String,
              lineSeparator: String) : this(FileOutputStream(file), encoding, lineSeparator)

  /**
   * Writes a string to the file
   */
  fun write(string: String?) {
    if (string != null) {
      dataStream.write(string.toByteArray(charset(encoding)))
    }
  }

  /**
   * Write a newline character
   */
  fun nl() {
    write(lineSeparator)
  }

  /**
   * Writes a string and a newline character into the file
   */
  fun writeln(string: String?) {
    write(string)
    nl()
  }

  // ----------------------------------------------------------------------
  // CLOSE
  // ----------------------------------------------------------------------
  /**
   * Close the file
   */
  fun close() {
    dataStream.flush()
    dataStream.close()
  }
}
