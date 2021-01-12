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

import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * An abstract implementation of notification components such as
 * warnings, errors, confirms and information.
 */
abstract class VAbstractNotification : Dialog(), Focusable<VAbstractNotification> {

  /**
   * Creates a new notification widget with a window containing
   * a title, a message, an image and buttons location.
   */
  fun init(locale: String) {
    setButtons(locale)
    element.setAttribute("hideFocus", "true")
    element.style["outline"] = "0px"
    this.width = "30%"
    this.height = "25%"
    createHeader()
    createContent()
    createFooter()
  }

  abstract fun onEnterEvent(keyDownEvent: ShortcutEvent?)
  abstract fun onRightEvent(keyDownEvent: ShortcutEvent?)
  abstract fun onLeftEvent(keyDownEvent: ShortcutEvent?)

  /**
   * Initializes the notification panel.
   */
  fun initialize(title: String, message: String, locale: String) {
    init(locale)
    setNotificationTitle(title)
    setNotificationMessage(message)
  }

  /**
   * Closes the notification panel.
   */
  override fun close() {
    hide()
  }

  /**
   * Hides the notification dialog.
   */
  protected open fun hide() {
    super.close()
  }

  /**
   * Fires a close event.
   * @param action The user action.
   */
  protected open fun fireOnClose(action: Boolean) {
    for (l in listeners) {
      l.onClose(action)
    }
  }

  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------

  /**
   * Sets the notification title.
   * @param title The notification title.
   */
  fun setNotificationTitle(title: String) {
    this.title.text = title
  }

  /**
   * Sets the notification message.
   * @param message The notification message.
   */
  fun setNotificationMessage(message: String) {
    this.message.text = message
  }

  /**
   * Creates the notification header.
   */
  fun createHeader() {
    val close = Button()
    close.icon = VaadinIcon.CLOSE.create()
    close.addClickListener { close() }
    header.add(title, close)
    header.setFlexGrow(1.0, title)
    header.isPadding = true
    header.alignItems = FlexComponent.Alignment.CENTER
    header.style.set("background-color", "DarkSeaGreen")
    header.width = "99%"
    header.height = "25%"
    add(header)
  }

  /**
   * Creates the notification content.
   */
  fun createContent() {
    icon = Icon(iconName)
    content.isPadding = true
    content.style.set("background-color", "AliceBlue")
    content.add(icon, message)
    content.alignItems = FlexComponent.Alignment.CENTER
    content.width = "99%"
    content.height = "35%"
    add(content)
  }

  /**
   * Creates the notification footer.
   */
  open fun createFooter() {
    footer.justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    footer.width = "99%"
    add(footer)
  }

  /**
   * Registers a new notification listener.
   * @param l The listener to be added.
   */
  open fun addNotificationListener(l: NotificationListener) {
    listeners.add(l)
  }

  /**
   * Removes a new notification listener.
   * @param l The listener to be removed.
   */
  open fun removeNotificationListener(l: NotificationListener) {
    listeners.remove(l)
  }

  //-------------------------------------------------
  // ABSTRACT METHODS
  //-------------------------------------------------

  /**
   * Sets the notification buttons.
   * @param locale The notification locale.
   */
  abstract fun setButtons(locale: String)

  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------

  /**
   * Represents the icon name to be used with this notification.
   */
  protected abstract val iconName: String?
  open var title = Label()
  open var message = Label()
  open var icon: Icon? = null
  var locale: String = "fr_FR"
  private val listeners = mutableListOf<NotificationListener>()
  protected val yesIsDefault = true
  val header = HorizontalLayout()
  val content = HorizontalLayout()
  val footer = HorizontalLayout()
}
