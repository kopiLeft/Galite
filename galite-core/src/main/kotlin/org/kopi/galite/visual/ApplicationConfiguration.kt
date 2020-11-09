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
import kotlin.jvm.Throws

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
  abstract fun getVersion(): String

  /**
   * Property app.name
   * Returns the application name
   */
  abstract fun getApplicationName(): String

  /**
   * Property app.comment
   * Returns the information text about this application
   */
  abstract fun getInformationText(): String

  // --------------------------------------------------------------
  //   Application Debugging
  // --------------------------------------------------------------

  /**
   * Property debug.logfile
   * Returns the failure file to add errors
   */
  abstract fun getLogFile(): String

  /**
   * Returns the debug mode (that you can change dynamically)
   */
  fun isDebugModeEnabled(): Boolean = false

  /**
   * Property debug.mail.recipient
   * Returns the mail recipient for failure messages
   *
   * For instance: failure.sys-admin@aHost.com
   */
  abstract fun getDebugMailRecipient(): String

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
  @Throws(PropertyException::class)
  abstract fun getSMTPServer(): String

  /**
   * Property fax.server
   * Returns the name of the fax server to use.
   */
  abstract fun getFaxServer(): String

  /**
   * Returns a RExec command handler
   */
  abstract fun getRExec(): Rexec

  // --------------------------------------------------------------
  //   Spell checking
  // --------------------------------------------------------------

  /**
   * Returns the url of the Dictionary Server e.g. c:/aspell
   */
  abstract fun getDictionaryServer(): String

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

  abstract fun getIntFor(key: String): Int // no more languages defined // no languages

  /**
   * returns options for a language
   */
  fun getDictionaryLanguages(): Array<Language> {
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
      // no more languages defined
    }
    return langs.toTypedArray()
  }

  /** @param    language   represents the name of the language
   *  @param    options    represents the language options
   */
  class Language(val language: String,
                 val options: String)

  /**
   * Returns a directory on the local machine for file generation
   * Use the user.home property because the path
   * of the home directory is OS dependant.
   */
  fun getDefaultDirectory(): File = File(System.getProperty("user.home"))

  // --------------------------------------------------------------
  // Preview with acroread
  // --------------------------------------------------------------
  open fun useAcroread(): Boolean = false

  // --------------------------------------------------------------
  // Database Encoding
  // --------------------------------------------------------------
  open fun isUnicodeDatabase(): Boolean = false

  // ----------------------------------------------------------------------
  // User configuration
  // ----------------------------------------------------------------------
  fun getUserConfiguration(): UserConfiguration? = null

  //---------------------------------------------------------------------
  // Window width
  //---------------------------------------------------------------------
  fun getDefaultModalWindowWidth(): Int = 0

  //---------------------------------------------------------------------
  // Window height
  //---------------------------------------------------------------------
  fun getDefaultModalWindowHeight(): Int = 0

  companion object {

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    const val STANDARD_PRINTER_NAME = "<Standard>"

    /**
     * A static instance that used to store an application configuration
     * when no application context is set
     */
    private var configuration: ApplicationConfiguration? = null

    fun getConfiguration(): ApplicationConfiguration? {
      return if (ApplicationContext.applicationContext != null) {
        ApplicationContext.applicationContext.getApplication().applicationConfiguration
      } else {
        configuration
      }
    }

    fun setConfiguration(conf: ApplicationConfiguration) {
      if (ApplicationContext.applicationContext != null) {
        ApplicationContext.applicationContext.getApplication().applicationConfiguration = conf
      } else {
        configuration = conf
      }
    }
  }
}
