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
package org.kopi.galite.ui.vaadin.field

import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.notif.NotificationListener
import org.kopi.galite.ui.vaadin.notif.NotificationUtils

/**
 * Thrown when the field content is checked against its validator.
 * This exception is transformed later to an error notification.
 *
 * @param field The concerned text input zone.
 * @param messageKey The message key.
 * @param params The message parameters
 */
class CheckTypeException(
  val field: InputTextField<*>,
  private val messageKey: String,
  vararg val params: Any?
) : Exception(), NotificationListener {

  /**
   * Creates a new check type exception from a message key.
   * @param field The concerned text input zone.
   * @param messageKey The message key.
   */
  constructor(field: InputTextField<*>, messageKey: String) : this(field, messageKey, null)

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Displays the check type error.
   */
  fun displayError() {
    field.setBlink(true)
    NotificationUtils.showError(
      this,
      MainWindow.locale,
      messageKey,
      params
    )
  }

  override fun onClose(action: Boolean?) {
    field.setBlink(false)
    field.setFocus(true)
    field.setCheckingValue(false)
    field.fieldConnector.isChanged = true
  }
}
