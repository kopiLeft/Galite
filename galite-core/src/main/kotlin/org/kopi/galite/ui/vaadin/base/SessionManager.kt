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
package org.kopi.galite.ui.vaadin.base

import com.vaadin.flow.server.VaadinSession
import com.vaadin.flow.server.WebBrowser
import org.kopi.galite.monitoring.Application
import org.kopi.galite.monitoring.UserData
import org.kopi.galite.type.Timestamp

/**
 * Manages all the active sessions within the application.
 *
 * !!!experimental
 */
object SessionManager {

  /**
   * List of all opened vaadin sessions.
   */
  private val sessions = mutableMapOf<VaadinSession, UserData>()

  /**
   * List of data of active users.
   */
  val activeUsers: List<UserData> get() = sessions.values.toList()

  /**
   * Registers a user session if absent. If a session already exits, that means that the user opened
   * anther tab in the browser, so this will increment tabCount of the [userData].
   *
   * @param vaadinSession   the Vaadin session.
   * @return the user data registered.
   */
  fun register(vaadinSession: VaadinSession): UserData {
    val userData = sessions[vaadinSession]

    return if (userData == null) {
      val newUserData = retrieveUserData(vaadinSession)

      sessions[vaadinSession] = newUserData
      newUserData
    } else {
      userData.tabCount++
      userData
    }
  }

  /**
   * Unregister a session.
   */
  fun unregister(vaadinSession: VaadinSession) {
    sessions.remove(vaadinSession)
  }

  /**
   * Returns the userData for a specific [vaadinSession]
   */
  fun userFor(vaadinSession: VaadinSession): UserData? = sessions[vaadinSession]


  private fun retrieveUserData(session: VaadinSession): UserData {
    val webBrowser = session.browser
    val browser = Application(getBrowser(webBrowser), webBrowser.browserMajorVersion, webBrowser.browserMinorVersion)

    return UserData("", getOS(webBrowser), browser, webBrowser.address.orEmpty())
  }

  fun getBrowser(browser: WebBrowser): String {
    return when {
      browser.isChrome -> "Chrome"
      browser.isFirefox -> "Firefox"
      browser.isEdge -> "Edge"
      browser.isIE -> "Internet Explorer"
      browser.isSafari -> "Safari"
      browser.isOpera -> "Opera"
      else -> "Unknown"
    }
  }

  fun getOS(browser: WebBrowser): String {
    return when {
      browser.isChromeOS -> "ChromeOS"
      browser.isAndroid -> "Android"
      browser.isLinux -> "Linux"
      browser.isMacOSX -> "MacOS X"
      browser.isWindows -> "Windows"
      browser.isWindowsPhone -> "Windows Phone"
      browser.isIPhone -> "IPhone"
      else -> "Unknown"
    }
  }

  internal fun login() {
    val vaadinSession = VaadinSession.getCurrent()
    if(vaadinSession != null) {
      val userData = userFor(vaadinSession)
      userData?.loggedin = true
      userData?.loginTime = Timestamp.now()
    }
  }

  internal fun logout() {
    val vaadinSession = VaadinSession.getCurrent()
    if(vaadinSession != null) {
      val userData = userFor(vaadinSession)
      userData?.loggedin = false
      userData?.loginTime = null
    }
  }
}
