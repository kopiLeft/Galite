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

package org.kopi.galite.visual.report

import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer

import org.kopi.galite.visual.util.base.InconsistencyException
import org.kopi.galite.visual.report.UReport.UTable

class PExport2CSV(table: UTable,
                  model: MReport,
                  pConfig: PConfig,
                  title: String)
  : PExport(table, model, pConfig, title), Constants {

  override fun export(stream: OutputStream) {
    try {
      writer = BufferedWriter(OutputStreamWriter(stream, "UTF-8"))
      exportData()
      (writer as BufferedWriter).flush()
      (writer as BufferedWriter).close()
    } catch (e: IOException) {
      throw InconsistencyException(e)
    }
  }

  override fun startGroup(subTitle: String?) {}
  override fun exportHeader(data: Array<String?>) {
    writeData(data)
  }

  override fun exportRow(level: Int, data: Array<String?>, orig: Array<Any?>, alignments: IntArray) {
    writeData(data)
  }

  private fun writeData(data: Array<String?>) {
    try {
      var first = true
      data.forEach { element ->
        if (!first) {
          writer.write("\t")
        }
        if (element != null) {
          writer.write(element)
        }
        first = false
      }
      writer.write("\n")
    } catch (e: IOException) {
      throw InconsistencyException(e)
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private lateinit var writer: Writer
}
