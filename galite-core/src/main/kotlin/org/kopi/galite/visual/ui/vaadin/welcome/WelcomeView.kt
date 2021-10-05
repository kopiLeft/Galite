/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.welcome

import java.util.Locale

import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.common.VContent
import org.kopi.galite.visual.ui.vaadin.common.VHeader
import org.kopi.galite.visual.ui.vaadin.common.VMain
import org.kopi.galite.visual.ui.vaadin.event.LoginWindowListener
import org.kopi.galite.visual.ui.vaadin.login.VEmptyModuleList
import org.kopi.galite.visual.ui.vaadin.login.VLoginView
import org.kopi.galite.visual.ui.vaadin.login.VLoginWindow

import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.applayout.AppLayout

/**
 * The server side of the welcome screen.
 */
class WelcomeView(locale: Locale,
                  languages: Array<Locale>?,
                  slogan: String?,
                  logo: String?,
                  href: String?) : AppLayout(), HasStyle, HasSize, LoginWindowListener {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  protected fun setLanguages(locale: Locale, languages: Array<Locale>?) {
    for (language in languages!!) {
      addSupportedLanguage(language.getDisplayName(locale), language.toString())
      if (locale == language) {
        setSelectedLanguage(language.toString())
      }
    }
  }

  /**
   * Sets the href for the anchor element.
   * @param href the href
   */
  fun setHref(href: String?) {
    header!!.setHref(href)
  }

  /**
   * Sets the target frame.
   * @param target The target frame.
   */
  fun setTarget(target: String) {
    header!!.setTarget(target)
  }

  /**
   * Sets the company logo image.
   *
   * @param url The logo image URL.
   * @param alt The alternate text.
   */
  fun setLogo(url: String, alt: String?) {
    header!!.setImage(url, alt)
  }

  /**
   * Adds a supported language for the application.
   * @param language The language display name.
   * @param isocode The language ISO code.
   */
  fun addSupportedLanguage(language: String, isocode: String?) {
    loginWindow!!.addSupportedLanguage(language, isocode)
  }

  /**
   * Adds a login window listener.
   * @param l The listener to be added.
   */
  fun addLoginWindowListener(l: LoginWindowListener) {
    loginWindow!!.addLoginWindowListener(l)
  }

  /**
   * Registers a welcome screen listener on this component.
   * @param listener The listener to be registered.
   */
  fun addWelcomeViewListener(listener: (WelcomeViewEvent) -> Unit) {
    addListener(WelcomeViewEvent::class.java, listener)
  }

  /**
   * Sets the slogan image.
   * @param slogan The slogan image URL.
   */
  fun setSloganImage(slogan: String?) {
    loginWindow!!.setSloganImage(slogan)
  }

  /**
   * Sets the default welcome screen locale.
   * @param locale The welcome screen locale.
   */
  fun setDefaultLocale(locale: String?) {
    loginWindow!!.setLocale(locale)
  }

  /**
   * Sets the selected language.
   * @param language The language index.
   */
  fun setSelectedLanguage(language: String) {
    loginWindow!!.setSelectedLanguage(language)
  }

  /**
   * Sets the error.
   * @param error The error message.
   */
  fun setError(error: String?) {
    loginWindow!!.setError(error)
  }

  /**
   * Focus on the first field in the login panel.
   */
  fun focus() {
    loginWindow!!.focus()
  }

  fun clear() { //super.clear();
    header = null
    main = null
    loginWindow = null
  }

  override fun onLogin(username: String, password: String, language: String) {
    fireEvent(WelcomeViewEvent(this, username, password, language))
  }

  private fun setResources(logo: String?, slogan: String?) {
    if (logo != null) {
      setLogo(logo, null)
    }
    slogan?.let { setSloganImage(it) }
  }

  fun setWaitInfo() {
    loginWindow!!.setWaitInfo()
  }

  fun unsetWaitInfo() {
    loginWindow!!.unsetWaitInfo()
  }

  //---------------------------------------------------
// DATA MEMBERS
//---------------------------------------------------
  private var header: VHeader?
  private var main: VMain?
  private var loginWindow: VLoginWindow?

  //---------------------------------------------------
// CONSTRUCTOR
//---------------------------------------------------
  init {
    className = Styles.WELCOME_VIEW
    header = VHeader()
    main = VMain()
    loginWindow = VLoginWindow()
    addToNavbar(header)
    content = main
    header!!.setMainMenu(VEmptyModuleList())
    val content = VContent()
    main!!.setContent(content)
    val loginView = VLoginView()
    loginView.setLoginWindow(loginWindow)
    content.setContent(loginView)
    setHref(href)
    setResources(logo, slogan)
    setTarget("_blank")
    focus()
    addLoginWindowListener(this)
    setDefaultLocale(locale.toString())
    setLanguages(locale, languages)
  }
}
