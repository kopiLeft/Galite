/*
 * Copyright (c) 2013-2026 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2026 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.visual

import java.sql.SQLException
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

import org.slf4j.LoggerFactory

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.PreserveOnRefresh
import com.vaadin.flow.server.AppShellRegistry
import com.vaadin.flow.server.AppShellSettings
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.SessionDestroyEvent
import com.vaadin.flow.server.SessionInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener
import com.vaadin.flow.server.VaadinServlet
import com.vaadin.flow.server.VaadinSession
import com.vaadin.flow.shared.communication.PushMode

import org.kopi.galite.database.Configuration
import org.kopi.galite.database.Connection
import org.kopi.galite.visual.Application
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.ImageHandler
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.MessageListener
import org.kopi.galite.visual.PrinterManager
import org.kopi.galite.visual.PropertyException
import org.kopi.galite.visual.Registry
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.VMenuTree
import org.kopi.galite.visual.VerifyConfiguration
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.WindowController
import org.kopi.galite.visual.base.UComponent
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.print.PrintManager
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.accessAndAwait
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.accessAndPush
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.startAndWaitAndPush
import org.kopi.galite.visual.ui.vaadin.base.FontMetrics
import org.kopi.galite.visual.ui.vaadin.base.StyleManager
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.main.MainWindowListener
import org.kopi.galite.visual.ui.vaadin.notif.AbstractNotification
import org.kopi.galite.visual.ui.vaadin.notif.ConfirmNotification
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification
import org.kopi.galite.visual.ui.vaadin.notif.InformationNotification
import org.kopi.galite.visual.ui.vaadin.notif.NotificationListener
import org.kopi.galite.visual.ui.vaadin.notif.WarningNotification
import org.kopi.galite.visual.ui.vaadin.welcome.WelcomeView
import org.kopi.galite.visual.ui.vaadin.welcome.WelcomeViewEvent
import org.kopi.galite.visual.ui.vaadin.window.Window

/**
 * The entry point for all Galite WEB applications.
 *
 * @param registry The [Registry] object.
 */
@PreserveOnRefresh
@CssImport.Container(value = [
  CssImport("./styles/galite/styles.css"),
  CssImport("./styles/galite/common.css")
])
@Suppress("LeakingThis")
abstract class VApplication(override val registry: Registry) : VerticalLayout(), Application, MainWindowListener, HasDynamicTitle {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  internal var mainWindow: MainWindow? = null
  private var welcomeView: WelcomeView? = null
  internal var windowError: Throwable? = null // Sets the window error.
  private var askAnswer = 0
  lateinit var styleManager: StyleManager // the styles injector attached with this application instance.
  var currentUI: UI? = null
    get() = field ?: UI.getCurrent()

  // ---------------------------------------------------------------------
  // Failure cause informations
  // ---------------------------------------------------------------------

  override val startupTime: Date = Date() // remembers the startup time

  init {
    className = "galite"
    // registry and locale initialization
    initialize()
    gotoWelcomeView()
    askAnswer = MessageListener.AWR_UNDEF
    instance = this
  }

  override fun onAttach(attachEvent: AttachEvent) {
    val ui = attachEvent.ui

    currentUI = ui
    styleManager = StyleManager(currentUI!!)
    ui.element.style["--background-color"] = theme.backgroundColor
    ui.element.style["--background-hover-color"] = theme.backgroundHoverColor
    ui.element.style["--actor-hover-color"] = theme.actorHoverColor
    ui.element.style["--disabled-actor-color"] = theme.disabledActorColor
  }

