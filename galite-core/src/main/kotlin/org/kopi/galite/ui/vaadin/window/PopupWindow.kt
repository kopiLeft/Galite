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
import org.kopi.galite.ui.vaadin.main.MainWindow

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.dependency.CssImport

/**
 * The popup window component.
 */

@CssImport("./styles/galite/dialog.css")
class PopupWindow : EnhancedDialog(), HasStyle {

  private val listeners = mutableListOf<CloseListener>()
  private var caption = VSpan()

  init {
    className = Styles.POPUP_WINDOW
    caption.className = Styles.POPUP_WINDOW_CAPTION
    isDraggable = true
    isResizable = true
    isCloseOnOutsideClick = false
    addToHeader(caption)
  }
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  fun setCaption(title: String) {
    caption.text = title
  }

  override fun close() {
    super.close()

    // try to get it from the main window current shown window.
    val lastActiveWindow = MainWindow.instance.currentWindow as? Window

    if (lastActiveWindow != null) {
      // focus the window itself to activate attached actors.
      lastActiveWindow.focus()
      if (lastActiveWindow.hasLastFocusedTextField()) {
        // focus last text focused text box if available
        lastActiveWindow.goBackToLastFocusedTextField()
      }
    }
  }
}
