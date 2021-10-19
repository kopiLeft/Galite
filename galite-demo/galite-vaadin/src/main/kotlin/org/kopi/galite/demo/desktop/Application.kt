/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.demo.desktop

import java.util.Locale

import org.kopi.galite.demo.ConfigurationManager
import org.kopi.galite.demo.GaliteRegistry
import org.kopi.galite.demo.database.connectToDatabase
import org.kopi.galite.demo.database.initDatabase
import org.kopi.galite.demo.database.initModules
import org.kopi.galite.demo.database.testDriver
import org.kopi.galite.demo.database.testPassword
import org.kopi.galite.demo.database.testURL
import org.kopi.galite.demo.database.testUser
import org.kopi.galite.visual.db.DBContext
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.visual.ApplicationConfiguration
import org.kopi.vkopi.lib.ui.swing.visual.JApplication

val testLocale: Locale = Locale.FRANCE

/**
 * Entry point to run the application with a menu.
 */
fun main(args: Array<String>) {
  connectToDatabase()
  initDatabase()
  run(args)
}

/**
 * Runs the application
 */
fun run(args: Array<String>) {
  val arguments = if (args.isNotEmpty()) {
    args
  } else {
    arrayOf("-d",
            testDriver,
            "-b",
            testURL,
            "-u",
            testUser,
            "-p",
            testPassword,
            "-l",
            testLocale.toString(),
            "-r"
    )
  }
  GaliteApplication().run(arguments)
}

/**
 * Runs the application with a specific form.
 */
fun run(formName: Form) {
  run(arrayOf(
    "-d",
    testDriver,
    "-b",
    testURL,
    "-u",
    testUser,
    "-p",
    testPassword,
    "-l",
    testLocale.toString(),
    "-r",
    "-f",
    formName::class.qualifiedName!!
  ))
}

/**
 * Used to run the application and show a specific form.
 */
fun runForm(formName: Form, init: (() -> Unit)? = null) {
  connectToDatabase()
  initDatabase()
  init?.invoke()
  run(formName)
}

class GaliteApplication : JApplication(GaliteRegistry()) {
  override fun login(
    database: String,
    driver: String,
    username: String,
    password: String,
    schema: String?
  ): DBContext? {
    val username = "admin"
    val password = "admin"
    return try {
      DBContext().apply {
        this.defaultConnection = this.createConnection(driver, database, username, password, true, schema)
      }
    } catch (exception: Throwable) {
      null
    }
  }

  override val dBContext: DBContext? = null
  override var isGeneratingHelp: Boolean = false
  override val isNoBugReport: Boolean
    get() = true

  override val defaultLocale: Locale
    get() = Locale.UK

  init {
    ApplicationConfiguration.setConfiguration(ConfigurationManager)
  }
}
