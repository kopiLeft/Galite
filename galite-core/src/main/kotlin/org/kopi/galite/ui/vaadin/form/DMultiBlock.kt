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
import org.kopi.galite.ui.vaadin.block.BlockLayout
import org.kopi.galite.ui.vaadin.block.MultiBlockLayout
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VRuntimeException

import com.vaadin.flow.component.Component

/**
 * The `DMultiBlock` is the UI implementation
 * of the [UMultiBlock] specification.
 *
 * @param parent The parent form.
 * @param model The block model.
 */
class DMultiBlock(parent: DForm, model: VBlock) : DChartBlock(parent, model), UMultiBlock {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun addToDetail(comp: UComponent?, constraints: Alignment) {
    /*(getLayout() as MultiBlockLayout).addToDetail(comp as Component?, TODO
                                                  constraints.x,
                                                  constraints.y,
                                                  constraints.width,
                                                  constraints.height,
                                                  constraints.alignRight,
                                                  constraints.useAll)*/
  }

  override fun inDetailMode(): Boolean = model.detailMode

  override fun createLayout(): BlockLayout {
    TODO()
  }

  override fun blockViewModeLeaved(block: VBlock, activeField: VField?) {
    try {
      // take care that value of current field
      // is visible in the other mode
      // Not field.updateText(); because the field is
      // maybe not visible in the Detail Mode
      activeField?.leave(true)
    } catch (ex: VException) {
      model.form.error(ex.message)
    }
  }

  override fun blockViewModeEntered(block: VBlock, activeField: VField?) {
    if (inDetailMode()) {
      try {
        // Show detail view

        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is
        // maybe not visible in the Detail Mode
        if (activeField == null) {
          //     getModel().gotoFirstField();
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
          // getModel().gotoFirstField();
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
        model.form.error(ex.message)
      }
    }
  }

  //---------------------------------------------------
  // MULTIBLOCK IMPLEMENTATION
  //---------------------------------------------------
  override fun switchView(row: Int) {
    // if this block is not the current block
    //!!! graf 20080521: is this possible?
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
    model.detailMode = !inDetailMode()
    //BackgroundThreadHandler.access(Runnable { TODO
    switchView(inDetailMode())
    //})
  }
}
