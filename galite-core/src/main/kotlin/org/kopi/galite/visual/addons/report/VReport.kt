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

package org.kopi.galite.visual.addons.report

import com.vaadin.flow.component.grid.Grid
import org.kopi.galite.visual.addons.common.Window
import org.kopi.galite.visual.field.Field
import org.kopi.galite.visual.report.Report

/**
 * Visual class for a report.
 */
class VReport(report: Report) : Window() {
  var reportGrid = Grid<MutableMap<Field<*>, Any>>()

  init {
    addColumns(report.fields)
    reportGrid.setItems(report.getLines())
    add(reportGrid)
  }

  // Add columns in the report based on the given fields
  fun addColumns(Fields: List<Field<*>>) {
    for (field in Fields) {
      reportGrid.addColumn { hashmap -> hashmap.get(field) }
          .setHeader(field.label)
          .setKey(field.label)
          .setSortable(true)
    }
  }

  private var table: Table? = null
}
