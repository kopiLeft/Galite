/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.ui.report

import com.vaadin.flow.component.grid.Grid
import org.kopi.galite.report.MReport
import org.kopi.galite.report.Report
import org.kopi.galite.report.ReportRow

/**
 * Data table for of a report.
 */
class VTable(var model: MReport) : Grid<ReportRow>() {

  init {
    isColumnReorderingAllowed = true
    this.model = model
  }

  /**
   * Fill table with data from report
   * @param report report that provides data
   */
  fun fillTable(report: Report) {
    setItems(report.reportRows.map { it })

    report.fields.forEach { field ->
      addColumn {
        it.getValueOf(field)
      }.setHeader(field.label).setSortable(true)
    }
  }

  /**
   * Returns the column name of a given column index.
   * @param column The column index.
   * @return The column name.
   */
  fun getColumnName(column: Int): String {
    val label: String = model.columns[column]!!.label
    return if (label == null || label.isEmpty()) {
      ""
    } else label
  }

  /**
   * Returns the column count.
   * @return the column count.
   */
  fun getColumnCount(): Int {
    return model.getColumnCount()
  }

  /**
   * Notifies the table container that structure has been changed.
   */
  fun fireStructureChanged() {
    TODO()
  }

  override fun toString(): String {
    return model.getAccessibleColumn(columnIndex)!!.format(getValue())
  }

  //---------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------
  fun getValue(): Any? {
    return model.getAccessibleColumn(columnIndex)!!.format(model.getValueAt(rowIndex, columnIndex))
  }

  private val columnIndex = 0

  //---------------------------------------
  // DATA MEMBERS
  //---------------------------------------
  private val rowIndex = 0
}
