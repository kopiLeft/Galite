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
 *
 * Represents an IPP Printer
 *
 * @param host the IPP server host
 * @param port the IPP server port
 *
 * @param printer the name of the IPP printer
 * @param user the name of the printer user
 * @param attributesForMedia a list of String[2] with the correspondence
 * between media and IPP attributes for this printer.
 */
class IPPPrinter(name: String,
                 private val host: String,
                 private val port: Int,
                 private val printer: String,
                 private val user: String,
                 private val attributesForMedia: List<Array<String>>)
      : AbstractPrinter(name), Printer {

  fun getMediaTypes(): List<*> {
      val client = IPPClient(host, port.toShort(), printer, user)

      return client.getMediaTypes()
    }

  /**
   * Set a given media for the printer.
   * Choose de attributes associated with this attribute for this printer.
   *
   * @return true if the attribute is supported by this printer.
   */
  private fun getAttributes(media: String?): Array<String>? {
    return if (media == null) {
      null
    } else {
      attributesForMedia.forEach { att ->
      if (att.size == 2 && att[0] == media) {
          return if (att[1] == null) null else att[1].split(" ").toTypedArray()
        }
      }
      null
    }
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------

  /**
   * Print a file and return the output of the command
   */
  override fun print(data: PrintJob): String {
    val ippClient = IPPClient(host, port.toShort(), printer, user)

    ippClient.print(data.inputStream,
                    data.numberOfCopies,
                    if (data.media == null) null else getAttributes(data.media))
    return "IPP Print"
  }
}
