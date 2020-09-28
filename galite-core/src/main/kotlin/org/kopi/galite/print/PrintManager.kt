package org.kopi.galite.print

import org.kopi.galite.report.VReport
import org.kopi.galite.util.Printer
import org.kopi.galite.visual.VWindow

interface PrintManager {
  /**
   * Handle printing
   * @param    parent    the form that initiate the printing process
   * @param    report    the report to print
   * @param    copies  the number of copies to print
   * @param    printer    an optional default printer
   * @param    fax    an optional default fax number
   * @param    mail    an optional default mail address
   */
  fun print(parent: VWindow,
            report: VReport,
            copies: Int,
            printer: Printer,
            fax: String?,
            mail: String?)
}
