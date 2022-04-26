/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.galite.visual.base.UComponent
import org.kopi.galite.visual.form.Alignment
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.block.BlockLayout
import org.kopi.galite.visual.ui.vaadin.block.BlockListener
import org.kopi.galite.visual.ui.vaadin.block.ChartBlockLayout
import org.kopi.galite.visual.VException

import com.vaadin.flow.component.Component

/**
 * The `DChartBlock` is a [DBlock] representing
 * a chart block where data is represented in a data grid.
 *
 * @param parent The parent form.
 * @param model The block model.
 */
open class DChartBlock(parent: DForm, model: VBlock) : DBlock(parent, model), BlockListener {

  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  private var init = false

  /*
   * This flag was added to avoid mutual communication
   * between client and server side when the scroll bar
   * position is changed from the client side.
   * Thus, scroll bar position is not changed by server
   * side when it is changed from the client side
   * @see {@link #refresh(boolean)
   */
  private var scrolling = false

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun add(comp: UComponent?, constraints: Alignment) {
    addComponent(comp as? Component,
                 constraints.x,
                 constraints.y,
                 constraints.width,
                 constraints.height,
                 constraints.alignRight,
                 constraints.useAll)
  }

  override fun createLayout(): BlockLayout {
    val layout = ChartBlockLayout(displayedFields, model.displaySize + 1)

    layout.hasScroll = model.displaySize < model.bufferSize
    return layout
  }

  override fun validRecordNumberChanged() {
    if (model.displaySize < model.bufferSize) {
      updateScrollbar()
    }
  }

  override fun recordInfoChanged(rec: Int, info: Int) {
    // send the new records info
    fireRecordInfoChanged(rec, info)
    model.updateColor(rec)
  }

  override fun refresh(force: Boolean) {
    super.refresh(force)
    // don't update scroll bar when the scroll position
    // is being changing from the client side.
    if (!scrolling) {
      updateScrollbar()
    }
  }

  override fun onScroll(value: Int) {
    if ((getFormView() as DForm).inAction) {
      // do not change the rows if there is currently a
      // another command executed
      return
    }
    if (!init) {
      init = true // on initialization, we do not scroll.
    } else {
      try {
        scrolling = true
        setScrollPos(value)
        scrolling = false
      } catch (e: VException) {
        e.printStackTrace()
      }
    }
  }

  /**
   * Updates the scroll bar position.
   */
  private fun updateScrollbar() {
    access(currentUI) {
      val validRecords = model.numberOfValidRecord
      val dispSize = model.displaySize

      updateScroll(dispSize,
                   validRecords,
                   validRecords > dispSize,
                   model.getNumberOfValidRecordBefore(getRecordFromDisplayLine(0)))
    }
  }
}
