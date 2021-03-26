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

import org.kopi.galite.form.AbstractFieldHandler
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.base.Utils

/**
 * The `DFieldHandler` is the vaadin implementation of the
 * field handler specifications.
 *
 * @param rowController The row controller.
 */
open class DFieldHandler internal constructor(rowController: VFieldUI) : AbstractFieldHandler(rowController) {
  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  override fun updateModel() {
    // model needs to be updated only when UI value has changed
    if (model.isChangedUI && model.hasFocus()) {
      model.checkType(getDisplayedValue(true))
    }
  }

  override fun predefinedFill(): Boolean {
    val filled = model.fillField(VPredefinedValueHandler(getRowController(), model.getForm(), model))
    if (filled) {
      getRowController().getBlock().gotoNextField()
    }
    return filled
  }

  override fun enter() {
    val enterMe = getCurrentDisplay() as? DField
    if (enterMe != null) {
      getRowController().resetCommands()
      enterMe.enter(true)
    }
  }

  override fun leave() {
    val leaveMe = getCurrentDisplay() as? DField
    if (leaveMe != null) {
      getRowController().resetCommands()
      leaveMe.leave()
    }
  }

  override fun labelChanged() {
    getRowController().resetLabel()
  }

  override fun searchOperatorChanged() {
    val operator: Int = model.getSearchOperator()
    val info: String? = if (operator == VConstants.SOP_EQ) null else VConstants.OPERATOR_NAMES.get(operator)
    if (getRowController().getLabel() != null) {
      (getRowController().getLabel() as DLabel).infoText = info
    }
    if (getRowController().getDetailLabel() != null) {
      (getRowController().getDetailLabel() as DLabel).infoText = info
    }
  }

  override fun valueChanged(r: Int) {
    val dispRow: Int = getRowController().blockView.getDisplayLine(r)
    if (dispRow != -1) {
      if (getRowController().displays != null) {
        getRowController().displays[dispRow]!!.updateText()
      }
      if (getRowController().detailDisplay != null) {
        getRowController().detailDisplay!!.updateText()
      }
    }
    // fire value changed only for text fields.
    if (model.getType() == VField.MDL_FLD_TEXT || model.getType() == VField.MDL_FLD_EDITOR) {
      // store the record value in the client cache
      // so that it can be used for free navigation
      // when there is no communication needed to
      // navigate between fields.
      // we don't cache values for actor fields
      (getRowController().blockView as DBlock).fireValueChanged(getRowController().index,
                                                                r,
                                                                getRowController().model.getText(r))
    }
  }

  override fun accessChanged(row: Int) {
    if (getRowController().blockView.getDisplayLine(row) != -1) {
      getRowController().fireAccessHasChanged(row)
    }
  }

  override fun colorChanged(r: Int) {
    if (getRowController().blockView.getDisplayLine(r) != -1) {
      getRowController().fireColorHasChanged(r)
    }
    // store the color properties into the client cache
    // so that it can be used for free navigation mode.
    // we don't cache color for actor fields.
    (getRowController().blockView as DBlock).fireColorChanged(getRowController().index,
                                                              r,
                                                              Utils.toString(model.getForeground(r)),
                                                              Utils.toString(model.getBackground(r)))
  }
}
