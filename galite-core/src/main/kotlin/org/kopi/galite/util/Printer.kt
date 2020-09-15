package org.kopi.galite.util

import java.io.IOException

interface Printer {
  /**
   * Unique name of the printer in the database which is chosen by the user
   */
  fun getPrinterName(): String?

  /**
   * Prints a Printjob
   */
  fun print(data: PrintJob?): String?

  // ----------------------------------------------------------------------
  // PRINT OPTIONS
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // PRINT OPTIONS
  // ----------------------------------------------------------------------
  /**
   * Sets the tray to use
   */
  fun selectTray(tray: Int)

  /**
   * Sets the paper format
   */
  fun setPaperFormat(paperFormat: String?)
}