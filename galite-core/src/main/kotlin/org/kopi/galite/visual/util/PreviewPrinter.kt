/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.util

import java.io.File

/**
 * Local printer
 */
class PreviewPrinter(name: String, var command: String) : AbstractPrinter(name), Printer {

  constructor(command: String) : this("PreviewPrinter", command)

  constructor(command: String, previewFile: File) : this("PreviewPrinter", command)

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUT STREAM
  // ----------------------------------------------------------------------
  /**
   * Print a file and return the output of the command
   */
  override fun print(data: PrintJob): String {
    // execute in separate process
    val dataFile = File.createTempFile("kopiprinter", "ps")

    // file is used with an external program (and cache printer),
    // do not delete it
    data.writeDataToFile(dataFile)
    Runtime.getRuntime().exec("$command $dataFile")
    return "NYI"
  }
}
