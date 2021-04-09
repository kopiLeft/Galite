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
package org.kopi.galite.ui.vaadin.main

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div

/**
 * The opened windows display component.
 * This component will be the responsible of displaying
 * the open windows menu.
 */
class VWindowsDisplay : Div() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private val menu = VWindowsMenu()

  init {
    className = "k-windows-menu-container"
  }

  /**
   * Updates the menu item caption for the given window.
   * @param window The concerned window.
   * @param caption The new item caption.
   */
  fun updateCaption(window: Component, caption: String) {
    TODO()
  }

  /**
   * Adds a window to the [VWindowsMenu].
   * @param container The container of the window.
   * @param window The window to be added.
   * @param title The window title.
   */
  fun addWindow(container : VWindowContainer, window: Component, title : String) {
    menu.addWindow(container, window, title)
  }

  /**
   * Shows the windows menu.
   */
  fun showMenu() {
    menu.open()
  }

  /**
   * Sets the dialog title.
   * @param text The title text.
   */
  override fun setText(text: String?) {
    menu.setHeaderText(text)
  }
}
