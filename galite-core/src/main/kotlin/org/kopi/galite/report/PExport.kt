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

import java.io.OutputStream
import java.io.Serializable

import org.kopi.galite.report.UReport.UTable

//TODO()
abstract class PExport: Serializable {

  /**
   * Constructor
   */
  constructor(table: UTable, model: MReport, pconfig: PConfig, title: String, tonerSaveMode: Boolean = false)

  protected abstract fun startGroup(subTitle: String)
  protected abstract fun exportRow(level: Int, data: Array<String>, orig: Array<Any>, alignment: IntArray)
  protected abstract fun exportHeader(data: Array<String>)
  protected abstract fun export(stream: OutputStream)
  fun formatStringColumn(column: VReportColumn, index: Int) {}
  protected fun formatDateColumn(column: VReportColumn, index: Int) {}
  protected fun formatMonthColumn(column: VReportColumn, index: Int) {}
  protected fun formatWeekColumn(column: VReportColumn, index: Int) {}
  protected fun formatFixedColumn(column: VReportColumn, index: Int) {}
  protected fun formatIntegerColumn(column: VReportColumn, index: Int) {}
  fun formatBooleanColumn(column: VReportColumn, index: Int) {}
  protected fun formatTimeColumn(column: VReportColumn, index: Int) {}
  protected fun formatTimestampColumn(column: VReportColumn, index: Int) {}
  protected open fun exportData() {}
}
