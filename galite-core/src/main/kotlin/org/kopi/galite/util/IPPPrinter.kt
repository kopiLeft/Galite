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

import org.kopi.galite.util.ipp.IPPClient

/**
 * IPP printer
 */
/**
 * Construct an IPP Printer
 *
 * @param host the IPP server host
 * @param port the IPP server port
 * @param printer the name of the IPP printer
 * @param user the name of the printer user
 * @param attributesForMedia a list of String[2] with the correpondance
 * between media and IPP attributes for this printer.
 */
class IPPPrinter(name: String,
                 private val host: String,
                 private val port: Int,
                 private val printer: String,
                 private val user: String,
                 private val attributesForMedia: List<*>) : AbstractPrinter(name), Printer {
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  val mediaTypes: List<*>
    get() {
      val client = IPPClient(host, port.toShort(), printer, user)
      return client.mediaTypes
    }

  /**
   * Set a given media for the printer.
   * Choose de attributes associated with this attribute for this printer.
   *
   * @return true iff the attribute is supported by this printer.
   */
  private fun getAttributes(media: String?): Array<String>? {
    return if (media == null) {
      null
    } else {
      val att = attributesForMedia.map { it as Array<String?> }.firstOrNull { it.size == 2 && it[0] == media }
      return when {
        att == null -> null
        att[1] == null -> null
        else -> att[1]!!.split(" ").toTypedArray()
      }
    }
  }
  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------
  /**
   * Print a file and return the output of the command
   */
  override fun print(printData: PrintJob?): String {
    val ippClient = IPPClient(host, port.toShort(), printer, user)
    ippClient.print(printData!!.getInputStream(),
            printData!!.numberCopy,
            getAttributes(printData.media))
    return "IPP Print"
  }

  override fun getPrinterName(): String? {
    TODO("Not yet implemented")
  }

  override fun selectTray(tray: Int) {
    TODO("Not yet implemented")
  }

  override fun setPaperFormat(paperFormat: String?) {
    TODO("Not yet implemented")
  }
}
