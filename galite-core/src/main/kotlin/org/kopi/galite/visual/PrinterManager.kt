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

package org.kopi.galite.visual

import org.kopi.galite.util.Printer

abstract class PrinterManager {
  /**
   * Returns a print for previewing document
   */
  abstract fun getPreviewPrinter(): Printer

  /**
   * Property print.server
   * Returns the name of the print server to use. for printing
   */
  abstract fun getPrintServer(): String

  /**
   * Returns the preferred printer for the STANDARD_MEDIA
   */
  abstract fun getCurrentPrinter(): Printer

  /**
   * Returns the preferredPrinter for the specified media.
   */
  abstract fun getPreferredPrinter(media: String): Printer

  /**
   * Returns the currently selected printer for current user
   */
  abstract fun getPrinterByName(name: String): Printer

  /**
   * Returns a printer that support this kind of media or the default one if none
   */
  abstract fun getPrinterByMedia(media: String): Printer

  /**
   * Returns the default Printer for a kind of document
   */
  abstract fun getPrinterByDocumentType(documentType: String): Printer

  companion object {

    fun getPrinterManager(): PrinterManager {
      return ApplicationContext.applicationContext.getApplication().printerManager
    }

    fun setPrinterManager(manager: PrinterManager) {
      ApplicationContext.applicationContext.getApplication().printerManager = manager
    }
  }
}
