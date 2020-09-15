package org.kopi.galite.util

/**
 * Fax printer
 */
class FaxPrinter
/**
 * Constructs a fax printer
 */
// never used locally but must be implemented.
private constructor(// ----------------------------------------------------------------------
        // DATA MEMBERS
        // ----------------------------------------------------------------------
        private val faxHost: String,
        /**
         * Gets the phone nummer
         */
        val nummer: String,
        private val user: String,
        private val id: String) : AbstractPrinter("FaxPrinter $nummer"), CachePrinter {
  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------
  /**
   * Print a file and return the output of the command
   */
  fun print(printdata: PrintJob): String {
    try {
      Fax.fax(faxHost, printdata.getInputStream(), user, nummer, id)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return "NYI"
  }

}
