/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.tests

import org.kopi.galite.db.Connection

import org.kopi.galite.db.DBContext
import org.kopi.galite.ui.visual.VApplication
import org.kopi.galite.util.Rexec
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.Registry
import org.kopi.vkopi.lib.ui.swing.visual.JApplication
import java.util.*
import kotlin.collections.HashMap

/**
 * TestBase class for all tests.
 */
open class JApplicationTestBase : TestBase() {
  class GaliteRegistry: Registry("Galite", null)

  class GaliteApplication() : JApplication(GaliteRegistry()) {

    override fun login(
            database: String,
            driver: String,
            username: String,
            password: String,
            schema: String?
    ): DBContext? {
      return try {
        DBContext().apply {
          this.defaultConnection = this.createConnection(driver, database, username, password, true, schema)
        }
      } catch (exception: Throwable) {
        null
      }
    }

    override val dBContext: DBContext? = null

    override val isGeneratingHelp: Boolean = false

    override val isNoBugReport: Boolean
      get() = true

    init {
      ApplicationConfiguration.setConfiguration(
              object : ApplicationConfiguration() {
                override fun getVersion(): String {
                  return ""
                }

                override fun getApplicationName(): String {
                  return ""
                }

                override fun getInformationText(): String {
                  return ""
                }

                override fun getLogFile(): String {
                  return ""
                }

                override fun getDebugMailRecipient(): String {
                  return ""
                }

                override fun mailErrors(): Boolean {
                  return false
                }

                override fun logErrors(): Boolean {
                  return true
                }

                override fun debugMessageInTransaction(): Boolean {
                  return true
                }

                override fun getSMTPServer(): String {
                  return ""
                }

                override fun getFaxServer(): String {
                  return ""
                }

                override fun getDictionaryServer(): String {
                  return ""
                }

                override fun getRExec(): Rexec {
                  TODO()
                }

                override fun getStringFor(var1: String): String {
                  TODO()
                }

                override fun getIntFor(var1: String): Int {
                  val var2 = this.getStringFor(var1)
                  return var2.toInt()
                }

                override fun getBooleanFor(var1: String): Boolean {
                  return java.lang.Boolean.valueOf(this.getStringFor(var1))
                }

                override fun isUnicodeDatabase(): Boolean {
                  return false
                }

                override fun useAcroread(): Boolean {
                  TODO()
                }

                val PRP_REXEC = 2
                val PRP_LPR = 3
                val PRP_IPP = 4
                val STANDARD_TRAY = 1
                private val connection: Connection? = null
                private val propertyCache: HashMap<*, *>? = null
                private val commandLineProperties: Hashtable<String, String>? = null

              }

      )
    }
  }

  init {
    setupApplication()
  }

  fun setupApplication() {
    val app = GaliteApplication()
  }
}
