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

package org.kopi.galite.visual.chart

import java.io.Serializable

import org.kopi.galite.visual.util.PPaperType

/**
 * Print options
 *
 * // Paper
 * @param paperType   different formats of paper
 * @param paperLayout the layout of the paper (Portrait or Landscape)
 *
 * // Margins
 * @param topMargin    top sheet margin in points
 * @param bottomMargin bottom sheet margin in points
 * @param leftMargin   left sheet margin in points
 * @param rightMargin  right sheet margin in points
 * @param headerMargin header sheet margin in points
 * @param footerMargin footer sheet margin in points
 * @param imageWidth   The image width to be used when exporting as image format.
 * @param imageHeight  The image height to be used when exporting as image format.
 */
class VPrintOptions(val paperType: Int = PPaperType.PPT_A4.code,
                    val paperLayout: String = "Landscape",
                    val topMargin: Int = 30,
                    val bottomMargin: Int = 30,
                    val leftMargin: Int = 30,
                    val rightMargin: Int = 30,
                    val headerMargin: Int = 5,
                    val footerMargin: Int = 10,
                    val imageWidth: Int = 900,
                    val imageHeight: Int = 500) : Serializable
