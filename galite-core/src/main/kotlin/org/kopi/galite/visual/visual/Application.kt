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

package org.kopi.galite.visual.visual

import java.util.Date
import java.util.Locale

import org.kopi.galite.visual.base.UComponent
import org.kopi.galite.visual.db.Connection
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.print.PrintManager

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
   * @return The [Connection] containing database connection information.
   */
  fun login(database: String, driver: String, username: String, password: String, schema: String?): Connection?

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
   * Displays a message box when we are not in a model context.
   * @param parent The parent component.
   * @param message The message to be displayed.
   */
  fun displayError(parent: UComponent?, message: String?)

  /**
   * `true` if no bug report is sent.
   */
  val isNoBugReport: Boolean

  /**
   * The start up time.
   */
  val startupTime: Date

  /**
   * The application menu.
   */
  val menu: VMenuTree?

  /**
   * `true` if the application in help generating  mode.
   */
  val isGeneratingHelp: Boolean

  /**
   * The [Connection] containing user connection information.
   */
  val dBConnection: Connection?

  /**
   * The connected user name.
   */
  val userName: String

  /**
   * The connected user IP address.
   */
  val userIP: String?

  /**
   * The application [Registry].
   */
  val registry: Registry

  /**
   * The application default [Locale].
   */
  val defaultLocale: Locale

  /**
   * The application [LocalizationManager].
   */
  val localizationManager: LocalizationManager?

  /**
   * The print manager of the application instance.
   */
  var printManager: PrintManager?

  /**
   * The printer manger of the application.
   */
  var printerManager: PrinterManager

  /**
   * The application configuration instance.
   */
  var applicationConfiguration: ApplicationConfiguration?
}
