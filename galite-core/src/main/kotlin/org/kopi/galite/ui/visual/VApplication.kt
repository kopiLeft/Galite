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

package org.kopi.galite.ui.visual

import java.sql.SQLException
import java.util.Date
import java.util.Locale

import org.kopi.galite.base.UComponent
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.print.PrintManager
import org.kopi.galite.visual.Application
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.ImageHandler
import org.kopi.galite.visual.PrinterManager
import org.kopi.galite.visual.Registry
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.VMenuTree
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.WindowController
import org.kopi.galite.db.DBContext

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.Component
import com.vaadin.flow.router.Route
import org.kopi.galite.ui.base.StylesInjector


/**
 * The entry point for all WEB applications.
 *
 * @param registry The [Registry] object.
 */
@Route("")
abstract class VApplication(override val registry: Registry) : VerticalLayout(), Application {
  init {
    checkAlternateLocale() // needs to do this to ensure that we start with a valid locale;
    // registry and locale initialization
    initialize()
    gotoWelcomeView()
  }

  // ---------------------------------------------------------------------
  // MESSAGE LISTENER IMPLEMENTATION
  // ---------------------------------------------------------------------
  override fun notice(message: String) {

  }

  override fun error(message: String) {

  }

  override fun warn(message: String) {

  }

  override fun ask(message: String, yesIsDefault: Boolean): Int {
    TODO()
  }

  //---------------------------------------------------------------------
  // APPLICATION IMPLEMENTATION
  // ---------------------------------------------------------------------
  override fun logout() {

  }

  override fun startApplication() {

  }

  override fun allowQuit(): Boolean =
          if (getInitParameter("allowQuit") == null) true
          else java.lang.Boolean.parseBoolean(getInitParameter("allowQuit"))

  override lateinit var applicationConfiguration: ApplicationConfiguration

  override lateinit var printManager: PrintManager

  override lateinit var printerManager: PrinterManager

  // --------------------------------------------------
  // WELCOME VIEW LISTENER IMPLEMENTATION
  // --------------------------------------------------
  fun onLogin() {

  }

  // --------------------------------------------------
  // PRIVATE MEMBERS
  // --------------------------------------------------
  /**
   * Tries to connect to the database using user name and password
   * provided by the login window.
   * @param username The login user name.
   * @param password The login password.
   * @throws SQLException When cannot connect to database.
   * @see .login
   */
  private fun connectToDatabase(username: String, password: String) {

  }

  // --------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------
  override val isNoBugReport: Boolean
    get() = java.lang.Boolean.parseBoolean(getInitParameter("nobugreport"))

  override val menu: VMenuTree
    get() = TODO()

  override var isGeneratingHelp: Boolean = false

  override var dBContext: DBContext? = null

  override val userName: String
  get() = dBContext!!.defaultConnection.userName

  override val defaultLocale: Locale? = null

  override val localizationManager: LocalizationManager? = null

  override fun displayError(parent: UComponent, message: String) {
    error(message)
  }

  //---------------------------------------------------
  // UTILS
  // --------------------------------------------------
  /**
   * Attaches the given component to the application.
   * @param component The component to be attached.
   */
  fun attachComponent(component: Component?) {

  }

  /**
   * Detaches the given component from the application.
   * @param component The component to be detached.
   */
  fun detachComponent(component: Component?) {

  }

  /**
   * The database URL.
   */
  val url: String
    get() = dBContext!!.defaultConnection.url

  /**
   * This methods is called at the beginning
   * you should use it to define [Locale], debugMode...
   */
  fun initialize() {

  }

  /**
   * Verifies the configuration settings.
   */
  fun verifyConfiguration() {

  }

  /**
   * Attaches a window to this application.
   * @param window The window to be added.
   */
  fun addWindow(window: Component) {

  }

  /**
   * Removes an attached window to this main window.
   * @param window The window to be removed.
   */
  fun removeWindow(window: Component) {

  }

  /**
   * Checks the existence of the application locale.
   * @exception VRuntimeException When no alternate locale is defined.
   */
  protected fun checkAlternateLocale() {
    if (alternateLocale == null) {
      throw VRuntimeException("An alternate locale should be provided with the application")
    }
  }

