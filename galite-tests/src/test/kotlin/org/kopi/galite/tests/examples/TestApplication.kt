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
package org.kopi.galite.tests.examples

import java.util.Locale

import com.vaadin.flow.router.Route

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.database.Connection
import org.kopi.galite.tests.common.GaliteRegistry
import org.kopi.galite.tests.database.connectToDatabase
import org.kopi.galite.tests.ui.vaadin.ConfigurationManager
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.ui.vaadin.visual.VApplication

@SpringBootApplication
open class TestApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
  connectToDatabase()
  transaction {
    initDatabase()
  }
  runApplication<TestApplication>(*args)
}

@Route("")
class GaliteApplication : VApplication(GaliteRegistry()) {
  override val sologanImage get() = "ui/vaadin/slogan.png"
  override val logoImage get() = "logo_galite.png"
  override val logoHref get() = "http://"
  override val alternateLocale get() = Locale.UK
  override val title get() = "Galite demo"
  override val supportedLocales
    get() =
      arrayOf(Locale.UK)

  override fun login(
    database: String,
    driver: String,
    username: String,
    password: String,
    schema: String?,
    maxRetries: Int?,
    waitMin: Long?,
    waitMax: Long?
  ): Connection? {
    return try {
      Connection.createConnection(url = database,
                                  driver = driver,
                                  userName = username,
                                  password = password,
                                  lookupUserId = true,
                                  schema = schema,
                                  maxRetries = maxRetries,
                                  waitMin = waitMin,
                                  waitMax = waitMax)
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
