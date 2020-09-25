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
package org.kopi.galite.visual

import org.kopi.galite.db.DBContext
import org.kopi.galite.base.UComponent
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.print.PrintManager
import java.util.*

/**
 * `Application` is the top level interface for all applications.
 * The `Application` should give a way how to login to the application.
 */
interface Application : MessageListener {

  /**
   * Logins to the application.
   * @param database The database URL.
   * @param driver The database driver.
   * @param username The user name.
   * @param password The user password.
   * @param schema The database schema.
   * @return The [DBContext] containing database connection information.
   */
  fun login(database: String, driver: String, username: String, password: String, schema: String): DBContext

  /**
   * Signs out from the application.
   */
  fun logout()

  /**
   * Starts the application.
   */
  fun startApplication()

  /**
   * Returns `true` if an application quit is allowed.
   * @return `true` if an application quit is allowed.
   */
  fun allowQuit(): Boolean

  /**
   * Returns `true` if no bug report is sent.
   * @return `true if no bug report is sent.`
   */
  fun isNoBugReport(): Boolean

  /**
   * Returns the start up time.
   * @return The start up time.
   */
  fun getStartupTime(): Date?

  /**
   * Returns the application menu.
   * @return The application menu.
   */
  fun getMenu(): VMenuTree

  /**
   * Sets the application in help generating mode.
   */
  fun setGeneratingHelp()

  /**
   * Returns `true` if the application in help generating  mode.
   * @return `true` if the application in help generating  mode.
   */
  fun isGeneratingHelp(): Boolean

  /**
   * Returns the [DBContext] containing user connection information.
   * @return The [DBContext] containing user connection information.
   */
  fun getDBContext(): DBContext

  /**
   * Returns the connected user name.
   * @return The connected user name.
   */
  fun getUserName(): String

  /**
   * Returns the connected user IP address.
   * @return The connected user IP address.
   */
  fun getUserIP(): String

  /**
   * Returns the application [Registry].
   * @return The application [Registry].
   */
  fun getRegistry(): Registry

  /**
   * Returns the application default [Locale].
   * @return The application default [Locale].
   */
  fun getDefaultLocale(): Locale

  /**
   * Returns the application [LocalizationManager].
   * @return The application [LocalizationManager].
   */
  fun getLocalizationManager(): LocalizationManager

  /**
   * Displays a message box when we are not in a model context.
   * @param parent The parent component.
   * @param message The message to be displayed.
   */
  fun displayError(parent: UComponent, message: String)

  /**
   * Returns the print manager of the application instance.
   * @return The print manager of the application instance.
   */
  fun getPrintManager(): PrintManager

  /**
   * Sets the print manager to the application.
   * @param printManager The print manager instance.
   */
  fun setPrintManager(printManager: PrintManager)

  /**
   * Returns the printer manger of the application.
   * @return The printer manger of the application.
   */
  fun getPrinterManager(): PrinterManager

  /**
   * Sets the printer manager of the application instance.
   * @param printerManager The printer manager.
   */
  fun setPrinterManager(printerManager: PrinterManager)

  /**
   * Returns the application configuration instance.
   * @return The application configuration instance.
   */
  fun getApplicationConfiguration(): ApplicationConfiguration

  /**
   * Sets the application configuration.
   * @param configuration The application configuration instance.
   */
  fun setApplicationConfiguration(configuration: ApplicationConfiguration)
}
