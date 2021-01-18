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
package org.kopi.galite.ui.vaadin.notification

import com.vaadin.flow.component.KeyPressEvent
import org.kopi.galite.ui.vaadin.base.VInputButton

/**
 * Confirm type notification component.
 */
open class VConfirmNotification(title: String, message: String) : VAbstractNotification(title, message) {
  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------
  override fun setButtons(locale: String?) {

  }

  fun focus() {

  }

  override val iconName: String?
    get() = "question-circle"

  override fun showGlassPane(): Boolean {
    return true
  }

  override fun goBackToLastFocusedWindow(): Boolean {
    return true
  }

  fun onKeyPress(event: KeyPressEvent) {

  }

  //------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------
  private var ok: VInputButton? = null
  private var cancel: VInputButton? = null
  private var okFocused = false
  private var cancelFocused = false
}
