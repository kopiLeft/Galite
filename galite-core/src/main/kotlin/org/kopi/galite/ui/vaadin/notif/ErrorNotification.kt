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
import org.kopi.galite.ui.vaadin.base.Utils.findMainWindow

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * Error type notification component.
 *
 * @param title the error notification title.
 * @param message the error notification message.
 * @param locale  the notification locale
 */
class ErrorNotification(title: String?,
                        val message: String?,
                        locale: String,
                        parent: Component?)
  : AbstractNotification(title, message, locale, parent) {

  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------
  private var details: ErrorMessageDetails? = null
  private lateinit var close: Button

  //-------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------
  init {
    Shortcuts.addShortcutListener(this, this::onArrowUpEvent, Key.ARROW_UP)
    Shortcuts.addShortcutListener(this, this::onArrowDownEvent, Key.ARROW_DOWN)
  }

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  override fun setButtons() {
    val application = parent?.findMainWindow()?.application

    close = Button(LocalizedProperties.getString(locale, "CLOSE"))
    close.addClickListener { fireOnClose(null) }
    close.isAutofocus = true
    buttons.add(close)

    if(application?.windowError != null) {
      details = ErrorMessageDetails(application.windowError!!.toString(), locale, this)
      details!!.element.setAttribute("aria-label", "Click me") // FIXME : do we need this?
      footer.add(details)
    }
  }

  override val iconName: VaadinIcon
    get() = VaadinIcon.EXCLAMATION_CIRCLE

  fun onArrowUpEvent(keyDownEvent: ShortcutEvent?) {
    close.focus()
  }

  fun onArrowDownEvent(keyDownEvent: ShortcutEvent?) {
    details?.focus()
  }
}
