/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.util.mailer

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

import javax.activation.DataSource

class Attachment(private val name: String, type: String?, input: InputStream) : DataSource {

  private val type = type ?: DEFAULT_TYPE
  val stream = input

  /**
   * Creates an attachment object.
   */
  constructor(file: File) : this(file.name, null, FileInputStream(file)) {}

  // ----------------------------------------------------------------------
  // interface DataSource
  // ----------------------------------------------------------------------
  override fun getName(): String {
    return name
  }

  override fun getContentType(): String {
    // graf 20060108: javax.activation.FileDataSouce verwenden?
    return type
  }

  /**
   * Return an InputStream for the data.
   * !!! Note - a new stream must be returned each time.
   */
  override fun getInputStream(): InputStream {
    return stream
  }

  override fun getOutputStream(): OutputStream {
    throw IOException("cannot do this")
  }

  companion object {
    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    private const val DEFAULT_TYPE = "application/octet-stream"
  }
}
