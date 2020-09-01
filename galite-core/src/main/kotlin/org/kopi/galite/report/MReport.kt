/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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

import java.util.*
import javax.swing.event.EventListenerList

class MReport {

  lateinit var columns: Array<VReportColumn>
  lateinit private var accessiblecolumns: Array<VReportColumn>

  private val root: VGroupRow? = null
  private val userRows: Vector<VBaseRow>? = null
  lateinit private var baseRows: Array<VReportRow>
  lateinit private var visibleRows: Array<VReportRow>
  private val maxRowCount = 0
  private val sortedColumn = 0
  private val sortingOrder = 0
  lateinit private var displayOrder: IntArray
  lateinit private var reverseOrder: IntArray
  lateinit private var displayLevels: IntArray
  protected var listenerList = EventListenerList()
}
