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
import org.kopi.galite.form.VFieldUI

/**
 * Boolean field.
 *
 * @param model The field row controller.
 * @param label The field label.
 * @param align The field alignment (not used)
 * @param options The field options.
 * @param detail is it a detail field view ?
 */
class DBooleanField(model: VFieldUI,
                    label: DLabel?,
                    align: Int,
                    options: Int,
                    detail: Boolean) : DObjectField(model, label, align, options,
                                                    detail), UTextField {
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
    TODO()
  }

  override fun updateFocus() {
    TODO()
  }

  override fun updateAccess() {
    TODO()
  }

  override fun getObject(): Any? = getText()

  override fun setBlink(b: Boolean) {
    TODO()
  }

  override fun getText(): String? {
    TODO()
  }

  override fun setHasCriticalValue(b: Boolean) {}
  override fun addSelectionFocusListener() {}
  override fun removeSelectionFocusListener() {}
  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {}

  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  protected val trueRepresentation: String?
    protected get() = getModel().toText(java.lang.Boolean.TRUE)

  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  protected val falseRepresentation: String?
    protected get() = getModel().toText(java.lang.Boolean.FALSE)

  /**
   * Gets the focus to this field.
   */
  protected fun enterMe() {
    TODO()
  }
}
