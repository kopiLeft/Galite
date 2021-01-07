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

import org.kopi.galite.report.UReport.UTable

import com.vaadin.flow.data.renderer.ClickableRenderer.ItemClickListener

/**
 * The `DTable` is a table implementing the [UTable]
 * specifications.
 *
 * @param model The table model.
 */
class DTable(val model: VTable) : UTable, ItemClickListener<DTable> {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun convertColumnIndexToModel(viewColumnIndex: Int): Int {
    TODO()
  }

  override fun convertColumnIndexToView(modelColumnIndex: Int): Int {
    TODO()
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * The table selected row.
   */
  var selectedRow = -1

  /**
   * The selected column.
   */
  var selectedColumn = -1

  override fun onItemClicked(item: DTable?) {
    TODO("Not yet implemented")
  }
}
