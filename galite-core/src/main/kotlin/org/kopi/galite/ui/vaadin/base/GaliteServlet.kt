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
package org.kopi.galite.ui.vaadin.base

import java.util.Locale
import java.util.Date

import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.base.InconsistencyException

import com.vaadin.flow.function.DeploymentConfiguration
import com.vaadin.flow.server.CustomizedSystemMessages
import com.vaadin.flow.server.SessionInitEvent
import com.vaadin.flow.server.SessionInitListener
import com.vaadin.flow.server.SystemMessagesProvider
import com.vaadin.flow.server.VaadinRequest
import com.vaadin.flow.server.VaadinResponse
import com.vaadin.flow.server.VaadinServlet
import com.vaadin.flow.server.VaadinServletService
import com.vaadin.flow.server.VaadinSession

/**
 * A customized servlet that handles the localization
 * of the "session expired" message.
 */
open class GaliteServlet : VaadinServlet(), SessionInitListener {
  override fun servletInitialized() {
    super.servletInitialized()
    val locale: String = getInitParameter("locale")

    // check if it is a valid locale.
    if (checkLocale(getInitParameter("locale"))) {
      this.locale = Locale(locale.substring(0, 2), locale.substring(3, 5))
      service.addSessionInitListener(this)
      // localize the session expired message
      service.systemMessagesProvider = SystemMessagesProvider {
        val message = CustomizedSystemMessages()
        message.isSessionExpiredNotificationEnabled = true
        message.sessionExpiredCaption = getLocalizedProperty("session-expired-caption")
        message.sessionExpiredMessage = getLocalizedProperty("session-expired-message")
        message
      }
    }
  }

  override fun sessionInit(event: SessionInitEvent) {
    event.session.locale = locale
  }

  override fun createServletService(deploymentConfiguration: DeploymentConfiguration?): VaadinServletService {
    val service = object : VaadinServletService(this, deploymentConfiguration) {
      override fun requestEnd(request: VaadinRequest?, response: VaadinResponse?, session: VaadinSession?) {
        super.requestEnd(request, response, session)
        if (isDebugMode && session != null) {
          log(session, request)
        }
      }
    }
    service.init()
    return service
  }

  /**
   * Checks the given locale format.
   * @param locale The locale to be checked.
   * @return `true` if the locale has a valid format.
   */
  private fun checkLocale(locale: String): Boolean {
    val chars: CharArray = locale.toCharArray()

    if (chars.size != 5
      || chars[0] < 'a' || chars[0] > 'z'
      || chars[1] < 'a' || chars[1] > 'z'
      || chars[2] != '_'
      || chars[3] < 'A' || chars[3] > 'Z'
      || chars[4] < 'A' || chars[4] > 'Z')
    {
      return false
    }

    return true
  }

  /**
   * Returns a localized property from XML property file.
   * @param key The property key.
   * @return The localized property.
   */
  private fun getLocalizedProperty(key: String): String {
    val manager = LocalizationManager(locale, Locale.getDefault())
    return try {
      // Within a String, "''" represents a single quote in java.text.MessageFormat.
      manager.getPropertyLocalizer(VLIB_PROPERTIES_RESOURCE_FILE, key).getValue().replace("'".toRegex(), "''")
    } catch (e: InconsistencyException) {
      "!$key!"
    }
  }

  /**
   * Traces the session request statistics.
   * @param session The session instance.
   * @param request The request object.
   */
  private fun log(session: VaadinSession, request: VaadinRequest?) {
    try {
      session.lock()
      println(
        request!!.remoteAddr.toString() + " - - "
                + session.csrfToken + " - "
                + Date(session.lastRequestTimestamp) + " - "
                + session.browser.browserApplication + " - "
                + request.method + " / "
                + request.contentType + " - "
                + session.lastRequestDuration + " / "
                + session.cumulativeRequestDuration
      )
    } finally {
      session.unlock()
    }
  }

  /**
   * Returns true is the requests exchanges times should be shown on log file.
   * @return true is the requests exchanges times should be shown on log file.
   */
  private val isDebugMode: Boolean
    get() = java.lang.Boolean.parseBoolean(getInitParameter("debugMode"))

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private var locale: Locale? = null

  companion object {
    private const val VLIB_PROPERTIES_RESOURCE_FILE = "org/kopi/galite/VlibProperties"
  }
}
