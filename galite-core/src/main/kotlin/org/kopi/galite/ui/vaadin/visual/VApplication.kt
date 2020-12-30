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

package org.kopi.galite.ui.vaadin.visual

import java.sql.SQLException
import java.util.Date
import java.util.Locale

import org.kopi.galite.base.UComponent
import org.kopi.galite.db.DBContext
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.print.PrintManager
import org.kopi.galite.ui.vaadin.base.StylesInjector
import org.kopi.galite.ui.vaadin.notification.ConfirmNotification
import org.kopi.galite.ui.vaadin.notification.ErrorNotification
import org.kopi.galite.ui.vaadin.notification.InformationNotification
import org.kopi.galite.ui.vaadin.notification.NotificationListener
import org.kopi.galite.ui.vaadin.notification.VAbstractNotification
import org.kopi.galite.ui.vaadin.notification.WarningNotification
import org.kopi.galite.ui.vaadin.welcome.WelcomeView
import org.kopi.galite.ui.vaadin.welcome.WelcomeViewEvent
import org.kopi.galite.visual.Application
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.ImageHandler
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.MessageListener
import org.kopi.galite.visual.PrinterManager
import org.kopi.galite.visual.Registry
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.VMenuTree
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.WindowController

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

/**
 * The entry point for all Galite WEB applications.
 *
 * @param registry The [Registry] object.
 */
@Route("")
abstract class VApplication(override val registry: Registry) : VerticalLayout(), Application {

  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------
  private var welcomeView: WelcomeView? = null
  private var askAnswer = 0
  private lateinit var configuration: ApplicationConfiguration
  private var stylesInjector: StylesInjector? = null

  // ---------------------------------------------------------------------
  // Failure cause informations
  // ---------------------------------------------------------------------

  override val startupTime: Date = Date() // remembers the startup time

  init {
    instance = this
    // registry and locale initialization
    initialize()
    gotoWelcomeView()
    askAnswer = MessageListener.AWR_UNDEF
  }

  // ---------------------------------------------------------------------
  // MESSAGE LISTENER IMPLEMENTATION
  // ---------------------------------------------------------------------
  override fun notice(message: String) {
    /*
    val dialog = InformationNotification(VlibProperties.getString("Notice"), message)
    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(yes: Boolean) {
        detachComponent(dialog)
      }
    })
    showNotification(dialog)*/ TODO()
  }

  override fun error(message: String?) {
    /*
    val dialog = ErrorNotification(VlibProperties.getString("Error"), message)
    dialog.setOwner(this)
    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(yes: Boolean) {
        detachComponent(dialog)
      }
    })
    showNotification(dialog)
*/ TODO()
  }

  override fun warn(message: String) {
    /*
    val dialog = WarningNotification(VlibProperties.getString("Warning"), message)
    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(yes: Boolean) {
        detachComponent(dialog)
      }
    })
    showNotification(dialog)*/ TODO()
  }

  override fun ask(message: String, yesIsDefault: Boolean): Int {
    /*
    val dialog = ConfirmNotification(VlibProperties.getString("Question"), message)
    dialog.yesIsDefault = yesIsDefault
    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(yes: Boolean) {
        askAnswer = if (yes) {
          MessageListener.AWR_YES
        } else {
          MessageListener.AWR_NO
        }
        detachComponent(dialog)
      }
    })
    // attach the notification to the application.
    showNotification(dialog)

    return askAnswer */ TODO()
  }

  /**
   * Shows a notification.
   * @param notification The notification to be shown
   */
  protected open fun showNotification(notification: VAbstractNotification?) {
    if (notification == null) {
      return
    }
    notification.locale = defaultLocale.toString()
    attachComponent(notification)
  }

  //---------------------------------------------------------------------
  // APPLICATION IMPLEMENTATION
  // ---------------------------------------------------------------------
  override fun logout() {

  }

  override fun startApplication() {

  }

  override fun allowQuit(): Boolean =
          getInitParameter("allowQuit") == null ||
                  java.lang.Boolean.parseBoolean(getInitParameter("allowQuit"))

  override lateinit var applicationConfiguration: ApplicationConfiguration

  override lateinit var printManager: PrintManager

  override lateinit var printerManager: PrinterManager

  // --------------------------------------------------
  // WELCOME VIEW LISTENER IMPLEMENTATION
  // --------------------------------------------------
  fun onLogin(event: WelcomeViewEvent) {
    // reset application locale before.
    setLocalizationContext(Locale(event.locale.substring(0, 2), event.locale.substring(3, 5)))
    // now try to connect to database
    try {
      connectToDatabase(event.username, event.password)
      startApplication() // create main window and menu
      if (welcomeView != null) {
        welcomeView = null
        removeAll()
      }
    } catch (e: SQLException) { // sets the error if any problem occur.
      welcomeView!!.setError(e.message)
    } finally { //push();
    }
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
    dBContext = login(getInitParameter("database")!!,
                      getInitParameter("driver")!!,
                      username,
                      password,
                      getInitParameter("schema")!!)
    // check if context is created
    if (dBContext == null) {
      throw SQLException(MessageCode.getMessage("VIS-00054"))
    } else {
      // set query trace level
      setTraceLevel()
    }
  }

  override val isNoBugReport: Boolean
    get() = java.lang.Boolean.parseBoolean(getInitParameter("nobugreport"))

  override val menu: VMenuTree
    get() = TODO()

  override var isGeneratingHelp: Boolean = false

  override var dBContext: DBContext? = null

  override val userName: String
    get() = dBContext!!.defaultConnection.userName

  override lateinit var defaultLocale: Locale

  override var localizationManager: LocalizationManager? = null

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
    if (registry != null) {
      registry.buildDependencies()
    }
    // set locale from initialization.
    // set locale from initialization.
    setLocalizationContext(Locale.FRANCE) // TODO
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
   * Sets the localization context.
   *
   * This aims to set the application [defaultLocale]
   * and [localizationManager] internal attributes.
   *
   */
  protected fun setLocalizationContext(locale: Locale) {
    // default application locale is initialized
    // from application descriptor file (web.xml)

    // default application locale is initialized
    // from application descriptor file (web.xml)
    defaultLocale = locale
    if (defaultLocale == null) {
      // if no valid local is defined in the application descriptor
      // pick the locale from the extra locale given with application
      // specifics.
      // This is only to be share that we start with a language.
      defaultLocale = alternateLocale
    }
    // Now create the localization manager using the application default locale.
    // Now create the localization manager using the application default locale.
    localizationManager = LocalizationManager(defaultLocale, Locale.getDefault())
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
    initialize()
    welcomeView = WelcomeView(defaultLocale, supportedLocales, sologanImage, logoImage, logoHref)
    welcomeView!!.setSizeFull() // important to get the full screen size.
    welcomeView!!.addWelcomeViewListener { event: WelcomeViewEvent -> onLogin(event) }
    add(welcomeView)
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

  companion object {

    /** Application instance */
    lateinit var instance: Application

    init {
      ApplicationContext.applicationContext = VApplicationContext()
      FileHandler.fileHandler = VFileHandler()
      ImageHandler.imageHandler = VImageHandler()
      WindowController.windowController = VWindowController()
      UIFactory.uiFactory = VUIFactory()
    }
  }
}
