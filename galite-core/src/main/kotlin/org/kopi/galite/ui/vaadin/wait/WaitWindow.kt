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
package org.kopi.galite.ui.vaadin.wait

import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.kopi.galite.ui.vaadin.base.Styles

/**
 * A Wait panel component.
 */
class WaitWindow : VerticalLayout() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var image = Icon(VaadinIcon.SPINNER)
  private var text = Span()
  private var popup = Dialog()

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  /**
   * Initializes the widget.
   * @param connection The application connection.
   */
  init {
    text.className = Styles.WAIT_WINDOW_TEXT
    add(image)
    add(text)
    justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    defaultHorizontalComponentAlignment = FlexComponent.Alignment.START
    isSpacing = false
  }

  /**
   * Sets the wait panel text.
   * @param text The wait text.
   */
  fun setText(text: String?) {
    if (text != null) {
      this.text.text = text
    }
  }
}