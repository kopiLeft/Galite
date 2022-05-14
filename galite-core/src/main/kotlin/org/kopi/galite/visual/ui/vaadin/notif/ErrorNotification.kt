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

import org.apache.commons.lang3.exception.ExceptionUtils
import org.kopi.galite.visual.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.visual.ui.vaadin.base.Utils.findMainWindow

import com.vaadin.flow.component.ClientCallable
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * Error type notification component.
 *
 * @param title the error notification title.
 * @param message the error notification message.
 * @param locale  the notification locale
 */
class ErrorNotification(title: String?,
                        val message: String?,
                        locale: String,
                        parent: Component?)
  : AbstractNotification(title, message, locale, parent) {

  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------
  private var details: ErrorMessageDetails? = null
  private lateinit var close: Button

  //-------------------------------------------------
  // IMPLEMENTATION
  //-------------------------------------------------

  override fun setButtons() {
    val application = parent?.findMainWindow()?.application

    close = Button(LocalizedProperties.getString(locale, "CLOSE"))
    close.addClickListener { fireOnClose(null) }
    close.isAutofocus = true
    buttons.add(close)

    if(application?.windowError != null) {
      val stacktrace = ExceptionUtils.getStackTrace(application.windowError)
      val htmlStacktrace = stacktrace.replace(System.lineSeparator(), "<br/>\n")
                                     .replace("\t", "&emsp;")

      details = ErrorMessageDetails(htmlStacktrace, locale, this)
      details!!.element.setAttribute("aria-label", "Click me") // FIXME : do we need this?
      footer.add(details)
    }
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


  @ClientCallable
  fun onNavigation(action: Int) {
    when (action) {
      CLICK_CLOSE -> close.click()
    }
  }

  override val iconName: VaadinIcon
    get() = VaadinIcon.EXCLAMATION_CIRCLE
}
