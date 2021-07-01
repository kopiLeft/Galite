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
package org.kopi.galite.ui.vaadin.report

import org.kopi.galite.report.MReport
import org.kopi.galite.report.VReportColumn
import org.kopi.galite.report.VSeparatorColumn

import com.vaadin.flow.function.SerializableFunction

/**
 * The `ColumnStyleGenerator` is the dynamic report
 * implementation for generating CSS class names for
 * cells in the column [column].
 *
 * @param model The report model.
 */
class ColumnStyleGenerator(private val model: MReport, val column: VReportColumn) : SerializableFunction<DReport.ReportModelItem, String> {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun apply(item: DReport.ReportModelItem): String =
    buildString {
      if (column is VSeparatorColumn) {
        append(" separator")
      } else {
        append(" level-" + model.getRow(item.rowIndex)!!.level)
      }
    }
}
