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

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.HasValue

import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.field.BooleanField

/**
 * Boolean field.
 *
 * @param model The field row controller.
 * @param label The field label.
 * @param align The field alignment (not used)
 * @param options The field options.
 * @param detail is it a detail field view ?
 */
class DBooleanField(
  model: VFieldUI,
  label: DLabel?,
  align: Int,
  options: Int,
  detail: Boolean
) : DField(model, label, align, options, detail),
    HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<org.kopi.galite.visual.ui.vaadin.field.AbstractField<Boolean?>, Boolean?>> {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private val field: BooleanField = BooleanField(trueRepresentation, falseRepresentation)
  private var inside = false

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  init {
    field.addValueChangeListener(this)
    field.addFocusListener {}
    field.addBlurListener { gotoNextField() }
    setFieldContent(field)
  }

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------

  override fun valueChanged() {
    // Nothing to do
  }

  override fun updateColor() {
    // NOT SUPPORTED
  }

  override fun updateText() {
    access(currentUI) {
      field.value = getModel().getBoolean(getBlockView().getRecordFromDisplayLine(position))
    }
    super.updateText()
  }

  override fun updateFocus() {
    label!!.update(model, position)
    if (!modelHasFocus()) {
      if (inside) {
        inside = false
      }
    } else {
      if (!inside) {
        inside = true
        enterMe()
      }
    }
    super.updateFocus()
  }

  override fun valueChanged(event: AbstractField.ComponentValueChangeEvent<org.kopi.galite.visual.ui.vaadin.field.AbstractField<Boolean?>, Boolean?>) {
    val text = getModel().toText(event.value)

    if (getModel().checkText(text!!)) {
      getModel().isChangedUI = true
      getModel().setBoolean(getBlockView().getRecordFromDisplayLine(position), event.value)
    }
    getModel().setChanged(true)
  }

  override fun updateAccess() {
    super.updateAccess()
    label!!.update(model, getBlockView().getRecordFromDisplayLine(position))
    access(currentUI) {
      field.isEnabled = getAccess() >= VConstants.ACS_VISIT
      field.mandatory = getAccess() == VConstants.ACS_MUSTFILL
    }
  }

  override fun getObject(): Any? = wrappedField.value

  override fun setBlink(blink: Boolean) {
    access(currentUI) {
      field.setBlink(blink)
    }
  }

  override fun getText(): String? = getModel().toText(field.value)

  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  private val trueRepresentation: String?
    get() = getModel().toText(true)

  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  private val falseRepresentation: String?
    get() = getModel().toText(false)

  /**
   * Gets the focus to this field.
   */
  private fun enterMe() {
    access(currentUI) {
      field.setFocus(true)
    }
  }
}
