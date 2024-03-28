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

import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.base.StyleManager
import org.kopi.galite.visual.ui.vaadin.visual.VApplication

/**
 * Specific field handling for grid block
 */
class DGridBlockFieldHandler(rowController: VFieldUI) : DFieldHandler(rowController) {

  protected val styleManager: StyleManager by lazy {
    (ApplicationContext.applicationContext.getApplication() as VApplication).styleManager
  }

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
    if (blockView.model.isChart() && blockView.inDetailMode()) {
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

//  fun colorChanged(r: Int) {
//    val columns = blockView.grid.columns
//    for ( i in 0 until  blockView.grid.columns.size) {
//      val column =  columns[i]
//
//    }
//  }

  override fun colorChanged(r: Int) {
    println("----- DGridBlockFieldHandler -----------colorChanged------- record : $r---- editor: ${getCurrentDisplay()} ")
    if (blockView.model.isChart() && blockView.inDetailMode()) {
      super.colorChanged(r)
    } else {
      val editor = getCurrentDisplay() as DGridEditorField<*>?
      if (editor != null && blockView.isEditorActive && blockView.editedRecord == r) {
        editor.updateColor()
//        println("********* editor condition validated  ************")

//        for (r in 0..<blockView.model.bufferSize) {
//          if (blockView.model.isRecordFilled(r)) {
////            styleManager.createAndApplyStyle(blockView.grid, model.align, model.getForeground(r), model.getBackground(r))
//            blockView.model.fields[r].setColor(model.getForeground(r), model.getBackground(r))
//          }
//        }
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
  internal val blockView: DGridBlock
    get() = getRowController().blockView as DGridBlock
}
