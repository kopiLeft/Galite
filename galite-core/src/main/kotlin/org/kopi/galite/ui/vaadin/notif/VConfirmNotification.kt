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

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.base.VInputButton

/**
 * Confirm type notification component.
 */
open class VConfirmNotification(title: String, message: String) : VAbstractNotification() {

//-------------------------------------------------
// IMPLEMENTATION
//-------------------------------------------------

  override fun setButtons(locale: String) {
    element.setAttribute("hideFocus", "true")
    ok = VInputButton(LocalizedProperties.getString(locale, "OK"))
    cancel = VInputButton(LocalizedProperties.getString(locale, "NO"))
    cancel!!.addClickListener { hide() }
    ok!!.addClickListener { open() }
    ok!!.width = "20%"
    ok!!.height = "50%"
    cancel!!.width = "20%"
    cancel!!.height = "50%"
  }

  override fun focus() {
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

  /**
   * Creates the confirmation notification footer.
   */
  override fun createFooter() {
    element.setAttribute("hideFocus", "true")
    footer.add(ok)
    footer.add(cancel)
    footer.isSpacing = true
    footer.justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    footer.style.set("background-color", "AliceBlue")
    footer.width = "99%"
    footer.height = "35%"
    add(footer)
  }

  override fun onEnterEvent(keyDownEvent: ShortcutEvent?) {
    cancel!!.click()
    cancelFocused = true
    okFocused = false
  }

  override fun onRightEvent(keyDownEvent: ShortcutEvent?) {
    cancel!!.focus()
    okFocused = false
    cancelFocused = true

  }

  override fun onLeftEvent(keyDownEvent: ShortcutEvent?) {
    ok!!.focus()
    okFocused = true
    cancelFocused = false
  }


  //------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------

  override val iconName: String?
    get() = "question-circle"
  var ok: VInputButton? = null
  private var cancel: VInputButton? = null
  var listener: ComponentEventListener<ClickEvent<Button>>? = null
  var okFocused = false
  var cancelFocused = false

  //--------------------------------------------------
  // CONSTRUCTOR
  //--------------------------------------------------

  /**
   * Creates the confirmation widget.
   */
  init {
    super.title = Label(title)
    super.message = Label(message)
    super.initialize(title, message, locale)
  }
}
