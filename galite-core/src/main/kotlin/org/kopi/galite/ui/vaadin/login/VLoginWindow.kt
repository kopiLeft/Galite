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
package org.kopi.galite.ui.vaadin.login

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.dom.DomEvent
import org.kopi.galite.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.event.LoginWindowListener

/**
 * The login window box used for identification of the user.
 * the user and its password are send to the server for verification.
 */
class VLoginWindow : Div() {
  //---------------------------------------------------
// IMPLEMENTATIONS
//---------------------------------------------------
  /**
   * Initializes the content of the login box.
   * @param welcomeText The welcome text.
   * @param informationText The information text.
   * @param usernameLabel The user name label.
   * @param passwordLabel The password label.
   * @param languageLabel The language label.
   * @param loginText the login text.
   */
  protected fun doInit(welcomeText: String?,
                       informationText: String?,
                       usernameLabel: String?,
                       passwordLabel: String?,
                       languageLabel: String?,
                       loginText: String?) {
    loginBox.setWelcomeText(welcomeText)
    loginBox.setInformationText(informationText)
    loginBox.setUsernameLabel(usernameLabel)
    loginBox.setPasswordLabel(passwordLabel)
    loginBox.setLanguageLabel(languageLabel)
    loginBox.setLoginText(loginText)
  }

  /**
   * Sets the welcome image.
   * @param welcomeImage The welcome image
   */
  fun setSloganImage(welcomeImage: String?) {
    loginBox.setWelcomeImage(welcomeImage)
  }

  /**
   * Adds a supported language for the application.
   * @param language The language display name.
   * @param isocode The language ISO code.
   */
  fun addSupportedLanguage(language: String, isocode: String?) {
    loginBox.addLanguage(language, isocode)
  }

  /**
   * Sets the error.
   * @param error The error message.
   */
  fun setError(error: String?) {
    loginBox.setError(error)
  }

  /**
   * Removes an error
   */
  fun removeError() {
    loginBox.removeError()
  }

  /**
   * Sets the selected language.
   * @param language The language index.
   */
  fun setSelectedLanguage(language: String) {
    loginBox.selectedLanguage = language
  }

  /**
   * Adds a login window listener.
   * @param l The listener to be added.
   */
  fun addLoginWindowListener(l: LoginWindowListener) {
    listeners.add(l)
  }

  /**
   * Removes a login window listener.
   * @param l The listener to be removed.
   */
  protected fun removeLoginWindowListener(l: LoginWindowListener?) {
    listeners.remove(l)
  }

  protected fun fireLogin(username: String, password: String, language: String) {
    for (l in listeners) {
      l.onLogin(username, password, language)
    }
  }

  fun onClick() {
    removeError()
    fireLogin(loginBox.username, loginBox.password, loginBox.selectedLanguage)
  }

  fun handleSelectionChange(event: DomEvent?) {
    removeError()
    setLocale(loginBox.selectedLanguage)
  }

  /**
   * Sets the login box locale.
   * @param locale The locale to use.
   */
  fun setLocale(locale: String?) {
    doInit(LocalizedProperties.getString(locale, "welcomeText"),
           LocalizedProperties.getString(locale, "informationText"),
           LocalizedProperties.getString(locale, "usernameLabel"),
           LocalizedProperties.getString(locale, "passwordLabel"),
           LocalizedProperties.getString(locale, "languageLabel"),
           LocalizedProperties.getString(locale, "loginText"))
  }

  /**
   * Focus on the first field in the login box.
   */
  fun focus() {
    loginBox.focus()
  }

  //---------------------------------------------------
// DATA MEMBERS
//---------------------------------------------------
  private val loginBox: VLoginBox
  private val listeners: MutableList<LoginWindowListener>
  //---------------------------------------------------
// CONSTRUCTOR
//---------------------------------------------------
  /**
   * Creates the login window box.
   */
  init {
    className = Styles.LOGIN_WINDOW
    listeners = ArrayList()
    loginBox = VLoginBox()
    add(loginBox)
    loginBox.addClickHandler { onClick() }
    loginBox.addChangeHandler { event: DomEvent? -> handleSelectionChange(event) }
    width = "400px"
  }
}
