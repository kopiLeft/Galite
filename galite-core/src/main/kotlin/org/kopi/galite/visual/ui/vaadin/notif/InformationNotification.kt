/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.notif

import org.kopi.galite.visual.ui.vaadin.base.LocalizedProperties

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * Information type notification component.
 *
 * @param title the information notification title.
 * @param message the information notification message.
 * @param locale  the notification locale
 */
class InformationNotification(title: String?,
                              message: String,
                              locale: String,
                              parent: Component?)
  : AbstractNotification(title, message, locale, parent) {

  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------
  private lateinit var close: Button

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  override fun setButtons() {
    close = Button(LocalizedProperties.getString(locale, "CLOSE"))
    close.addClickListener { fireOnClose(null) }
    buttons.add(close)
    close.isAutofocus = true
  }

  override fun getDefaultButton(): Button = close

  override fun setNavigationListeners() {
    // SHORTCUTS:
    // For locale = EN : C -> Close
    // For locale = FR : F -> Fermer
    element.executeJs(
      """
        window.___keyPress = function(event) {
          if (event.key == '${close.text[0].lowercase()}') {
            $0.${"$"}server.onNavigation($CLICK_CLOSE);
          }
        }

        window.addEventListener('keypress', ___keyPress);""",
      element
    )

    // Cleanup listeners on detach
    addDetachListener {
      element.executeJs(
        """
          window.removeEventListener('keypress', ___keyPress);
          """)
    }
  }

  override val iconName: VaadinIcon
    get() = VaadinIcon.INFO_CIRCLE
}
