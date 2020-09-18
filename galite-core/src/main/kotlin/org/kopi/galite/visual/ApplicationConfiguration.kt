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

import org.kopi.galite.util.Rexec
import java.io.File

/**
 * Manages Application configuration data
 */
abstract class ApplicationConfiguration {
  // --------------------------------------------------------------
  //   Application Properties
  // --------------------------------------------------------------
  /**
   * Property app.version
   * Returns the version of the application
   */
  abstract val version: String

  /**
   * Property app.name
   * Returns the application name
   */
  abstract val applicationName: String

  /**
   * Property app.comment
   * Returns the information text about this application
   */
  abstract val informationText: String
  // --------------------------------------------------------------
  //   Application Debugging
  // --------------------------------------------------------------
  /**
   * Property debug.logfile
   * Returns the failure file to add errors
   */
  abstract val logFile: String

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  val isDebugModeEnabled: Boolean
    get() = false

  /**
   * Property debug.mail.recipient
   * Returns the mail recipient for failure messages
   *
   * For instance: failure.sys-admin@aHost.com
   */
  abstract val debugMailRecipient: String

  abstract fun mailErrors(): Boolean

  abstract fun logErrors(): Boolean

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  abstract fun debugMessageInTransaction(): Boolean
  // --------------------------------------------------------------
  //   Application Properties
  // --------------------------------------------------------------
  /**
   * Property smtp.server
   * Returns the name of the SMTP server to use
   */
  abstract val sMTPServer: String

  /**
   * Property fax.server
   * Returns the name of the fax server to use.
   */
  abstract val faxServer: String

  /**
   * Returns a RExec command handler
   */
  abstract val rExec: Rexec
  // --------------------------------------------------------------
  //   Spell checking
  // --------------------------------------------------------------
  /**
   * Gets the url of the Dicionary Server e.g. c:/aspell
   */
  abstract val dictionaryServer: String
  // --------------------------------------------------------------
  //   Basic Methods
  // --------------------------------------------------------------
  /**
   * Reads the value of the property
   *
   * @return the property
   */
  abstract fun getStringFor(key: String): String?

  abstract fun getBooleanFor(key: String): Boolean

  abstract fun getIntFor(key: String): Int// no more languages definied// no languages

  /**
   * Gets options for a language
   */
  val dictionaryLanguages: Array<Language>
    get() {
      // no languages
      val langs = ArrayList<Language>()
      var lang: String?
      var i = 0
      try {
        while (getStringFor("aspell.$i.language").also { lang = it } != null) {
          langs.add(Language(lang!!, getStringFor("aspell.$i.options")!!))
          i++
        }
      } catch (e: PropertyException) {
        // no more languages definied
      }
      return langs.toTypedArray()
    }

  inner class Language(//name of the language
          val language: String, // options
          val options: String)

  /**
   * Returns a directory on the local machine for file generation
   * Use the user.home property because the path
   * of the home directory is OS dependant.
   */
  val defaultDirectory: File
    get() = File(System.getProperty("user.home"))

  // --------------------------------------------------------------
  // Preview with acroread
  // --------------------------------------------------------------
  fun useAcroread(): Boolean {
    return false
  }

  // --------------------------------------------------------------
  // Database Encoding
  // --------------------------------------------------------------
  val isUnicodeDatabase: Boolean
    get() = false

  // ----------------------------------------------------------------------
  // User configuration
  // ----------------------------------------------------------------------
  val userConfiguration: UserConfiguration?
    get() = null

  //---------------------------------------------------------------------
  // Window size
  //---------------------------------------------------------------------
  val defaultModalWindowWidth: Int
    get() = 0

  val defaultModalWindowHeight: Int
    get() = 0

  companion object {

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    const val STANDARD_PRINTER_NAME = "<Standard>"

    /**
     * A static instance that used to store an application configuration
     * when no application context is set
     */
    var configuration: ApplicationConfiguration? = null
      get() = if (ApplicationContext.getApplicationContext() != null) {
        ApplicationContext.getApplicationContext()?.application!!.applicationConfiguration
      } else {
        field
      }
      set(conf) {
        assert(conf != null) { "configuration must not be null" }
        if (ApplicationContext.getApplicationContext() != null) {
          ApplicationContext.getApplicationContext()?.application!!.applicationConfiguration = conf
        } else {
          field = conf
        }
      }
  }
}
