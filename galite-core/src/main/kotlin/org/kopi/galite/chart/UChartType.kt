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

package org.kopi.galite.chart

import org.kopi.galite.base.UComponent

import java.io.IOException
import java.io.OutputStream
import java.io.Serializable

/**
 * The Chart type view representation.
 */
interface UChartType : Serializable, UComponent {
  /**
   * Builds the content of this chart type.
   */
  fun build()

  /**
   * Refreshes the content of this chart type.
   */
  fun refresh()

  /**
   * Exports the chart type to the PDF format.
   *
   * @param destination Where to write the export.
   * @param options The print options.
   * @throws IOException I/O errors.
   */
  fun exportToPDF(destination: OutputStream, options: VPrintOptions)

  /**
   * Exports the chart type to the PNG format.
   *
   * @param destination Where to write the export.
   * @param width The image width.
   * @param height The image height.
   * @throws IOException I/O errors.
   */
  fun exportToPNG(destination: OutputStream, width: Int, height: Int)

  /**
   * Exports the chart type to the JPEG format.
   *
   * @param destination Where to write the export.
   * @param width The image width.
   * @param height The image height.
   * @throws IOException I/O errors.
   */
  fun exportToJPEG(destination: OutputStream, width: Int, height: Int)
}
