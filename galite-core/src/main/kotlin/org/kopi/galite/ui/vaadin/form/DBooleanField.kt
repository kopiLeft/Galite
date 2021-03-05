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

import org.kopi.galite.form.UTextField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.field.BooleanField
import org.kopi.galite.ui.vaadin.field.ObjectField

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.customfield.CustomField

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
) : DObjectField(model, label, align, options, detail),
        UTextField,
        HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<CustomField<Boolean>, Boolean>> {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private val field: BooleanField
  private var inside = false

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  init {
    field = BooleanField(trueRepresentation, falseRepresentation)
    field.addValueChangeListener(this)
    //setContent(field) TODO
  }
  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  override fun blinkOnFocus(): Boolean {
    return false
  }

  override fun updateColor() {
    // NOT SUPPORTED
  }

  override fun updateText() {
    //BackgroundThreadHandler.access(Runnable { TODO
      field.value = getModel().getBoolean(getBlockView().getRecordFromDisplayLine(position))
    //})
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


  override fun valueChanged(event: AbstractField.ComponentValueChangeEvent<CustomField<Boolean>, Boolean>?) {
    // ensures to get model focus to validate the field
    if (!getModel().hasFocus()) {
      getModel().block!!.activeField = getModel()
    }
    val text = getModel().toText(event!!.value)

    if (getModel().checkText(text!!)) {
      getModel().changedUI = true
      getModel().setBoolean(getBlockView().getRecordFromDisplayLine(position), event.value)
    }
    getModel().setChanged(true)
  }

  override fun updateAccess() {
    super.updateAccess()
    label!!.update(model, getBlockView().getRecordFromDisplayLine(position))
    //BackgroundThreadHandler.access(Runnable { TODO
      field.label = label!!.text
      field.isEnabled = getAccess() >= VConstants.ACS_VISIT
      field.mandatory = getAccess() == VConstants.ACS_MUSTFILL
    //})
  }

  override fun getObject(): Any? = value

  override fun setBlink(b: Boolean) {
    //BackgroundThreadHandler.access(Runnable { TODO
      field.setBlink(b)
    //})
  }

  override fun getText(): String? = getModel().toText(field.value)

  override fun setHasCriticalValue(b: Boolean) {}

  override fun addSelectionFocusListener() {}

  override fun removeSelectionFocusListener() {}

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {}

  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  protected val trueRepresentation: String?
    get() = getModel().toText(java.lang.Boolean.TRUE)

  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  protected val falseRepresentation: String?
    get() = getModel().toText(java.lang.Boolean.FALSE)

  /**
   * Gets the focus to this field.
   */
  protected fun enterMe() {
    //BackgroundThreadHandler.access(Runnable {  TODO
    field.setFocus(true)
    //})
  }
}
