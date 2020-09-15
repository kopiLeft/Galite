package org.kopi.galite.util

import java.io.IOException
import java.io.OutputStream


/**
 * Local printer
 */
class LPrinter(name: String, command: String) : AbstractPrinter(name) {
  /**
   * Print a file and return the output of the command
   */
  fun setCommand(command: String?) {
    this.command = command
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------
  fun print(printData: PrintJob): String {
    val process = Runtime.getRuntime().exec(command)
    val data = printData.getInputStream()
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

  /**
   *
   */
  init {
    setCommand(command)
  }
}
