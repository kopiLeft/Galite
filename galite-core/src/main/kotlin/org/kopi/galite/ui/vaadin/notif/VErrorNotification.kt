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

import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.base.VInputButton

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.details.DetailsVariant
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent

/**
 * Error type notification component.
 */
open class VErrorNotification(title: String?, message: String) : VAbstractNotification() {

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  /**
   * Creates the error notification footer.
   */
  override fun createFooter() {
    footer.add(details)
    footer.add(close)
    footer.isSpacing = true
    footer.justifyContentMode = FlexComponent.JustifyContentMode.CENTER
    footer.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, details)
    footer.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, close)
    super.setFooter(footer)
  }

  override fun setButtons(locale: String) {
    close = VInputButton(LocalizedProperties.getString(locale, "CLOSE"))
    if (locale == "fr_FR") {
      details = Details("Afficher les détails",
                        Text("Les détails ici"))
    } else if (locale == "en_GB") {
      details = Details("Show details",
                        Text("Details here"))
    }
    close.addClickListener { hide() }
    close.width = "20%"
    close.height = "50%"
  }

  override fun onEnterEvent(keyDownEvent: ShortcutEvent?) {
    close.click()
  }

  override fun onRightEvent(keyDownEvent: ShortcutEvent?) {
    TODO("Not yet implemented")
  }

  override fun onLeftEvent(keyDownEvent: ShortcutEvent?) {
    TODO("Not yet implemented")
  }

  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------
  var details = Details()
  var close = VInputButton()
  val listener: ComponentEventListener<ClickEvent<Button>>? = null
  override val iconName: String
    get() = "warning"

  //-------------------------------------------------
  // CONSTRUCTOR
  //-------------------------------------------------
  init {
    super.title = H3(title)
    super.message = Span(message)
    details.element.setAttribute("aria-label", "Click me")
    details.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED)
    super.initialize(title, message, locale)
  }
}
