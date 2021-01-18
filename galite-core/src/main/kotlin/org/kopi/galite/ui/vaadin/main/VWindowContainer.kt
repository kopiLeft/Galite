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

import org.kopi.galite.ui.vaadin.common.VCaption

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div

/**
 * The main window container component.
 * This component will be responsible of displaying only one window.
 * The control of the displayed component will be from outside.
 */
internal class VWindowContainer : Div() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val widgetToIndexMap = mutableMapOf<Component, Int>()
  private val widgetToCaptionMap = mutableMapOf<Component, String>()
  private val pane = Div()
  private val caption: VCaption
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  init {
    val captionWrapper = Div()
    className = "k-window-container"
    pane.className = "k-container-pane"
    caption = VCaption(false)
    caption.className = "k-window-caption"
    captionWrapper.setId("session")
    captionWrapper.add(caption)
    add(captionWrapper)
    add(pane)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds the specified window to this container.
   * @param window The window widget.
   * @param title the window caption.
   */
  fun addWindow(window: Component, title: String) {
    // first add the window to the container
    pane.add(window)
    // now map the window widget to its index
    widgetToIndexMap[window] = pane.element.childCount - 1
    widgetToCaptionMap[window] = title
  }

  /**
   * Removes the given window from the container.
   * @param window The window to be removed.
   * @return The new shown widget or `null` if no window is shown.
   */
  fun removeWindow(window: Component): Component? {
    // look for internal map.
    caption.setCaption("") // reset window caption
    widgetToCaptionMap.remove(window)
    val index = widgetToIndexMap.remove(window)
    if (index != null) {
      TODO()
    } else {
      TODO()
    }
    return null
  }

  /**
   * Updates the given window title.
   * @param window The concerned window.
   * @param title The new title.
   */
  fun updateWindowTitle(window: Component, title: String) {
    widgetToCaptionMap[window] = title
    TODO()
  }

  /**
   * Shows the given window in this container.
   * @param window The window to be shown.
   * @return The new shown widget or `null` if no window is shown.
   */
  fun showWindow(window: Component?): Component? {
    if (window == null) {
      // no window, give up
      return null
    }

    // look for internal map.
    var index = widgetToIndexMap.remove(window)
    TODO()
    return null
  }

  /**
   * Shows the next window in the list.
   */
  fun showNextWindow(): Component? {
    TODO()
  }

  /**
   * Shows the previous window in the list.
   */
  fun showPreviousWindow(): Component? {
    TODO()
  }
}
