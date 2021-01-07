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

import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.base.VInputButton

/**
 * Information type notification component.
 */
open class VInformationNotification(title: String, message: String) : VAbstractNotification() {


  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  /**
   * Creates the information notification footer.
   */
  override fun createFooter() {
    val footer = HorizontalLayout()
    footer.add(close)
    footer.isSpacing = true
    footer.justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    footer.style.set("background-color", "AliceBlue")
    footer.width = "99%"
    footer.height = "35%"
    add(footer)
  }

  override fun setButtons(locale: String) {
    close =  VInputButton(LocalizedProperties.getString(locale, "CLOSE"))
    close.addClickListener {  hide()  }
    close.width = "20%"
    close.height = "50%"
  }

  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------

  var close = VInputButton()
  override val iconName: String
    get() = "info-circle"

  //--------------------------------------------------
  // CONSTRUCTOR
  //--------------------------------------------------

  /**
   * Creates the information widget.
   */
  init {
    super.title = Label(title)
    super.message = Label(message)
    super.initialize(title, message, locale)
  }

}
