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
package org.kopi.galite.ui.vaadin.list

import com.vaadin.flow.component.grid.Grid
import org.kopi.galite.form.VListDialog

class ListTable(val model: VListDialog) : Grid<List<Any?>>() {
  init {
    isColumnReorderingAllowed = true
    buildRows()
    buildColumns()
  }

  private fun buildRows() {
    val items: Array<List<Any?>?> = arrayOfNulls(model.count)

    for (row in 0 until model.count) {
      items[row] = model.data.map { it[model.translatedIdents[row]] }
    }

    setItems(*items)
  }

  private fun buildColumns() {
    for(col in 0 until model.getColumnCount()) {
      addColumn {
        formatObject(it[col], col)
      }.setHeader(model.getColumnName(col))
        .setAutoWidth(true)
        .setKey(col.toString())
    }
  }

  /**
   * Formats an object according to the property nature.
   * @param o The object to be formatted.
   * @return The formatted property object.
   */
  protected fun formatObject(o: Any?, col: Int): String {
    return model.columns[col]!!.formatObject(o).toString()
  }
}
