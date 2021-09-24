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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.db.DBContext
import org.kopi.galite.demo.connectToDatabase
import org.kopi.galite.demo.vaadin.ConfigurationManager
import org.kopi.galite.tests.common.GaliteRegistry
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.ApplicationConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

import com.vaadin.flow.router.Route
import com.vaadin.flow.server.PWA

@SpringBootApplication
open class TestApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
  connectToDatabase()
  DBSchemaTest.reset()
  transaction {
    DBSchemaTest.createDBSchemaTables()
    DBSchemaTest.insertIntoUsers(DBSchemaTest.testUser, "administrator")
  }
  initData()
  initModules()
  runApplication<TestApplication>(*args)
}

@Route("")
@PWA(name = "Galite test", shortName = "Tests", iconPath = "favicon.png")
class GaliteApplication : VApplication(GaliteRegistry()) {
  override val sologanImage get() = "ui/vaadin/slogan.png"
  override val logoImage get() = "logo_galite.png"
  override val logoHref get() = "http://"
  override val alternateLocale get() = Locale.UK
  override val title get() = "Galite demo"
  override val supportedLocales
    get() =
      arrayOf(
        Locale.UK,
        Locale.FRANCE,
        Locale("de", "AT"),
        Locale("ar", "TN")
      )

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

  override val isNoBugReport: Boolean
    get() = true

  init {
    ApplicationConfiguration.setConfiguration(ConfigurationManager)
  }
}
