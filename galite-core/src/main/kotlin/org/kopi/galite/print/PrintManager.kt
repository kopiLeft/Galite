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
package org.kopi.galite.print

import org.kopi.galite.print.Printable
import org.kopi.galite.util.Printer
import org.kopi.galite.visual.VWindow

interface PrintManager {
  /**
   * Handle printing
   * @param    parent    the form that initiate the printing process
   * @param    report    the report to print
   * @param     copies  the number of copies to print
   * @param    printer    an optional default printer
   * @param    fax    an optional default fax number
   * @param    mail    an optional default mail address
   */
  fun print(parent: VWindow,
            report: Printable,
            copies: Int,
            printer: Printer,
            fax: String,
            mail: String)
}
