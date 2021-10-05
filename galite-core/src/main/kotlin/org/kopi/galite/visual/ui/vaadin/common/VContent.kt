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
package org.kopi.galite.visual.ui.vaadin.common

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div

/**
 * The main window content component.
 */
class VContent : Div() {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the content of this main container.
   * @param content The main container content.
   */
  fun setContent(content: Component?) {
    removeAll()
    add(content)
    isEmpty = false
  }

  /**
   * Clears the content.
   */
  fun clearContent() {
    removeAll()
    isEmpty = true
  }

  /**
   * Checks for view content.
   * @return `true` is content exists.
   */
  var isEmpty = true
    private set

  init {
    setId("content")
  }
}
