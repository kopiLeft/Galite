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

package org.kopi.galite.report

import org.kopi.galite.util.PPaperType

import java.io.Serializable

class PConfig : Serializable {
  // Paper
  /** different formats of paper */
  val papertype = PPaperType.PPT_A4.code

  /** the layout of the paper (Portrait or Landscape)*/
  val paperlayout = "Landscape"

  // Margins
  /**top sheet margin in points*/
  val topmargin = 30

  /**bottom sheet margin in points*/
  val bottommargin = 30

  /**left sheet margin in points*/
  val leftmargin = 30

  /**right sheet margin in points*/
  val rightmargin = 30

  /**header sheet margin in points*/
  val headermargin = 5

  /**footer sheet margin in points*/
  val footermargin = 10

  // Cell to be displayed
  /**true if only visible rows must be displayed*/
  val visibleRows = true

  // Options
  /**order to used for printing*/
  val order = Constants.SUM_AT_TAIL

  /**cut the form at each big nivel*/
  val groupFormfeed = false

  // Grid and border
  /**horizontal grid*/
  val grid_H = 0.1

  /**vertical grid*/
  val grid_V = 0.1

  /**border size*/
  val border = 1.0

  /**report scale*/
  val reportScale = MIN_REPORT_SCALE

  companion object {
    const val MIN_REPORT_SCALE = 3.0
    const val MAX_REPORT_SCALE = 12.0
  }
}
