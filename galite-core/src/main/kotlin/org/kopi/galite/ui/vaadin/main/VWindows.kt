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

import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.shared.Registration

/**
 * Component that contains the opened windows
 * in the current application session.
 * By clicking on the link, a popup will be shown that contains
 * the opened windows and then the user can switch between them.
 */
class VWindows : Div(), HasEnabled, Focusable<VWindows> {

  private val anchor = Anchor()
  private val label = Span()
  private val icon = IronIcons.CONTENT_COPY.create()
  private var enabled = false

  init {
    setId("windows")
    label.isVisible = false
    add(anchor)
    anchor.add(label)
    anchor.add(icon)
    hideLabel()
  }

  /**
   * Sets the link text.
   * @param text The link text.
   */
  override fun setText(text: String) {
    label.text = text
  }

  /**
   * Shows the link localized label.
   */
  fun showLabel() {
    label.isVisible = true
  }

  /**
   * Hides the link localized label.
   */
  fun hideLabel() {
    label.isVisible = false
  }

  /**
   * Registers a click handler to the welcome text.
   * @param handler The handler to be registered.
   * @return The registration .
   */
  fun addClickHandler(handler: (Any) -> Unit): Registration {
    return anchor.addAttachListener(handler)
  }

  override fun setEnabled(enabled: Boolean) {
    this.enabled = enabled
    if (enabled) {
      setClassName("empty", true)
    } else {
      setClassName("empty", false)
    }
  }
}
