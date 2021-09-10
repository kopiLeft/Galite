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
package org.kopi.galite.ui.vaadin.notif

import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.common.VSpan

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.Scroller

/**
 * An error message popup window containing stack trace detail
 * for a server exception.
 *
 * @param message The error message.
 * @param locale  The notification locale
 * @param parent  The parent dialog containing this message details
 *
 */
class ErrorMessageDetails(message: String?, locale: String, val parent: Dialog) : Div(), HasSize {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val scroller = Scroller()
  private val details = FocusableDetails(LocalizedProperties.getString(locale, "showErrorDetails"), scroller)
  private val message = VSpan()

  init {
    createContent()
    setMessage(message)
    add(details)
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  /**
   * Creates the window content.
   */
  protected fun createContent() {
    scroller.className = "error-details"
    scroller.height = "200px"
    message.className = "details-message"
    scroller.content = message
  }

  /**
   * Sets the error message.
   * @param message The error message.
   */
  fun setMessage(message: String?) {
    this.message.setHtml(message)
  }

  fun setPixelSize(width: Int, height: Int) {
    scroller.width = width.toString()
    scroller.height = height.toString()
  }

  fun focus() {
    details.focus()
  }

  class FocusableDetails(message: String?,
                         content: Component)
    : Details(message, content), Focusable<FocusableDetails>
}
