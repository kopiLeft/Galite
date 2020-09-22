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
 * Fax printer
 */
/**
 * Constructs a fax printer
 */
// never used locally but must be implemented.
class FaxPrinter
private constructor(private val faxHost: String,
                    /**
                     * Gets the phone number
                     */
                    val number: String,
                    private val user: String,
                    private val id: String) : AbstractPrinter("FaxPrinter $number"), CachePrinter {
  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------
  /**
   * Print a file and return the output of the command
   */
  fun print(printdata: PrintJob): String {
    try {
      Fax.fax(faxHost, printdata.getInputStream(), user, number, id)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return "NYI"
  }
}
