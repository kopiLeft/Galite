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

import org.kopi.galite.form.FieldHandler
import org.kopi.galite.form.UBlock
import org.kopi.galite.form.UChartLabel
import org.kopi.galite.form.UField
import org.kopi.galite.form.ULabel
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VBooleanField
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.form.VImageField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.VTextField
import org.kopi.galite.ui.vaadin.base.Utils
import org.kopi.galite.util.base.InconsistencyException

/**
 * The `DFieldUI` is the vaadin UI components implementation of
 * the [VFieldUI] row controller.
 *
 * @param blockView The block view.
 * @param model The field model.
 */
open class DFieldUI(blockView: UBlock, model: VField, index: Int) : VFieldUI(blockView, model, index) {
  // --------------------------------------------------
  // VFIELDUI IMPLEMENTATION
  // --------------------------------------------------
  override fun createDisplay(label: ULabel?, model: VField, detail: Boolean): UField {
    return when (model.getType()) {
      VField.MDL_FLD_EDITOR -> if ((model as VTextField).isStyled) {
        DRichTextEditor(this, label as? DLabel, model.align,
                        model.options,
                        model.height, detail)
      } else {
        DTextEditor(this, label as? DLabel, model.align, model.options,
                    model.height, detail)
      }
      VField.MDL_FLD_TEXT -> if (model is VBooleanField) {
        DBooleanField(this, label as? DLabel, model.align, model.options,
                      detail)
      } else if (model is VStringField && model.isStyled) {
        DRichTextEditor(this, label as? DLabel, model.align,
                        model.options,
                        model.height, detail)
      } else {
        DTextField(this, label as? DLabel, model.align, model.options,
                   detail)
      }
      VField.MDL_FLD_IMAGE -> DImageField(this, label as? DLabel, model.align,
                                          0, (model as VImageField).iconWidth,
                                          model.iconHeight, detail)
      VField.MDL_FLD_ACTOR -> DActorField(this, label as? DLabel, model.align,
                                          model.options, detail)
      else -> throw InconsistencyException("Type of model " + model.getType().toString() + " not supported.")
    }
  }

  override fun createFieldHandler(): FieldHandler {
    return DFieldHandler(this)
  }

  override fun createLabel(text: String?, help: String?, detail: Boolean): ULabel {
    return DLabel(text, help)
  }

  override fun createChartHeaderLabel(text: String?,
                                      help: String?,
                                      index: Int,
                                      model: VBlock.OrderModel): UChartLabel {
    return DChartHeaderLabel(text, help, index, model)
  }

  override fun includeBooleanAutofillCommand(): Boolean {
    return false // boolean fields are handled differently
  }

  /**
   * If the fields values are set in the model before display creation,
   * The [org.kopi.galite.ui.vaadin.form.DFieldHandler.valueChanged] is not called since the
   * listener is not registered yet. We will call the value change event for
   * every block record here to fill out the client side cached values.
   */
  override fun fireDisplayCreated() {
    for (r in 0 until getBlock().bufferSize) {
      // fire value changed only for text fields and when text value is not empty.
      if (model.getType() == VField.MDL_FLD_TEXT || model.getType() == VField.MDL_FLD_EDITOR) {
        if (model.getText(r) != null && model.getText(r)!!.isNotEmpty()) {
          (blockView as DBlock).fireValueChanged(index, r, model.getText(r))
        }
      }
      // fire color changed for non empty colors
      if (Utils.toString(model.getForeground(r)).isNotEmpty() || Utils.toString(
                      model.getBackground(r)).isNotEmpty()) {
        (blockView as DBlock).fireColorChanged(index,
                                               r,
                                               Utils.toString(model.getForeground(r)),
                                               Utils.toString(model.getBackground(r)))
      }
    }
  }
}
