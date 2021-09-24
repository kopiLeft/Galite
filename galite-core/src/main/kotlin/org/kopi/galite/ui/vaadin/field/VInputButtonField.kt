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
package org.kopi.galite.ui.vaadin.field

import com.vaadin.flow.component.BlurNotifier
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.FocusNotifier
import com.vaadin.flow.component.textfield.TextField

/**
 * A field that wraps an input button as an element.
 */
class VInputButtonField(size: Int) : InputTextField<TextField>(TextField()),
  ComponentEventListener<FocusNotifier.FocusEvent<TextField>> {

  init {
    setInputType("button")
    internalField.addFocusListener(this)
    addStyleDependentName("action")
    setWidth(size * CHAR_WIDTH, com.vaadin.flow.component.Unit.PIXELS)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun onFocus(event: FocusNotifier.FocusEvent<InputTextField<TextField>>) {}

  override fun onBlur(event: BlurNotifier.BlurEvent<InputTextField<TextField>>) {}

  override fun setFocus(focused: Boolean) {}

  override fun onComponentEvent(event: FocusNotifier.FocusEvent<TextField>?) {
    connector.fieldParent.actionPerformed()
  }

  override fun hasAutoComplete(): Boolean {
    return false
  }

  override fun setEnabled(enabled: Boolean) {}

  override fun onLoad() {}

  companion object {
    /**
     * TODO use font metric to detected the
     */
    private const val CHAR_WIDTH = 9.833f
  }
}
