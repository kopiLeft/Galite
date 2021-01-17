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
package org.kopi.galite.demo.web.vaadin

import java.util.Locale

import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.db.DBContext
import org.kopi.galite.tests.VApplicationTestBase
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.tests.form.FormSample
import org.kopi.galite.tests.form.FormWithFields
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.util.Rexec
import org.kopi.galite.visual.ApplicationConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
open class Application : SpringBootServletInitializer()

fun main(args: Array<String>) {
  DBApplication.connectToDatabase()
  DBApplication.initDatabase()
  DBApplication.initModules()
  DBApplication.initUserRights()
  runApplication<Application>(*args)
}

class GaliteApplication : VApplication(VApplicationTestBase.GaliteRegistry()) {
  override val sologanImage get() = "slogan.png"
  override val logoImage get() = "logo_kopi.png"
  override val logoHref get() = "http://"
  override val alternateLocale get() = Locale("de", "AT")
  override val supportedLocales
    get() =
      arrayOf(Locale.FRANCE,
              Locale("de", "AT"),
              Locale("ar", "TN"))

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

  init {
    ApplicationConfiguration.setConfiguration(
            object : ApplicationConfiguration() {
              override fun getVersion(): String = ""
              override fun getApplicationName(): String = ""
              override fun getInformationText(): String = ""
              override fun getLogFile(): String = ""
              override fun getDebugMailRecipient(): String = ""
              override fun mailErrors(): Boolean = false
              override fun logErrors(): Boolean = true
              override fun debugMessageInTransaction(): Boolean = true
              override fun getSMTPServer(): String = ""
              override fun getFaxServer(): String = ""
              override fun getDictionaryServer(): String = ""
              override fun getRExec(): Rexec = TODO()
              override fun getStringFor(var1: String): String = TODO()
              override fun getIntFor(var1: String): Int {
                val var2 = this.getStringFor(var1)
                return var2.toInt()
              }

              override fun getBooleanFor(var1: String): Boolean {
                return java.lang.Boolean.valueOf(this.getStringFor(var1))
              }

              override fun isUnicodeDatabase(): Boolean = false
              override fun useAcroread(): Boolean = TODO()
            }
    )
  }
}

object DBApplication : DBSchemaTest() {

  /**
   * Inserts the modules names to include in the application.
   */
  fun initModules() {
    transaction {
      insertIntoModule("2000", "org/kopi/galite/test/Menu", 10)
      insertIntoModule("1000", "org/kopi/galite/test/Menu", 10, "2000")
      insertIntoModule("2009", "org/kopi/galite/test/Menu", 90, "1000", FormSample::class)
      insertIntoModule("2010", "org/kopi/galite/test/Menu", 90, "1000", FormWithFields::class)
    }
  }

  /**
   * Adds rights to the [user] to access the application modules.
   */
  fun initUserRights(user: String = connectedUser) {
    transaction {
      insertIntoUserRights(user, "2000", true)
      insertIntoUserRights(user, "1000", true)
      insertIntoUserRights(user, "2009", true)
      insertIntoUserRights(user, "2010", true)
    }
  }
}
