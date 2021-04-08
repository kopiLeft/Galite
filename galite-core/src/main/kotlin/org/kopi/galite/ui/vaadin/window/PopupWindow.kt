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
package org.kopi.galite.ui.vaadin.window

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.common.VSpan

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.HasStyle

/**
 * The popup window component.
 */
class PopupWindow : EnhancedDialog(), HasStyle {

  private val listeners = mutableListOf<CloseListener>()
  private var caption = VSpan()

  init {
    className = Styles.POPUP_WINDOW
    caption.className = Styles.POPUP_WINDOW_CAPTION
    isDraggable = true
    isResizable = true
    addToHeader(caption)
  }
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Registers a close listener.
   * @param l The listener to be registered.
   */
  fun addCloseListener(l: CloseListener) {
    listeners.add(l)
  }

  /**
   * Removes a close listener.
   * @param l The listener to be removed.
   */
  fun removeCloseListener(l: CloseListener) {
    listeners.remove(l)
  }

  /**
   * Fires a close event.
   */
  fun fireOnClose() {
    for (l in listeners) {
      l.onClose()
    }
  }

  fun setCaption(title: String) {
    caption.text = title
  }
}
