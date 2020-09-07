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

import java.io.Serializable

/**
 * Print options
 *
 * // Paper
 * @param papertype   differents formats of paper
 * @param paperlayout the layout of the paper (Portrait or Landscape)
 * // Margins
 * @param topmargin    top sheet margin in points
 * @param bottommargin bottom sheet margin in points
 * @param leftmargin   left sheet margin in points
 * @param rightmargin  right sheet margin in points
 * @param headermargin header sheet margin in points
 * @param footermargin footer sheet margin in points
 * @param imageWidth   The image width to be used when exporting as image format.
 * @param imageHeight  The image height to be used when exporting as image format.
 */
class VPrintOptions(var papertype: Int = PPaperType.PPT_A4.code,
                    var paperlayout: String = "Landscape",
                    var topmargin: Int = 30,
                    var bottommargin: Int = 30,
                    var leftmargin: Int = 30,
                    var rightmargin: Int = 30,
                    var headermargin: Int = 5,
                    var footermargin: Int = 10,
                    var imageWidth: Int = 900,
                    var imageHeight: Int = 500) : Serializable
