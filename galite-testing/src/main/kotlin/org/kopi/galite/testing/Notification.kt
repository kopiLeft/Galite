/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.testing

import org.kopi.galite.visual.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.visual.ui.vaadin.notif.ConfirmNotification
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification

import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.button.Button

/**
 * Interacts with a [ConfirmNotification] dialog.
 *
 * @param value true if you want to confirm, false if you want to discard.
 */
fun confirm(value: Boolean) {
  val notificationFooter = _get<ConfirmNotification>().footer
  val button = if(value) {
    notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "OK") }
  } else {
    notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "NO") }
  }

  button._click()
  waitAndRunUIQueue(100)
}

/**
 * Interacts with a [ErrorNotification] dialog.
 *
 * call function to close error notification
 */
fun closeErrorNotification() {
  val notificationFooter = _get<ErrorNotification>().buttons
  val button =  notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "CLOSE") }

  button._click()
  waitAndRunUIQueue(100)
}