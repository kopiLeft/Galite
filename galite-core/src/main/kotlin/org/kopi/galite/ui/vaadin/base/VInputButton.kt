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
package org.kopi.galite.ui.vaadin.base

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.html.Input

/**
 * An input element type button that cannot handle icons.
 */
class VInputButton(caption: String? = null) : Input(), ClickNotifier<VInputButton> {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------

  /**
   * Creates a new input button.
   */
  init {
    type = "button"
    if (caption != null) {
      value = caption
    }
    className = Styles.INPUT_BUTTON
    element.setAttribute("hideFocus", "true")
    element.style["outline"] = "0px"
  }

  /**
   * Creates a new button component.
   *
   * @param caption The button caption.
   * @param clickListener The click listener.
   */
  constructor(caption: String?, clickListener: ComponentEventListener<ClickEvent<VInputButton?>?>?) : this(caption) {
    addClickListener(clickListener)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Fires a click for this button.
   */
  fun click() {
    fireEvent(ClickEvent<VInputButton>(this))
  }

  /**
   * Sets the button caption
   *
   * @param caption The button caption.
   */
  var caption: String?
    get() = value
    set(caption) {
      value = caption
    }

  /**
   * sets the name of the component.
   *
   * @return The name.
   */
  fun setName(name: String?) {
    element.setAttribute("name", name)
  }
}
