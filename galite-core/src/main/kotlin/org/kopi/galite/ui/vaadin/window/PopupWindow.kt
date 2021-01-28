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

import org.kopi.galite.ui.vaadin.visual.DWindow

import com.vaadin.flow.component.html.Div

/**
 * The popup window component.
 */
class PopupWindow : Div() {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the modality of this window.
   * @param modal Is it a modal window.
   */
  fun setModal(modal: Boolean) {
    this.modal = modal
  }

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

  fun setContent(view: DWindow) {
    // TODO
  }

  fun setCaption(title: String) {
    // TODO
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val listeners = mutableListOf<CloseListener>()
  private var modal = false
}
