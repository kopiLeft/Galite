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

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Paragraph

/**
 * An abstract implementation of notification components such as
 * warnings, errors, confirms and informations.
 */
// TODO : implement this class with appropriate visual component
abstract class VAbstractNotification(title: String?, message: String?) : Component() {

  /**
   * Localization string.
   */
  var locale: String? = null

  /**
   * Initializes the notification panel.
   * @param connection  The application connection.
   */
  fun init() {

  }

  /**
   * Shows the notification popup.
   */
  fun show(locale: String?) {

  }

  /**
   * Shows the notification popup.
   */
  fun show(parent: HasComponents, locale: String?) {

  }

  /**
   * Closes the notification panel.
   */
  fun close() {

  }

  /**
   * Hides the notification dialog.
   */
  protected fun hide() {

  }

  /**
   * Registers a new notification listener.
   * @param l The listener to be added.
   */
  fun addNotificationListener(l: NotificationListener) {

  }

  /**
   * Removes a new notification listener.
   * @param l The listener to be removed.
   */
  fun removeNotificationListener(l: NotificationListener) {

  }

  /**
   * Fires a close event.
   * @param action The user action.
   */
  protected fun fireOnClose(action: Boolean) {

  }

  fun onClose() {

  }
  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------
  /**
   * Sets the notification title.
   * @param title The notification title.
   */
  fun setNotificationTitle(title: String?) {

  }

  /**
   * Sets the notification message.
   * @param text The notification message.
   */
  fun setNotificationMessage(text: String?) {

  }

  /**
   * Shows an optional glass pane.
   * @return `true` if a glass pane should be shown
   */
  protected open fun showGlassPane(): Boolean {
    return false
  }

  /**
   * Should we go back to the last focused field when the notification is closed ?
   * @return `true` if we should go back to the last focused field when the notification is closed.
   */
  protected open fun goBackToLastFocusedWindow(): Boolean {
    return true
  }

  /**
   * Sets yes is a default answer.
   * @param yesIsDefault Yes is the default answer.
   */
  fun setYesIsDefault(yesIsDefault: Boolean) {

  }

  fun clear() {

  }
  //-------------------------------------------------
  // ABSTRACT METHODS
  //-------------------------------------------------
  /**
   * Sets the notification buttons.
   * @param locale The notification locale.
   */
  abstract fun setButtons(locale: String?)

  /**
   * Returns the icon name to be used with this notification.
   * @return The icon name to be used with this notification.
   */
  protected abstract val iconName: String?
  //-------------------------------------------------
  // INNER CLASSES
  //-------------------------------------------------
  /**
   * A simple component that wraps a h3 HTML tag.
   */
  private class VH3 : H3() {
    /**
     * Sets the inner text for this element.
     * @param text The inner text.
     */
    override fun setText(text: String?) {
      // TODO
    }
  }

  /**
   * A simple component that wraps a p html tag.
   */
  private class VParagraph : Paragraph() {
    /**
     * Sets the inner HTML for this component element.
     * @param html The component inner HTML.
     */
    fun setHtml(html: String?) {
      element.setProperty("innerHTML", html)
    }
  }

  //-------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------
  /**
   * Creates a new notification component with table containing
   * a title, a message, an image and buttons location.
   */
  init {

  }
}
