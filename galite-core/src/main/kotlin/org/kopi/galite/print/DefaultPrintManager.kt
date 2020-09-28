package org.kopi.galite.print

import org.kopi.galite.report.VReport
import org.kopi.galite.util.PrintException
import org.kopi.galite.util.Printer
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VWindow

class DefaultPrintManager : PrintManager {

  /**
   * Handle printing
   * @param    parent     the form that initiate the printing process
   * @param    report     the report to print
   * @param    printer    an optional default printer
   * @param    fax        an optional default fax number
   * @param    mail       an optional default mail address
   */
  override fun print(parent: VWindow,
                     report: VReport,
                     copies: Int,
                     printer: Printer,
                     fax: String?,
                     mail: String?) {
    try {
      report.createPrintJob()
    } catch (exc: PrintException) {
      throw VExecFailedException(exc.message!!)
    }
  }

  companion object {

    fun getPrintManager(): PrintManager? {
      return if (ApplicationContext.getApplicationContext()!!.getApplication().getPrintManager() == null) {
        DefaultPrintManager()
      } else ApplicationContext.getApplicationContext()!!.getApplication().getPrintManager()
    }

    fun setPrintManager(printCopies: PrintManager?) {
      ApplicationContext.getApplicationContext()!!.getApplication().setPrintManager(printCopies)
    }
  }
}
