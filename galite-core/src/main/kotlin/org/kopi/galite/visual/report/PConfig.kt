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

package org.kopi.galite.visual.report

import java.io.Serializable

import org.kopi.galite.visual.util.PPaperType

/**
 * PConfig Class that allows to set the configuration parameters
 * @param papertype different formats of paper
 * @param paperlayout the layout of the paper (Portrait or Landscape)
 * @param topmargin top sheet margin in points
 * @param bottommargin bottom sheet margin in points
 * @param leftmargin left sheet margin in points
 * @param rightmargin right sheet margin in points
 * @param headermargin header sheet margin in points
 * @param footermargin footer sheet margin in points
 * @param visibleRows true if only visible rows must be displayed
 * @param order order to used for printing
 * @param groupFormfeed cut the form at each big nivel
 * @param grid_H horizontal grid
 * @param grid_V vertical grid
 * @param border border size
 * @param reportScale report scale
 */
data class PConfig(val papertype: Int = PPaperType.PPT_A4.code,
                   val paperlayout: String = "Landscape",
                   val topmargin: Int = 30,
                   val bottommargin: Int = 30,
                   val leftmargin: Int = 30,
                   val rightmargin: Int = 30,
                   val headermargin: Int = 5,
                   val footermargin: Int = 10,
                   val visibleRows: Boolean = true,
                   val order: Int = Constants.SUM_AT_TAIL,
                   val groupFormfeed: Boolean = false,
                   val grid_H: Double = 0.1,
                   val grid_V: Double = 0.1,
                   val border: Double = 1.0,
                   val reportScale: Double = MIN_REPORT_SCALE) : Serializable {

  companion object {
    const val MIN_REPORT_SCALE = 3.0
    const val MAX_REPORT_SCALE = 12.0
  }
}
