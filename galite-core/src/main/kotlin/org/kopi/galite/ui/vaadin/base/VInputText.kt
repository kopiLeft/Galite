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
package org.kopi.galite.ui.vaadin.base

import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.html.Input

/**
 * An input text component.
 */
open class VInputText() : Input(), KeyNotifier {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates the input text component.
   */
  init {
    type = "text"
  }

  /**
   * For children classes.
   * @param input The input text.
   */
  protected constructor(input: String?) : this() {
    value = input
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the input element size.
   * @param size The element size.
   */
  fun setSize(size: Int) {
    element.setAttribute("size", size.toString() + "")
  }

  /**
   * Sets the element name.
   * @param name The element name.
   */
  fun setName(name: String?) {
    element.setAttribute("name", name)
  }
}
