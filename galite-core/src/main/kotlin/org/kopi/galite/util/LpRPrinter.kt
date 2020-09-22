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

import java.io.InputStream
import org.kopi.galite.util.lpr.LpR

/**
 * Creates a printer that send file to an lpd server
 */
class LpRPrinter(val name: String,
                 val serverHost: String,
                 val port: Int,
                 val proxyHost: String,
                 val queue: String,
                 val user: String) : Printer {

  init {
    selectTray(1)
    setPaperFormat(null)
  }

  override fun getPrinterName(): String? {
    return name
  }

  /**
   * Sets the tray to use
   */
  override fun selectTray(tray: Int) {
    this.tray = tray
  }

  /**
   * Sets the paper format
   */
  override fun setPaperFormat(paperFormat: String?) {
    this.paperFormat = paperFormat
  }

  // ----------------------------------------------------------------------
  // PRINTING WITH AN INPUTSTREAM
  // ----------------------------------------------------------------------
  override fun print(data: PrintJob?): String? {
    TODO()
  }

  private inner class LprImpl internal constructor(data: PrintJob) : LpR(serverHost, port, proxyHost, queue, user) {
    fun print(): String {
      TODO()
    }

    fun readFully(input: InputStream): ByteArray {
      val size = input.available()
      val data = ByteArray(size)
      var count: Int = 0
      while (count < size) {
        count += input.read(data, count, size - count)
      }
      input.close()
      return data
    }

    private val data: PrintJob = TODO()
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var tray = 0
  private var paperFormat: String? = null
}
