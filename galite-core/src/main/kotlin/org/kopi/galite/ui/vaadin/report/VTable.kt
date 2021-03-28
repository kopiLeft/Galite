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
import org.kopi.galite.report.VReportRow

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.function.SerializablePredicate

/**
 * The VTable is a vaadin [Grid] data provider adapted
 * to dynamic reports needs.
 *
 * @param model The table model.
 */
class VTable(internal val model: MReport): ListDataProvider<VReportRow>(model.getRows()) {

  init {
    addFilter {
      it != null
    }
  }

  override fun size(query: Query<VReportRow, SerializablePredicate<VReportRow>>?): Int {
    return model.getRowCount()
  }

  /**
   * Notifies the table data provider that content has been changed.
   */
  fun fireContentChanged() {
    for (i in 0 until model.getRowCount()) {
      for (j in 0 until model.getColumnCount()) {
        model.updateValueAt(i, j)
      }
    }
    refreshAll()
  }

  /**
   * Returns the accessible columns to display in the grid.
   */
  val accessibleColumns: Array<VReportColumn?> = model.accessibleColumns
}
