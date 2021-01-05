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

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.base.VInputButton

/**
 * Confirm type notification component.
 */
open class VConfirmNotification(title: String, message: String) : VAbstractNotification() {

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  /**
   * Creates the confirmation notification footer.
   */
  override fun createFooter() {
    val footer = HorizontalLayout()
    footer.add(ok)
    footer.add(cancel)
    footer.isSpacing = true
    footer.justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    footer.style.set("background-color", "AliceBlue")
    footer.width = "99%"
    footer.height = "35%"
    add(footer)
  }

  override fun setButtons(locale: String) {
    ok =  VInputButton(LocalizedProperties.getString(locale, "OK"))
    cancel = VInputButton(LocalizedProperties.getString(locale, "NO"))
    cancel.addClickListener { super.close() }
    ok.width = "20%"
    ok.height = "50%"
    cancel.width = "20%"
    cancel.height = "50%"
  }

  //------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------

  override val iconName: String?
    get() = "question-circle"
  private var ok = VInputButton()
  private var cancel = VInputButton()
  val listener: ComponentEventListener<ClickEvent<Button>>? = null

  //--------------------------------------------------
  // CONSTRUCTOR
  //--------------------------------------------------

  /**
   * Creates the confirmation widget.
   */
  init {
    super.title = Label(title)
    super.message = Label(message)
    ok.addClickListener { open() }
    cancel.addClickListener { close() }
    super.initialize(title, message, locale)
  }
}
