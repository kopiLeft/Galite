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

package org.kopi.galite.visual.util

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.InputStreamReader

import org.kopi.galite.visual.base.Utils

/**
 * DefaultPrinter
 */
abstract class AbstractPrinter protected constructor(private val name: String) : Printer {

  var numberOfCopies = 1 // the number of copy to print
  private var tray = 1
  private var paperFormat: String? = null

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

  companion object {
    fun convertToGhostscript(printData: PrintJob): PrintJob {
      val tempFile = Utils.getTempFile("kopigsconv", "PS")
      val gsJob = printData.createFromThis(tempFile, true)
      val ous = BufferedWriter(FileWriter(tempFile))

      /* READ HEADER */
      val reader = BufferedReader(InputStreamReader(printData.inputStream))
      var line: String
      var currentPage = -1

      while (reader.readLine().also { line = it } != null) {
        when {
          line == TOPRINTER_TRUE -> ous.write(TOPRINTER_FALSE)
          printData.numberOfPages == -1 && line.startsWith("%%Page: ") -> {
            currentPage = readCurrentPageNumber(line)
            ous.write(line)
          }
          else -> ous.write(line)
        }
        ous.write("\n")
      }
      ous.close()
      if (gsJob.numberOfPages == -1 && currentPage != -1) {
        gsJob.numberOfPages = currentPage
      }
      return gsJob
    }

    private fun readCurrentPageNumber(line: String): Int {

      val buffer = buildString {
        /* skip "%%Page: "*/
        line.filterIndexed { index, char -> index >= 8 && Character.isDigit(char) }
                .forEach { append(it) }
      }

      return if (buffer.isEmpty()) -1
      else try {
        buffer.toInt()
      } catch (e: NumberFormatException) {
        -1
      }
    }

    protected const val TOPRINTER_TRUE = "/toprinter {true} def"
    protected const val TOPRINTER_FALSE = "/toprinter {false} def"
  }
}
