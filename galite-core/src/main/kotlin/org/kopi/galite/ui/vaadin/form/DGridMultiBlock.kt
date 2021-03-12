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
package org.kopi.galite.ui.vaadin.form

import org.kopi.galite.base.UComponent
import org.kopi.galite.form.Alignment
import org.kopi.galite.form.UMultiBlock
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VField
import org.kopi.galite.ui.vaadin.block.SimpleBlockLayout
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VRuntimeException

import com.vaadin.flow.component.Component

/**
 * A based Grid multi block implementation
 */
class DGridMultiBlock(parent: DForm,
                      model: VBlock)
  : DGridBlock(parent, model), UMultiBlock /*, DetailsGenerator TODO */ {

  private var detail: SimpleBlockLayout? = null
  private var itemHasDetailVisible: Int? = null

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  override fun switchView(row: Int) {
    if (model.form.getActiveBlock() != model) {
      if (!model.isAccessible) {
        return
      }
      try {
        model.form.gotoBlock(model)
      } catch (ex: Exception) {
        (getFormView() as DForm).reportError(VRuntimeException(ex.message, ex))
        return
      }
    }
    if (row >= 0) {
      model.gotoRecord(getRecordFromDisplayLine(row))
    } else if (getDisplayLine() >= 0) {
      model.gotoRecord(getRecordFromDisplayLine(getDisplayLine()))
    }
    model.isDetailMode = !inDetailMode()
    /*BackgroundThreadHandler.access(Runnable { TODO
      if (grid.getEditedItemId() != null) {
        itemHasDetailVisible = grid.getEditedItemId()
        grid.setDetailsVisible(grid.getEditedItemId(), inDetailMode())
        if (grid.isEditorActive()) {
          grid.cancelEditor()
        }
      }
    })*/
  }

  override fun getRecordFromDisplayLine(line: Int): Int {
    return if (inDetailMode() && itemHasDetailVisible != null) {
      itemHasDetailVisible!!
    } else {
      super.getRecordFromDisplayLine(line)
    }
  }

  override fun addToDetail(comp: UComponent?, constraint: Alignment) {
    if (detail == null) {
      detail = SimpleBlockLayout(2 * maxColumnPos, maxRowPos)
      // detail.addStyleName("grid-detail") TODO
    }
    // block will not be marked in detail in client side
    // we force the field to be on the chart view as a hack
    // to allow free navigation
    if (comp is DField) {
      comp.noChart = false
    }
    detail!!.addComponent(comp as Component,
                          constraint.x,
                          constraint.y,
                          constraint.width,
                          constraint.height,
                          constraint.alignRight,
                          constraint.useAll)
  }

  /*fun getDetails(rowReference: RowReference?): Component? { TODO
    return detail
  }*/

  override fun inDetailMode(): Boolean = model.isDetailMode

  override fun blockViewModeLeaved(block: VBlock, activeField: VField?) {
    // send active record to client side before view switch
    if (!inDetailMode()) {
      //BackgroundThreadHandler.access(Runnable { TODO
      fireActiveRecordChanged(model.activeRecord)
      //})
    }
    try {
      // take care that value of current field
      // is visible in the other mode
      // Not field.updateText(); because the field is
      // maybe not visible in the Detail Mode
      activeField?.leave(true)
    } catch (ex: VException) {
      ex.printStackTrace()
      model.form.error(ex.message)
    }
  }

  override fun blockViewModeEntered(block: VBlock, activeField: VField?) {
    if (inDetailMode()) {
      // update detail view access and texts
      updateDetailDisplay()
      try {
        // Show detail view

        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is
        // maybe not visible in the Detail Mode
        if (activeField == null) {
          //     model.gotoFirstField();
        } else {
          if (!activeField.noDetail()) {
            // field is visible in chartView
            activeField.enter()
          } else {
            // field is not visible in in chart view:
            // go to the next visible field
            block.activeField = activeField
            model.gotoNextField()
          }
        }
      } catch (ex: VException) {
        ex.printStackTrace()
        model.form.error(ex.message)
      }
    } else {
      try {
        // show chart view

        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is
        // maybe not visible in the Detail Mode
        if (activeField == null) {
          // model.gotoFirstField();
        } else {
          if (!activeField.noChart()) {
            // field is visible in chartView
            activeField.enter()
          } else {
            // field is not visible in in chart view:
            // go to the next visible field
            block.activeField = activeField
            model.gotoNextField()
          }
        }
      } catch (ex: VException) {
        ex.printStackTrace()
        model.form.error(ex.message)
      }
    }
  }

  override fun blockChanged() {
    super.blockChanged()
    if (model.activeRecord != -1 && itemHasDetailVisible != null && model.activeRecord != itemHasDetailVisible) {
      enterRecord(model.activeRecord)
    }
  }

  override fun enterRecord(recno: Int) {
    /*BackgroundThreadHandler.access(Runnable { TODO
      if (inDetailMode() && itemHasDetailVisible != null) {
        grid.setDetailsVisible(itemHasDetailVisible, false)
        model.isDetailMode = false
        if (recno != itemHasDetailVisible) {
          itemHasDetailVisible = recno
          model.isDetailMode = true
          grid.setDetailsVisible(itemHasDetailVisible, true)
        }
      }
    })*/
    super.enterRecord(recno)
  }

  /**
   * Updates the state of the detail display
   */
  protected fun updateDetailDisplay() {
    for (columnView in columnViews) {
      if (columnView?.detailDisplay != null) {
        columnView.detailDisplay!!.updateAccess()
        columnView.detailDisplay!!.updateText()
        columnView.detailDisplay!!.updateColor()
      }
    }
  }
}
