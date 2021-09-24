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

package org.kopi.galite.util

import java.io.InputStream

import org.kopi.galite.util.lpr.LpR
import org.kopi.galite.util.lpr.LpdException

/**
 * Remote execution client
 *
 * Creates a printer that send file to an lpd server
 */
class LpRPrinter(private val name: String,
                 private val serverHost: String,
                 private val port: Int,
                 private val proxyHost: String,
                 private val queue: String,
                 private val user: String) : Printer {

  init {
    selectTray(1) // Standard tray
    setPaperFormat(null)
  }

  override fun getPrinterName(): String = name

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

  override fun print(data: PrintJob): String = LprImpl(data).print()

  private inner class LprImpl(private val data: PrintJob) : LpR(serverHost,
                                                                port,
                                                                proxyHost,
                                                                queue,
                                                                user) {
    fun print(): String {
      try {
        if (data.title != null) {
          setTitle(data.title!!)
        }
        print(data.inputStream, null)
        close()
      } catch (e: LpdException) {
        throw PrintException(e.message!!, PrintException.EXC_UNKNOWN)
      }
      return "not yet implemented"
    }

    override fun readFully(inputStream: InputStream): ByteArray {
      val size = inputStream.available()
      val data = ByteArray(size)
      var count = 0

      while (count < size) {
        count += inputStream.read(data, count, size - count)
      }
      inputStream.close()
      return data
    }

    init {
      setPrintBurst(false)
    }
  }

  private var tray = 0
  private var paperFormat: String? = null
}
