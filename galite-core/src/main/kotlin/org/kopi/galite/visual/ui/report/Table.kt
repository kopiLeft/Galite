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

package org.kopi.galite.visual.ui.report

import com.vaadin.flow.component.grid.Grid
import org.kopi.galite.visual.report.Line
import org.kopi.galite.visual.report.Report

/**
 * Data table for of a report.
 */
class Table() : TreeGrid<Line>() {
  init {
    isColumnReorderingAllowed = true
  }

  /**
   * Fill tree table with data from report
   * @param report report that provides data
   */
  fun fillTable(report: Report) {
    setItems(report.lines.map { it }, report.lines.map { it.subLines })

    addHierarchyColumn {
      it.reportLine[report.fields[0]]
    }.setHeader(report.fields[0].label)

    report.fields.filter { field -> !field.equals(report.fields[0]) }
            .forEach { field ->
              addColumn {
                it.reportLine[field]
              }.setHeader(field.label).setSortable(true)
            }
  }
}

