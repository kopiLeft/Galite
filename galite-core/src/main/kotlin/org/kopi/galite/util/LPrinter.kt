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

/**
 * Local printer
 */
class LPrinter(name: String, command: String) : AbstractPrinter(name) {

  /**
   * Print a file and return the output of the command
   */
  fun setCommand(command: String) {
    this.command = command
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  override fun print(data: PrintJob): String {
    val process = Runtime.getRuntime().exec(command)
    val data = data.inputStream
    val buffer = ByteArray(1024)
    val output = process.outputStream
    var length: Int

    while (data.read(buffer).also { length = it } != -1) {
      output.write(buffer, 0, length)
    }
    output.close()

    return "NYI"
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private var command: String? = null

  init {
    setCommand(command)
  }
}
