/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.print

import org.kopi.galite.visual.util.PrintException
import org.kopi.galite.visual.util.Printer
import org.kopi.galite.visual.visual.ApplicationContext
import org.kopi.galite.visual.visual.VExecFailedException
import org.kopi.galite.visual.visual.VWindow

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
                     report: Printable,
                     copies: Int,
                     printer: Printer,
                     fax: String?,
                     mail: String?) {
    try {
      report.createPrintJob()
    } catch (exc: PrintException) {
      throw VExecFailedException(exc.message)
    }
  }

  companion object {
    fun getPrintManager(): PrintManager {
      return ApplicationContext.applicationContext.getApplication().printManager ?: DefaultPrintManager()
    }

    fun setPrintManager(printCopies: PrintManager) {
      ApplicationContext.applicationContext.getApplication().printManager = printCopies
    }
  }
}