  // ---------------------------------------------------------------------
  // MESSAGE LISTENER IMPLEMENTATION
  // ---------------------------------------------------------------------
  override fun notice(message: String) {
    val dialog = InformationNotification(VlibProperties.getString("Notice"), message, notificationLocale, mainWindow)
    val lock = Object()

    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(action: Boolean?) {
        BackgroundThreadHandler.releaseLock(lock)
      }
    })
    showNotification(dialog, lock)
  }

  override fun error(message: String?) {
    val dialog = ErrorNotification(VlibProperties.getString("Error"), message, notificationLocale, mainWindow)
    val lock = Object()

    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(action: Boolean?) {
        windowError = null // remove any further error.
        BackgroundThreadHandler.releaseLock(lock)
      }
    })
    showNotification(dialog, lock)
  }

  override fun warn(message: String) {
    val dialog = WarningNotification(VlibProperties.getString("Warning"), message, notificationLocale, mainWindow)
    val lock = Object()

    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(action: Boolean?) {
        BackgroundThreadHandler.releaseLock(lock)
      }
    })
    showNotification(dialog, lock)
  }

  /**
   * Displays a request dialog for a user interaction.
   * @param message The message to be displayed in the dialog box.
   */
  fun ask(message: String): Boolean {
    return ask(message, false) == MessageListener.AWR_YES
  }

  override fun ask(message: String, yesIsDefault: Boolean): Int {
    val dialog = ConfirmNotification(VlibProperties.getString("Question"), message, notificationLocale, mainWindow)
    val lock = Object()

    dialog.yesIsDefault = yesIsDefault
    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(yes: Boolean?) {
        askAnswer = if (yes == true) {
          MessageListener.AWR_YES
        } else {
          MessageListener.AWR_NO
        }
        BackgroundThreadHandler.releaseLock(lock)
      }
    })
    showNotification(dialog, lock)

    return askAnswer
  }

  private val notificationLocale get() = defaultLocale.toString()

  /**
   * Shows a notification.
   * @param notification The notification to be shown
   */
  protected open fun showNotification(notification: AbstractNotification) {
    access(currentUI) {
      notification.show()
    }
  }

  /**
   * Shows a notification.
   * @param notification The notification to be shown
   */
  protected open fun showNotification(notification: AbstractNotification, lock: Object) {
    startAndWaitAndPush(lock, currentUI) {
      notification.show()
    }
  }

  //---------------------------------------------------------------------
  // APPLICATION IMPLEMENTATION
  // ---------------------------------------------------------------------
  override fun logout() {
    val dialog = ConfirmNotification(VlibProperties.getString("Question"),
                                     Message.getMessage("confirm_quit"),
                                     notificationLocale,
                                     mainWindow)

    dialog.yesIsDefault = false
    dialog.addNotificationListener(object : NotificationListener {
      override fun onClose(yes: Boolean?) {
        if (yes == true) {
          closeConnection()
          // show welcome screen
          gotoWelcomeView()
        }
        currentUI?.push()
      }
    })
    showNotification(dialog)
  }

  override fun startApplication() {
    menu = VMenuTree(dBConnection)
    menu!!.setTitle(userName + "@" + url.substring(url.indexOf("//") + 2))
    mainWindow = MainWindow(defaultLocale, logoImage, logoHref, this)
    mainWindow!!.addMainWindowListener(this)
    mainWindow!!.setMainMenu(DMainMenu(menu!!))
    mainWindow!!.setUserMenu(DUserMenu(menu!!))
    mainWindow!!.setAdminMenu(DAdminMenu(menu!!))
    mainWindow!!.setBookmarksMenu(DBookmarkMenu(menu!!))
    mainWindow!!.setWorkspaceContextItemMenu(DBookmarkMenu(menu!!))
    mainWindow!!.connectedUser = userName
  }

  fun remove(mainWindow: MainWindow?) {
    // Remove main window from parent
    super.remove(mainWindow)

    // Close opened popups
    parent.ifPresent { body ->
      body.children
        .filter { component -> component is Dialog }
        .forEach {
          val dialog = (it as Dialog)
          if(dialog.isOpened) {
            dialog.close()
          }
        }
    }
  }

  override fun allowQuit(): Boolean =
    getInitParameter("allowQuit") == null ||
    java.lang.Boolean.parseBoolean(getInitParameter("allowQuit"))

  override var applicationConfiguration: ApplicationConfiguration? = null

  override var printManager: PrintManager? = null

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
      add(mainWindow)
    } catch (e: SQLException) { // sets the error if any problem occur.
      welcomeView!!.setError(e.message)
    } finally {
      currentUI?.push()
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
   * @see login
   */
  private fun connectToDatabase(username: String, password: String) {
    val database = getInitParameter("database")
    val driver = getInitParameter("driver")
    val schema = getInitParameter("schema")
    val maxRetires = getInitParameter("maxRetries")
    val waitMin = getInitParameter("waitMin")
    val waitMax = getInitParameter("waitMax")

    requireNotNull(database) { "The database url shouldn't be null" }
    requireNotNull(driver) { "The jdbc driver shouldn't be null" }

    dBConnection = login(database,
                         driver,
                         username,
                         password,
                         schema,
                         maxRetires?.toInt(),
                         waitMin?.toLong(),
                         waitMax?.toLong())
    // check if context is created
    if (dBConnection == null) {
      throw SQLException(MessageCode.getMessage("VIS-00054"))
    } else {
      // Add initiated database connection to vaadin session attributes.
      // This allows us to clean up / close opened database connections on session destroy event.
      addConnectionToSession(dBConnection!!)
      // set query trace level
      setTraceLevel()
    }
  }

  override val isNoBugReport: Boolean
    get() = java.lang.Boolean.parseBoolean(getInitParameter("nobugreport"))

  override var menu: VMenuTree? = null

  override var isGeneratingHelp: Boolean = false

  override var dBConnection: Connection? = null

  override val userName: String
    get() = dBConnection!!.userName

  override lateinit var defaultLocale: Locale

  override var localizationManager: LocalizationManager? = null

  override fun displayError(parent: UComponent?, message: String?) { error(message) }

  //---------------------------------------------------
  // UTILS
  // --------------------------------------------------

  /**
   * The database URL.
   */
  val url: String
    get() = dBConnection!!.url

  /**
   * This methods is called at the beginning
   * you should use it to define [Locale], debugMode...
   */
  fun initialize() {
    registry.buildDependencies()
    // set locale from initialization.
    setLocalizationContext(getInitializationLocale()) // TODO
  }

  /**
   * Verifies the configuration settings.
   */
  open fun verifyConfiguration() {
    val verifyConfiguration: VerifyConfiguration = VerifyConfiguration.verifyConfiguration
    try {
      verifyConfiguration.verifyConfiguration(
        ApplicationConfiguration.getConfiguration()!!.getSMTPServer(),
        ApplicationConfiguration.getConfiguration()!!.debugMailRecipient,
        ApplicationConfiguration.getConfiguration()!!.applicationName
      )
    } catch (e: PropertyException) {
      e.printStackTrace()
    }
  }

  /**
   * Add initiated connection on login to current vaadin session.
   * This allows us to clean up / close opened connections on session destroy event.
   *
   * @param connection Database connection initiated on application login.
   */
  fun addConnectionToSession(connection: Connection) {
    val session = VaadinSession.getCurrent()

    session.lock()
    try {
      // Get the existing list, or create a new one if null
      val connections = session.getAttribute("DB_CONNECTIONS") as? MutableList<Connection>
        ?: CopyOnWriteArrayList<Connection>().also {
          // CopyOnWriteArrayList is used to be thread-safe,
          // since multiple tabs/requests may access the list concurrently.
          session.setAttribute("DB_CONNECTIONS", it)
        }
      // Add the new connection to the session attribute.
      connections.add(connection)
    } finally {
      session.unlock()
    }
  }

  /**
   * Attaches a window to this application.
   * @param window The window to be added.
   */
  fun addWindow(window: Window, title: String) {
    if (mainWindow != null) {
      access(currentUI) {
        window.setSizeFull()
        mainWindow!!.addWindow(window, title)
      }
    }
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
    defaultLocale = locale
    // Now create the localization manager using the application default locale.
    localizationManager = LocalizationManager(defaultLocale, Locale.getDefault())

    // Set the locale for the current UI.
    UI.getCurrent()?.locale = defaultLocale
  }

  /**
   * Returns the initialization locale found in the application descriptor file.
   * @return the initialization locale found in the application descriptor file.
   */
  protected open fun getInitializationLocale(): Locale {
    val locale = getInitParameter("locale") // obtain application locale from descriptor file

    if (locale == null) {
      return alternateLocale
    }
    // check the locale format
    return if (!checkLocale(locale)) {
      System.err.println("Error: Wrong locale format. Alternate locale will be used")
      alternateLocale
    } else {
      Locale(locale.substring(0, 2), locale.substring(3, 5))
    }
  }

  /**
   * Sets the query trace level.
   */
  protected fun setTraceLevel() {

  }

  /**
   * Closes the database connection
   */
  fun closeConnection() {
    dBConnection?.poolConnection?.close()
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
    if (mainWindow != null) {
      mainWindow!!.resetTitle()
      // it should be detached to the application.
      remove(mainWindow)
      mainWindow = null
      menu = null
      localizationManager = null
      isGeneratingHelp = false
    }
    if (welcomeView == null) {
      welcomeView = WelcomeView(defaultLocale, supportedLocales, sologanImage, logoImage, logoHref)
      welcomeView!!.setSizeFull() // important to get the full screen size.
      welcomeView!!.addWelcomeViewListener { event: WelcomeViewEvent ->
        welcomeView!!.setWaitInfo()
        Thread {
          accessAndPush(currentUI) {
            try {
              onLogin(event)
            } finally {
              welcomeView?.unsetWaitInfo()
            }
          }
        }.start()
      }
      add(welcomeView)
    }
  }

  /**
   * Checks the given locale format.
   * @param locale The locale to be checked.
   * @return `true` if the locale has a valid format.
   */
  private fun checkLocale(locale: String): Boolean {
    val chars = locale.toCharArray()

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

  //---------------------------------------------------
  // MAIN WINDOW LISTENER IMPLEMENTATION
  // --------------------------------------------------
  override fun onAdmin() {
    // TODO
  }

  override fun onSupport() {
    // TODO
  }

  override fun onHelp() {
    // TODO
  }

  override fun onLogout() {
    // close database connection and show welcome view
    logout()
  }

  override fun onUser() {
    // TODO
  }

  override val userIP: String
    get() {
      var userIP = ""
      val currentSession = VaadinSession.getCurrent()

      if (currentSession != null) {
        return currentSession.browser.address
      } else {
        accessAndAwait(currentUI) {
          userIP = VaadinSession.getCurrent().browser.address
        }
      }

      return userIP
    }

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
  protected fun getInitParameter(key: String): String? {
    return VaadinServlet.getCurrent()?.getInitParameter(key) ?: Configuration.getString(key)
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

  /**
   * The page title.
   */
  open val title: String? = null

  /**
   * The theme is the colors used in the user interface.
   * The default theme is blue.
   */
  open val theme: Theme = Theme("#009bd4", "#d9f0f8", "#547988", "#82cfe9")

  override fun getPageTitle(): String? {
    return pageTitle
  }

  internal fun setPageTitle(title: String) {
    this.pageTitle = title
    currentUI!!.internals.title = title
  }

  private var pageTitle: String? = title

  companion object {

    /** Application instance */
    lateinit var instance: Application
    private val FONT_METRICS = arrayOf(FontMetrics.DIGIT, FontMetrics.LETTER)

    init {
      ApplicationContext.applicationContext = VApplicationContext()
      FileHandler.fileHandler = VFileHandler()
      ImageHandler.imageHandler = VImageHandler()
      WindowController.windowController = VWindowController()
      UIFactory.uiFactory = VUIFactory()
    }
  }
}

@Push(PushMode.MANUAL)
class GaliteAppShellConfigurator: AppShellConfigurator {

  override fun configurePage(settings: AppShellSettings) {
    settings.addFavIcon("icon", "favicon.png", "192x192")
  }
}

class ApplicationServiceInitListener: VaadinServiceInitListener {
  private val logger = LoggerFactory.getLogger("vaadin.session.lifecycle")

  override fun serviceInit(event: ServiceInitEvent) {
    val context  = event.source.context
    val appShellRegistry = AppShellRegistry.getInstance(context)

    // AppShellConfigurator is not discovered automatically by spring boot based applications
    // Because it's not located in the same package of the class annotated by @SpringBootApplication
    // This is a workaround to discover manually the AppShellConfigurator implementation.
    if(appShellRegistry.shell == null) {
      appShellRegistry.shell = GaliteAppShellConfigurator::class.java
    }

    event.source.addUIInitListener { uiInitEvent ->
      val loadingIndicatorConfiguration = uiInitEvent.ui.loadingIndicatorConfiguration
      loadingIndicatorConfiguration.firstDelay = 1000
    }

    // Add logging when a new vaadin session is initiated.
    event.source.addSessionInitListener(::onSessionInit)
    // Close all database connections opened within the current vaadin session.
    event.source.addSessionDestroyListener(::onSessionDestroy)
  }

  /**
   * Trace the created session ID when a new vaadin session is initiated.
   */
  fun onSessionInit(event: SessionInitEvent) {
    val session = event.session

    logger.info("${LocalDateTime.now()} - New Session [ID : ${session.session.id}] is created.")
  }

  /**
   * Close all database connections opened within the current vaadin session if not already closed.
   */
  fun onSessionDestroy(event: SessionDestroyEvent) {
    val session = event.session

    session.lock()
    try {
      val connections = session.getAttribute("DB_CONNECTIONS") as? List<Connection>

      connections?.forEach { connection ->
        try {
          // Close all opened connections in the current vaadin session.
          if (!connection.poolConnection.isClosed) {
            connection.poolConnection.close()
            logger.info("${LocalDateTime.now()} - DB connection ${connection.poolConnection} closed " +
                            "for session [ID : ${session.session.id}].")
          }
        } catch (ex: Exception) {
          logger.warn("${LocalDateTime.now()} - Failed to close connection ${connection.poolConnection} " +
                          "for session [ID : ${session.session.id}]", ex)
        }
      }
      // Clear the "DB_CONNECTIONS" attribute.
      session.setAttribute("DB_CONNECTIONS", null)
      // Session destroyed.
      logger.info("${LocalDateTime.now()} - Session [ID : ${session.session.id}] is destroyed.")
    } finally {
      session.unlock()
    }
  }
}
