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

import org.kopi.galite.ui.vaadin.base.Styles

import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * A Wait panel component.
 */
class WaitWindow : VerticalLayout(), ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>> {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var image = Icon(VaadinIcon.SPINNER)
  private var text = Span()
  private var popup = Dialog()

  init {
    image.className = Styles.WAIT_WINDOW_IMAGE
    text.className = Styles.WAIT_WINDOW_TEXT
    add(image)
    add(text)
    justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    defaultHorizontalComponentAlignment = FlexComponent.Alignment.START
    isSpacing = false
    popup.addOpenedChangeListener(this)
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

  /**
   * Shows the wait window.
   * @param parent the parent widget.
   */
  fun show() {
    popup.open()
  }

  override fun onComponentEvent(event: GeneratedVaadinDialog.OpenedChangeEvent<Dialog>) {
    if(event.isOpened) {
      popup.element.style["cursor"] = "wait"
    } else {
      popup.element.style["cursor"] = "default"
    }
  }
}
