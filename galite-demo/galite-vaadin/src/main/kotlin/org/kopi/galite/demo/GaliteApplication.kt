/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.demo

import java.util.Locale

import org.kopi.galite.database.Connection
import org.kopi.galite.visual.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.util.Rexec
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.PropertyException
import org.kopi.galite.visual.Registry

import com.vaadin.flow.router.Route

@Route("")
class GaliteApplication : VApplication(GaliteRegistry()) {
  override val sologanImage get() = "ui/vaadin/slogan.png"
  override val logoImage get() = "logo_galite.png"
  override val logoHref get() = "https://kopileft.github.io/Galite/"
  override val alternateLocale get() = Locale.UK
  override val title get() = "Galite demo"
  override val supportedLocales
    get() =
      arrayOf(Locale.UK, Locale.FRANCE)

  override fun login(
          database: String,
          driver: String,
          username: String,
          password: String,
          schema: String?,
          maxRetries: Int?
  ): Connection? {
    return try {
      Connection.createConnection(database, driver, username, password, true, schema, maxRetries)
    } catch (exception: Throwable) {
      null
    }
  }

  override val isNoBugReport: Boolean
    get() = true

  init {
    ApplicationConfiguration.setConfiguration(ConfigurationManager)
  }
}

object ConfigurationManager : ApplicationConfiguration() {
  override val isDebugModeEnabled: Boolean = true
  override val version get(): String = "1.0"
  override val applicationName get(): String = "Inventory app"
  override val informationText get(): String = "info"
  override val logFile get(): String = ""
  override val debugMailRecipient get(): String = ""
  override fun getSMTPServer(): String = ""
  override val faxServer get(): String = ""
  override val dictionaryServer get(): String = ""
  override fun mailErrors(): Boolean = false
  override fun logErrors(): Boolean = true
  override fun debugMessageInTransaction(): Boolean = true
  override val RExec get(): Rexec = TODO()
  override fun getStringFor(key: String): String {
    // In a real application you can read these properties from database.
    // Select from some table the value where property name equals to key.
    return when (key) {
      "debugging.mail.cc" -> "mail@adress"
      "debugging.mail.bcc" -> "mail@adress"
      "debugging.mail.sender" -> "mail@adress"
      else -> {
        throw PropertyException("Property $key not found")
      }
    }
  }
  override fun getIntFor(key: String): Int {
    val value = this.getStringFor(key)
    return value.toInt()
  }

  override fun getBooleanFor(var1: String): Boolean {
    return java.lang.Boolean.valueOf(this.getStringFor(var1))
  }

  override fun isUnicodeDatabase(): Boolean = false
  override fun useAcroread(): Boolean = TODO()
}

class GaliteRegistry : Registry("Galite", null)
