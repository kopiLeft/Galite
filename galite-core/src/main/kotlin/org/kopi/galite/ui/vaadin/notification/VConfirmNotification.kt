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
 * Confirm type notification component.
 */
open class VConfirmNotification(title: String, message: String) : VAbstractNotification(title, message) {

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  override fun setButtons(locale: String?) {
    ok = VInputButton(LocalizedProperties.getString(locale, "OK"))
    ok!!.addClickListener { show() }
    buttons!!.add(ok!!)
    cancel = VInputButton(LocalizedProperties.getString(locale, "NO"))
    cancel!!.addClickListener { close() }
    buttons!!.add(cancel!!)
  }

  fun focus() {
    if (yesIsDefault && ok != null) {
      ok!!.focus()
      okFocused = true
      cancelFocused = false
    } else if (cancel != null) {
      cancel!!.focus()
      okFocused = false
      cancelFocused = true
    }
  }
  override fun showGlassPane(): Boolean {
    return true
  }

  override fun goBackToLastFocusedWindow(): Boolean {
    return true
  }

  fun onKeyPress(event: KeyPressEvent) {
    if (cancel != null && event.code.toString() === cancel!!.caption.toLowerCase()) {
      cancel!!.click()
    } else if (ok != null && event.code.toString() === ok!!.caption.toLowerCase()) {
      ok!!.click()
    }
  }

  //------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------

  private var ok: VInputButton? = null
  private var cancel: VInputButton? = null
  private var okFocused = false
  private var cancelFocused = false
  override val iconName: String?
    get() = "question-circle"

}
