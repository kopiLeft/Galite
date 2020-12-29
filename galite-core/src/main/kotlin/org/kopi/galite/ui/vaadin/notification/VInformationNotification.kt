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
import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.base.VInputButton

/**
 * Information type notification component.
 */
open class VInformationNotification(title: String, message: String) : VAbstractNotification(title, message) {

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  override fun setButtons(locale: String?) {
    close = VInputButton(LocalizedProperties.getString(locale, "CLOSE")) {
        hide()
        fireOnClose(false)
      }
    buttons!!.add(close)
  }

  override val iconName: String
    get() = "info-circle"

  fun focus() {
    if (close != null) {
      close!!.focus()
    }
  }

  fun onKeyPress(event: KeyPressEvent) {
    if (close != null && close!!.caption.toLowerCase() === event.code.toString()) {
      close!!.click()
    }
  }

  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------

  private var close: VInputButton? = null
}
