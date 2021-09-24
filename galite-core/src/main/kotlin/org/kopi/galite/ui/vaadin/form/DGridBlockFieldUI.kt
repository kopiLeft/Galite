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

import org.kopi.galite.form.FieldHandler
import org.kopi.galite.form.UBlock
import org.kopi.galite.form.UChartLabel
import org.kopi.galite.form.UField
import org.kopi.galite.form.ULabel
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VBooleanField
import org.kopi.galite.form.VField
import org.kopi.galite.form.VImageField
import org.kopi.galite.ui.vaadin.grid.GridEditorField

/**
 * A row controller for the grid block implementation
 */
class DGridBlockFieldUI(blockView: UBlock, model: VField, index: Int) : DFieldUI(blockView, model, index) {

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  override fun createDisplay(label: ULabel?, model: VField, detail: Boolean): UField {
    return if (detail) {
      super.createDisplay(label, model, detail)
    } else {
      var field: DGridEditorField<*>
      when (model.getType()) {
        VField.MDL_FLD_EDITOR, VField.MDL_FLD_TEXT -> if (model is VBooleanField) {
          field = DGridEditorBooleanField(this, label as? DGridEditorLabel, model.align, model.options)
        } else {
          field = DGridTextEditorField(this, label as? DGridEditorLabel, model.align, model.options)
        }
        VField.MDL_FLD_IMAGE -> field = DGridEditorImageField(this,
                                                              label as? DGridEditorLabel,
                                                              model.align,
                                                              (model as VImageField).iconWidth,
                                                              model.iconHeight,
                                                              model.options)
        VField.MDL_FLD_ACTOR -> field = DGridEditorActorField(this,
                                                              label as? DGridEditorLabel,
                                                              model.align,
                                                              model.options)
        else -> return super.createDisplay(label, model, detail)
      }
      field
    }
  }

  override fun createLabel(text: String?, help: String?, detail: Boolean): ULabel {
    return if (detail) {
      super.createLabel(text, help, detail)
    } else {
      DGridEditorLabel(text, help)
    }
  }

  override fun createChartHeaderLabel(text: String?,
                                      help: String?,
                                      index: Int,
                                      model: VBlock.OrderModel): UChartLabel {
    return DGridEditorLabel(text, help)
  }

  override fun createFieldHandler(): FieldHandler = DGridBlockFieldHandler(this)

  override fun fireDisplayCreated() {
    // no client side cache
  }

  override fun getDisplaySize(): Int = 1

  override fun scrollTo(toprec: Int) {
    if (model.hasFocus()) {
      fieldHandler.enter()
    }
  }

  override fun gotoActiveRecord() {
    blockView.editRecord(getBlock().activeRecord)
  }

  /**
   * Returns the grid editor display of this row controller.
   * @return The grid editor display of this row controller.
   */
  val editorField: DGridEditorField<*>
    get() = displays[0] as DGridEditorField<*>

  /**
   * Returns the grid editor field associated with this column view.
   * @return The grid editor field associated with this column view.
   */
  val editor: GridEditorField<*>
    get() = editorField.editor

  /**
   * Returns true if the column view has a chart display for this field model.
   * @return True if the column view has a chart display for this field model.
   */
  fun hasDisplays(): Boolean = displays != null // TODO: display is nullable or not?

  override val blockView: DGridBlock
    get() = super.blockView as DGridBlock
}
