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
package org.kopi.galite.visual.ui.vaadin.grid

import java.lang.Exception

import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.notif.NotificationListener
import org.kopi.galite.visual.ui.vaadin.notif.NotificationUtils

/**
 * Thrown when the field content is checked against its validation
 * strategy. This exception is transformed later to an error notification.
 *
 * @param field The concerned text input zone.
 * @param messageKey The message key.
 * @param params The message parameters
 */
class InvalidEditorFieldException(val field: GridEditorField<*>,
                                  val messageKey: String,
                                  vararg val params: Any)
  : Exception(), NotificationListener {
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
      null,
      MainWindow.locale,
      messageKey,
      params
    )
  }

  override fun onClose(action: Boolean?) {
    field.setBlink(false)
    field.focus()
  }
}
