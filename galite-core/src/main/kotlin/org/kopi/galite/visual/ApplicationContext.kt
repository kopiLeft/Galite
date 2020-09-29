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
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.base.InconsistencyException
import java.util.*

/**
 * `ApplicationContext` is a kopi application context that contains the
 * running [Application] instance in the context thread. The `ApplicationContext`
 * handles all shared applications components.
 */
abstract class ApplicationContext {
  //-----------------------------------------------------------
  // ABSTRACT METHODS
  //-----------------------------------------------------------
  /**
   * Returns the **current** [Application] instance.
   * @return The **current** [Application] instance.
   */
  abstract fun getApplication(): Application?

  /**
   * Returns the **current** [PreviewRunner] instance.
   * @return The **current** [PreviewRunner] instance.
   */
  abstract fun getPreviewRunner(): PreviewRunner

  /**
   * Returns `true` if we are in a web application context.
   * @return `true` if we are in a web application context.
   */
  abstract fun isWebApplicationContext(): Boolean

  companion object {

    //-----------------------------------------------------------
    // UTILS
    //-----------------------------------------------------------

    /**
     * Returns the default configuration of the Application
     */
    fun getDefaultd() {
      TODO()
    }

    /**
     * Returns the [Application] menu.
     * @return The [Application] menu.
     */
    fun getMenu(): VMenuTree {
      TODO()
    }

    /**
     * Returns the [LocalizationManager] instance.
     * @return The [LocalizationManager] instance.
     */
    fun getLocalizationManager(): LocalizationManager {
      TODO()
    }

    /**
     * Returns the default application [Locale].
     * @return The default application [Locale].
     */
    fun getDefaultLocale() {
      TODO()
    }


    /**
     * Returns the application [Registry].
     * @return the application [Registry].
     */
    fun getRegistry() {
      TODO()
    }

    /**
     * Returns the application [DBContext].
     * @return The application [DBContext].
     */
    fun getDBContext(): DBContext {
      TODO()
    }

    /**
     * Returns `true` if the [Application] should only generate help.
     * @return `true` if the [Application] should only generate help.
     */
    fun isGeneratingHelp() {
      TODO()
    }

    /**
     * Displays an error message outside a model context. This can happen when launching a module.
     * @param parent The parent component.
     * @param message The message to be displayed.
     */
    fun displayError() {
      TODO()
    }
    // ---------------------------------------------------------------------
    // SEND A BUG REPORT
    // ---------------------------------------------------------------------
    /**
     * Reports a trouble at execution time.
     *
     * @param     module          the module where the trouble was detected
     * @param     reason          the exception that triggered the bug report
     */
    fun reportTrouble(s: String, s1: String, message: String, e: InconsistencyException) {
      TODO()
    }

    /**
     * Write the network interfaces.
     * @param writer The Writer object.
     */
    private fun writeNetworkInterfaces() {
      TODO()
    }

    //-----------------------------------------------------------
    // DATA MEMBERS
    //-----------------------------------------------------------
    var applicationContext: ApplicationContext? = null
    var compt = 0
  }
}