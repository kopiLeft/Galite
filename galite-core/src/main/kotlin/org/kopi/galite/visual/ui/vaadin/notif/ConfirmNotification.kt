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

import com.vaadin.flow.component.ClientCallable
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * Confirm type notification component.
 *
 * @param title the confirm notification title.
 * @param message the confirm notification message.
 * @param locale  the notification locale
 */
class ConfirmNotification(title: String?,
                          message: String,
                          locale: String,
                          parent: Component?)
  : AbstractNotification(title, message, locale, parent) {

  //------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------
  private lateinit var ok: Button
  private lateinit var cancel: Button

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  override fun setButtons() {
    ok = Button(LocalizedProperties.getString(locale, "OK"))
    cancel = Button(LocalizedProperties.getString(locale, "NO"))
    ok.addClickListener { fireOnClose(true) }
    cancel.addClickListener { fireOnClose(false) }
    buttons.add(ok)
    buttons.add(cancel)
  }

  override val iconName: VaadinIcon
    get() = VaadinIcon.QUESTION_CIRCLE

  override fun getDefaultButton(): Button {
    return if (yesIsDefault) {
      ok
    } else {
      cancel
    }
  }

  override fun setNavigationListeners() {
    // SHORTCUTS:
    // For locale = EN : Y -> yes, N -> no
    // For locale = FR : O -> oui, N -> non
    element.executeJs(
      """
        window.___keyPress = function(event) {
          if (event.key == '${cancel.text[0].lowercase()}') {
            $0.${"$"}server.onNavigation($CLICK_CANCEL);
          } else if (event.key == '${ok.text[0].lowercase()}') {
            $0.${"$"}server.onNavigation($CLICK_OK);
          }
        }

        window.addEventListener('keypress', ___keyPress);""",
      element
    )

    // NAVIGATION OK <- -> CANCEL
    element.executeJs(
      """
        window.___keyDown = function(event) {
          if (event.keyCode == 37) { // 37: Arrow left
            $0.focus();
          } else if (event.keyCode == 39) { // 39: Arrow right
            $1.focus();
          }
        }

        window.addEventListener('keydown', ___keyDown);""",
      ok.element,
      cancel.element
    )

    // Cleanup listeners on detach
    addDetachListener {
      element.executeJs(
        """
          window.removeEventListener('keypress', ___keyPress);
          window.removeEventListener('keydown', ___keyDown);
          """)
    }
  }

  @ClientCallable
  fun onNavigation(action: Int) {
    when (action) {
      CLICK_OK -> ok.click()
      CLICK_CANCEL -> cancel.click()
    }
  }
}
