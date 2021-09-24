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
package org.kopi.galite.ui.vaadin.menu

import org.kopi.galite.ui.vaadin.base.Utils.findMainWindow
import org.kopi.galite.ui.vaadin.window.Window

import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.dialog.Dialog

class VNavigationMenu(private val rootNavigationItem: VActorsRootNavigationItem) : Dialog(), HasStyle {

  init {
    //this.setThemeVariants(EnhancedDialogVariant.SIZE_MEDIUM)
    isResizable = false
    isDraggable = true
    isCloseOnEsc = true
  }
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  /**
   * Sets the the navigation panel associated with this navigation menu.
   * @param panel The navigation panel.
   */
  fun setNavigationPanel(panel: VNavigationPanel) {
    add(panel)
  }

  override fun close() {
    val lastActiveWindow = rootNavigationItem.findMainWindow()?.currentWindow as? Window

    super.close()
    lastActiveWindow?.goBackToLastFocusedTextField()
  }
}
