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
package org.kopi.galite.visual.ui.vaadin.event

import java.io.Serializable

import com.vaadin.flow.component.Component

/**
 * Registered objects are notified about actions happening
 * in the main application window.
 */
interface MainWindowListener : Serializable {
  /**
   * Fired when the logout link is clicked.
   */
  fun onLogout()

  /**
   * Fired when the connected user link is clicked.
   */
  fun onUser()

  /**
   * Fired when windows link is clicked.
   */
  fun onWindows()

  /**
   * Fired when the given window is shown on the main window.
   * @param window The window the became visible.
   */
  fun onWindowVisible(window: Component?)
}
