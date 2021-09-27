/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.notif

import org.kopi.galite.visual.ui.vaadin.base.LocalizedProperties

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * Confirm type notification component.
 *
 * @param title the confirm notification title.
 * @param message the confirm notification message.
 * @param locale  the notification locale
 */
class ConfirmNotification(title: String?,
                          message: String,
                          locale: String,
                          parent: Component?)
  : AbstractNotification(title, message, locale, parent) {

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  override fun setButtons() {
    ok = Button(LocalizedProperties.getString(locale, "OK"))
    cancel = Button(LocalizedProperties.getString(locale, "NO"))
    ok.addClickListener { fireOnClose(true) }
    cancel.addClickListener { fireOnClose(false) }
    buttons.add(ok)
    buttons.add(cancel)

    if (yesIsDefault) {
      ok.isAutofocus = true
    } else {
      cancel.isAutofocus = true
    }
  }

  override val iconName: VaadinIcon
    get() = VaadinIcon.QUESTION_CIRCLE

  fun onArrowRightEvent(keyDownEvent: ShortcutEvent?) {
    cancel.focus()
  }

  fun onArrowLeftEvent(keyDownEvent: ShortcutEvent?) {
    ok.focus()
  }

  //------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------
  private lateinit var ok: Button
  private lateinit var cancel: Button
  private val listener: ComponentEventListener<ClickEvent<Button>>? = null

  init {
    Shortcuts.addShortcutListener(this, this::onArrowRightEvent, Key.ARROW_RIGHT)
    Shortcuts.addShortcutListener(this, this::onArrowLeftEvent, Key.ARROW_LEFT)
  }
}
