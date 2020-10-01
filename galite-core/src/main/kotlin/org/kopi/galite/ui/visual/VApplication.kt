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

import com.vaadin.flow.component.UI
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinServlet
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.visual.*
import java.util.*

/**
 * The entry point for all KOPI WEB applications.
 *
 * Creates a new `VApplication` instance
 *
 * @param registry The [Registry] object.
 */
@Route("")
abstract class VApplication() : Application {
  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  protected fun init() {
    initialize()
  }

  // ---------------------------------------------------------------------
  // MESSAGE LISTENER IMPLEMENTATION
  // ---------------------------------------------------------------------

  override fun getRegistry(): Registry {
    return registry
  }
  private lateinit var registry: Registry
  override fun notice(message: String) {
    TODO()
  }

  override fun error(message: String) {
    TODO()
  }

  override fun warn(message: String) {
    TODO()
  }

  override fun ask(message: String, yesIsDefault: Boolean): Int {
    TODO()
  }

  //---------------------------------------------------------------------
  // APPLICATION IMPLEMENTATION
  // ---------------------------------------------------------------------
  override fun logout() {
    TODO()
  }

  override fun startApplication() {
    TODO()
  }

  override fun allowQuit(): Boolean {
    TODO()
  }

  override fun getApplicationConfiguration(): ApplicationConfiguration = configuration!!

  override fun setApplicationConfiguration(configuration: ApplicationConfiguration) {
    this.configuration = configuration
  }

  /**
   * This methods is called at the beginning
   * you should use it to define [Locale], debugMode...
   */
  fun initialize() {
    //registry?.buildDependencies()
    // set locale from initialization.
    setLocalizationContext(initializationLocale)
    if (supportedLocales != null) {
      for (locale in supportedLocales!!) {
        UI.getCurrent().locale = locale
      }
    }
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
    localizationManager = LocalizationManager(defaultLocale!!, Locale.getDefault())
  }// obtain application locale from descriptor file
  // check the locale format
  /**
   * Returns the initialization locale found in the application descriptor file.
   * @return the initialization locale found in the application descriptor file.
   */
  protected val initializationLocale: Locale
    protected get() {
      val locale: String? = getInitParameter("locale") // obtain application locale from descriptor file
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
   * Checks the given locale format.
   * @param locale The locale to be checked.
   * @return `true` if the locale has a valid format.
   */
  private fun checkLocale(locale: String): Boolean {
    val chars: CharArray = locale.toCharArray()
    return !(chars.size != 5 || chars[0] < 'a' || chars[0] > 'z' || chars[1] < 'a' || chars[1] > 'z' || chars[2] != '_' || chars[3] < 'A' || chars[3] > 'Z' || chars[4] < 'A' || chars[4] > 'Z')
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
    return VaadinServlet.getCurrent().getInitParameter(key)
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  // --------------------------------------------------
  /**
   * Returns the supported locales that can be used with this application.
   * <pre>
   * The map will contain the displayed language as key value. Its corresponding
   * value is the locale ISO code.
  </pre> *
   * @return The supported locales that can be used with this application.
   */
  protected abstract val supportedLocales: Array<Locale>?

  /**
   * Returns the LOGO link to be associated with the application LOGO image.
   * @return The LOGO link to be associated with the application LOGO image.
   * @see {{@link .getLogoImage
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

  override fun getDefaultLocale(): Locale = defaultLocale!!

  override fun getLocalizationManager(): LocalizationManager = localizationManager!!
  //---------------------------------------------------
  // DATA MEMBEERS
  //---------------------------------------------------

  private var defaultLocale: Locale? = Locale.FRANCE
  private var localizationManager: LocalizationManager? = null
  private var configuration: ApplicationConfiguration? = null

  init {
    instance = this
  }

  companion object {
    lateinit var instance: Application
    init {
      ApplicationContext.applicationContext = VApplicationContext()
      UIFactory.uiFactory = VUIFactory()
    }
  }
}