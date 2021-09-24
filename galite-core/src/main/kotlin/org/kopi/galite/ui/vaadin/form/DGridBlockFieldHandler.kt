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
package org.kopi.galite.ui.vaadin.form

import org.kopi.galite.form.VFieldUI

/**
 * Specific field handling for grid block
 */
class DGridBlockFieldHandler(rowController: VFieldUI) : DFieldHandler(rowController) {

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  override fun enter() {
    if (blockView.model.isChart() && blockView.inDetailMode()) {
      super.enter()
    } else {
      val editor = getCurrentDisplay() as DGridEditorField<*>?
      if (getRowController().getBlock().activeRecord != -1) {
        blockView.editRecord(getRowController().getBlock().activeRecord)
      }
      if (editor != null) {
        getRowController().resetCommands()
        editor.enter()
      }
    }
  }

  override fun leave() {
    if (blockView .model.isChart() && blockView.inDetailMode()) {
      super.leave()
    } else {
      val editor = getCurrentDisplay() as DGridEditorField<*>?
      if (editor != null) {
        getRowController().resetCommands()
        editor.leave()
      }
    }
  }

  override fun searchOperatorChanged() {
    // not yet implemented for grid labels
    if (blockView.model.isChart() && blockView.inDetailMode()) {
      super.searchOperatorChanged()
    }
  }

  override fun valueChanged(r: Int) {
    if (blockView.model.isChart() && blockView.inDetailMode()) {
      super.valueChanged(r)
    } else {
      val editor = getCurrentDisplay() as DGridEditorField<*>?

      if (editor != null && blockView.isEditorActive && blockView.editedRecord == r) {
        editor.updateText()
      }
      blockView.refreshRow(r)
    }
  }

  override fun colorChanged(r: Int) {
    if (blockView.model.isChart() && blockView.inDetailMode()) {
      super.colorChanged(r)
    } else {
      val editor = getCurrentDisplay() as DGridEditorField<*>?

      if (editor != null && blockView.isEditorActive && blockView.editedRecord == r) {
        editor.updateColor()
      }
    }
  }

  override fun accessChanged(row: Int) {
    if (blockView.model.isChart() && blockView.inDetailMode()) {
      super.accessChanged(row)
    } else {
      blockView.updateColumnAccess(model, row)
      if (blockView.isEditorActive && blockView.editedRecord == row) {
        getRowController().fireAccessHasChanged(row)
      }
    }
  }

  /**
   * Returns the grid block view attached with this field handler.
   * @return The grid block view attached with this field handler.
   */
  protected val blockView: DGridBlock
    get() = getRowController().blockView as DGridBlock
}
