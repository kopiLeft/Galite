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

package org.kopi.galite.demo.desktop

import java.util.Locale

import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.form.dsl.Form
import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.tests.form.FormSample
import org.kopi.galite.tests.form.FormWithFields

val testLocale: Locale = Locale.FRANCE

/**
 * Entry point to run the application with a menu.
 */
fun main(args: Array<String>) {
  Application.connectToDatabase()
  Application.initDatabase()
  Application.initModules()
  Application.initUserRights()
  Application.run(args)
}

object Application : DBSchemaTest() {

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
    JApplicationTestBase.GaliteApplication().run(arguments)
  }

  /**
   * Used to run the application and show a specific form.
   */
  fun runForm(formName: Form, init: (Application.() -> Unit)? = null) {
    connectToDatabase()
    initDatabase()
    init?.invoke(this)
    run(formName)
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
}
