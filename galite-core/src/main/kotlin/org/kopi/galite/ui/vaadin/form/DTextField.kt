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
import org.kopi.galite.ui.vaadin.field.TextField

/**
 * The `DTextField` is the vaadin implementation
 * of the [UTextField] specifications.
 */
open class DTextField(model: VFieldUI,
                      label: DLabel?,
                      align: Int,
                      options: Int,
                      detail: Boolean) : DField(model, label, align, options, detail), UTextField {

  // --------------------------------------------------
  // CREATE FIELD UI
  // --------------------------------------------------
  /**
   * Creates the field UI component.
   * @param noEcho Password field ?
   * @param scanner Scanner field ?
   * @param align The field alignment.
   * @return The [TextField] object.
   */
  private fun createFieldGUI(noEcho: Boolean,
                             scanner: Boolean,
                             align: Int): TextField {
    TODO()
  }

  // ----------------------------------------------------------------------
  // DRAWING
  // ----------------------------------------------------------------------
  override fun updateAccess() {
    TODO("Not yet implemented")
  }

  override fun updateText() {
    TODO("Not yet implemented")
  }

  override fun updateColor() {
    TODO("Not yet implemented")
  }

  override fun updateFocus() {
    TODO("Not yet implemented")
  }

  override fun forceFocus() {
    TODO("Not yet implemented")
  }

  //---------------------------------------------------
  // TEXTFIELD IMPLEMENTATION
  //---------------------------------------------------
  override fun getText(): String? {
    TODO("Not yet implemented")
  }

  override fun setHasCriticalValue(b: Boolean) {
    // ignore
  }

  override fun addSelectionFocusListener() {
    // ignore
  }

  override fun removeSelectionFocusListener() {
    // ignore
  }

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {
    TODO("Not yet implemented")
  }

  //---------------------------------------------------
  // DFIELD IMPLEMENTATION
  //---------------------------------------------------
  override fun getObject(): Any? {
    TODO("Not yet implemented")
  }

  override fun setBlink(blink: Boolean) {
    TODO("Not yet implemented")
  }
}
