/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.form

import org.kopi.galite.visual.form.UChartLabel
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.ui.vaadin.label.SortableLabelListener

/**
 * The `DChartHeaderLabel` is the vaadin implementation
 * of the [UChartLabel] specifications.
 *
 * @param text The label text.
 * @param help The label help.
 * @param index The field index.
 * @param sortModel The sort model.
 * @param commands The fields command.
 */
class DChartHeaderLabel internal constructor(text: String?,
                                             help: String?,
                                             var fieldIndex: Int,
                                             val sortModel: VBlock.OrderModel) : DLabel(text, help), UChartLabel {

  init {
    sortModel.addSortingListener(this)
    sortable = true
    addSortableLabelListener(object : SortableLabelListener {
      override fun onSort() {
        sortModel.sortColumn(fieldIndex)
      }
    })
  }
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun orderChanged() {
    // TODO correct sort icon in client side if needed
  }

  override fun repaint() {
    // nothing to do
  }

  override fun isEnabled(): Boolean = true
}