  /**
   * Sets the localization context.
   *
   *
   * This aims to set the application [.defaultLocale]
   * and [.localizationManager] internal attributes.
   *
   */
  protected fun setLocalizationContext(locale: Locale) {

  }

  /**
   * Returns the initialization locale found in the application descriptor file.
   * @return the initialization locale found in the application descriptor file.
   */
  protected open fun getInitializationLocale(): Locale? {
    TODO()
  }

  /**
   * Sets the query trace level.
   */
  protected fun setTraceLevel() {

  }

  /**
   * Closes the database connection
   */
  protected fun closeConnection() {

  }

  /**
   * Returns the client side calculated font metrics for a given font.
   * @param fontFamily The font family.
   * @param fontSize The font size.
   * @param text The text.
   * @return The text width in the given font.
   */
  fun getWidth(fontFamily: String?, fontSize: Int, text: String?): Int {

    return 0
  }

  /**
   * Shows the welcome view.
   */
  protected fun gotoWelcomeView() {

  }

  /**
   * Checks the given locale format.
   * @param locale The locale to be checked.
   * @return `true` if the locale has a valid format.
   */
  private fun checkLocale(locale: String): Boolean {
    val chars = locale.toCharArray()
    return !(chars.size != 5 ||
            chars[0] < 'a' ||
            chars[0] > 'z' ||
            chars[1] < 'a' ||
            chars[1] > 'z' ||
            chars[2] != '_' ||
            chars[3] < 'A' ||
            chars[3] > 'Z' ||
            chars[4] < 'A' ||
            chars[4] > 'Z')
  }

  //---------------------------------------------------
  // MAIN WINDOW LISTENER IMPLEMENTATION
  // --------------------------------------------------
  fun onAdmin() {
    // TODO
  }

  fun onSupport() {
    // TODO
  }

  fun onHelp() {
    // TODO
  }

  fun onLogout() {
    // close database connection and show welcome view
    logout()
  }

  fun onUser() {
    // TODO
  }

  override val userIP: String
    get() = TODO()

  //---------------------------------------------------
  // UTILS
  // --------------------------------------------------
  /**
   * Returns the initialization parameter of the given key.
   * The initialization parameter is contained in the application
   * descriptor (Web.xml) file.
   * @param key The parameter key.
   * @return The initialization parameter contained in the application descriptor file.
   */
  protected fun getInitParameter(key: String?): String? {
    TODO()
  }

  /**
   * Returns the styles injector attached with this application instance.
   * @return The styles injector attached with this application instance.
   */
  fun getStylesInjector(): StylesInjector {
    TODO()
  }

  //---------------------------------------------------
  // ABSTRACT MEMBERS TO CUSTOMIZE YOUR APPLICATION
  // --------------------------------------------------
  /**
   * The supported locales that can be used with this application.
   *
   * The map will contain the displayed language as key value. Its corresponding
   * value is the locale ISO code.
   *
   */
  protected abstract val supportedLocales: Array<Locale>

  /**
   * The SLOGAN image to be used in welcome screen.
   */
  protected abstract val sologanImage: String

  /**
   * The LOGO image to be used with application.
   */
  protected abstract val logoImage: String

  /**
   * The LOGO link to be associated with the application LOGO image.
   * @see logoImage
   */
  protected abstract val logoHref: String

  /**
   * Returns the alternate locale to be used as default locale
   * when no default locale is specified. This will force the application
   * to use the given locale to avoid localization problems.
   * ** This language should not be `null`. Otherwise, the application won't start**
   * @return The alternate locale to be used when no default locale is specified.
   */
  protected abstract val alternateLocale: Locale

  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------
  private var askAnswer = 0
  private lateinit var configuration: ApplicationConfiguration
  private var stylesInjector: StylesInjector? = null

  // ---------------------------------------------------------------------
  // Failure cause informations
  // ---------------------------------------------------------------------

  override val startupTime: Date = Date() // remembers the startup time

  companion object {
    init {
      ApplicationContext.applicationContext = VApplicationContext()
      FileHandler.fileHandler = VFileHandler()
      ImageHandler.imageHandler = VImageHandler()
      WindowController.windowController = VWindowController()
      UIFactory.uiFactory = VUIFactory()
    }
  }
}
