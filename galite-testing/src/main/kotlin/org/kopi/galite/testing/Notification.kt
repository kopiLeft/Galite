/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import kotlin.test.assertEquals

import org.kopi.galite.visual.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.visual.ui.vaadin.notif.ConfirmNotification
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification
import org.kopi.galite.visual.ui.vaadin.common.VSpan
import org.kopi.galite.visual.ui.vaadin.notif.InformationNotification

import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * Interacts with a [ConfirmNotification] dialog.
 *
 * @param confirm true if you want to confirm, false if you want to discard.
 */
fun expectConfirmNotification(confirm: Boolean) {
  //val notificationFooter = _get<ConfirmNotification>().footers
  val button = if(confirm) {
    //notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "OK") }
  } else {
    //notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "NO") }
  }

  //button._click()
  waitAndRunUIQueue(100)
}

/**
 * Interacts with a [ErrorNotification] dialog.
 *
 * call function to close error notification
 */
fun expectErrorNotification(message: String, close: Boolean = true) {
  //val notificationFooter = _get<ErrorNotification>().footers
  //val button =  notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "CLOSE") }
  val errorPopUp = _get<ErrorNotification>()
  val errorMessage = errorPopUp
    ._get<HorizontalLayout> { classes = "k-notification-content"}
    ._get<VSpan> { classes = "k-notification-message"  }

  assertEquals(message, errorMessage.getHtml())
  if (close) {
    //button._click()
    waitAndRunUIQueue(100)
  }
}

/**
 * Interacts with a [InformationNotification] dialog.
 *
 * call function to close information notification
 */
fun expectInformationNotification(message: String, close: Boolean = true) {
  //val notificationFooter = _get<InformationNotification>().footers
  val informationMessage = _get<InformationNotification>()
    ._get<HorizontalLayout> { classes = "k-notification-content"}
    ._get<VSpan> { classes = "k-notification-message"  }

  assertEquals(message, informationMessage.getHtml())

  if(close) {
    //notificationFooter._get<Button> { text = LocalizedProperties.getString(defaultLocale, "CLOSE") }._click()
  }

  waitAndRunUIQueue(100)
}
