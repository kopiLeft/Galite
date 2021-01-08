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
 * Rich text editor implementation based on CK editor for vaadin.
 */
class DRichTextEditor(model: VFieldUI,
                      label: DLabel?,
                      align: Int,
                      options: Int,
                      height: Int,
                      detail: Boolean)
  : DField(model, label!!, align, options, detail), UTextField {

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateColor() {}

  override fun getObject(): Any? {
    TODO("Not yet implemented")
  }

  override fun setBlink(blink: Boolean) {
    TODO("Not yet implemented")
  }

  override fun updateText() {
    TODO("Not yet implemented")
  }

  override fun updateFocus() {
    TODO("Not yet implemented")
  }

  override fun updateAccess() {
    TODO("Not yet implemented")
  }

  override fun forceFocus() {
    TODO("Not yet implemented")
  }

  override fun getText(): String? {
    TODO("Not yet implemented")
  }

  override fun setHasCriticalValue(b: Boolean) {}

  override fun addSelectionFocusListener() {}

  override fun removeSelectionFocusListener() {}

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {}
}
