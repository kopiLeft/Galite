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

import javax.swing.Popup

import org.kopi.galite.ui.vaadin.common.VSpan

import com.vaadin.flow.component.orderedlayout.Scroller

/**
 * An error message popup window containing stack trace detail
 * for an server exception.
 */
class VErrorMessagePopup : Popup() {

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  /**
   * Creates the window content.
   */
  protected fun createContent() {
    scroller.className = "error-details"
    message = VSpan()
    message!!.className = "details-message"
    scroller.content = message
  }

  /**
   * Sets the error message.
   * @param message The error message.
   */
  fun setMessage(message: String?) {
    this.message!!.setHtml(message)
  }

  fun setPixelSize(width: Int, height: Int) {
    scroller.width = width.toString()
    scroller.height = height.toString()
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var message: VSpan? = null
  private val scroller = Scroller()

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  init {
    createContent()
  }
}
