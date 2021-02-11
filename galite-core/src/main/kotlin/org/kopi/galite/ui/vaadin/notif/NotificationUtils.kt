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

import org.kopi.galite.ui.vaadin.base.LocalizedMessages
import org.kopi.galite.ui.vaadin.base.LocalizedProperties

import com.vaadin.flow.component.HasComponents

/**
 * Utilities to show notifications in the client side.
 */
object NotificationUtils {

  /**
   * Shows an error completely in the client side.
   *
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   */
  fun showError(callback: NotificationListener?,
                parent: HasComponents,
                locale: String,
                messageKey: String) {
    showError(callback, parent, locale, messageKey, null)
  }

  /**
   * Shows an error completely in the client side.
   *
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   * @param params The message parameters.
   */
  fun showError(callback: NotificationListener?,
                parent: HasComponents,
                locale: String,
                messageKey: String,
                vararg params: Any?) {
    val error = VErrorNotification(LocalizedProperties.getString(locale, "Error"),
                                   LocalizedMessages.getMessage(locale, messageKey, params))
    error.init()
    if (callback != null) {
      error.addNotificationListener(callback)
    }
    error.show(parent, locale)
  }

  /**
   * Shows a warning completely in the client side.
   *
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   */
  fun showWarning(callback: NotificationListener?,
                  parent: HasComponents,
                  locale: String,
                  messageKey: String) {
    showWarning(callback, parent, locale, messageKey, null)
  }

  /**
   * Shows a warning completely in the client side.
   *
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   * @param params The message parameters.
   */
  fun showWarning(callback: NotificationListener?,
                  parent: HasComponents,
                  locale: String,
                  messageKey: String,
                  vararg params: Any?) {
    val warning = VWarningNotification(LocalizedProperties.getString(locale, "Warning"),
                                       LocalizedMessages.getMessage(locale, messageKey, params))
    warning.init()
    if (callback != null) {
      warning.addNotificationListener(callback)
    }
    warning.show(parent, locale)
  }

  /**
   * Shows an information completely in the client side.
   *
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   */
  fun showInformation(callback: NotificationListener?,
                      parent: HasComponents,
                      locale: String,
                      messageKey: String) {
    showInformation(callback, parent, locale, messageKey, null)
  }

  /**
   * Shows an information completely in the client side.
   *
   * @param callback The notification callback listener.
   * @param parent The error notification parent.
   * @param locale The error locale.
   * @param messageKey The message key to be displayed.
   * @param params The message parameters.
   */
  fun showInformation(callback: NotificationListener?,
                      parent: HasComponents,
                      locale: String,
                      messageKey: String,
                      vararg params: Any?) {
    val information = VInformationNotification(LocalizedProperties.getString(locale, "Notice"),
                                               LocalizedMessages.getMessage(locale, messageKey, params))
    information.init()
    if (callback != null) {
      information.addNotificationListener(callback)
    }
    information.show(parent, locale)
  }
}
