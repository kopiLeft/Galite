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

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.tests.form.TestForm

val testURL = "jdbc:h2:mem:test"
val testDriver = "org.h2.Driver"
val testUser = "admin"
val testPassword = "admin"
val testLocale = Locale.FRANCE

fun main(args: Array<String>) {
  val dbTest =  DBSchemaTest()

  Database.connect(testURL, driver = testDriver, user = testUser, password = testPassword)
  transaction {
    dbTest.createDBSchemaTest()

    dbTest.insertIntoUsers("admin", "administrator")

    dbTest.insertIntoModule("2000", "org/kopi/galite/test/Menu", 10)
    dbTest.insertIntoModule("1000", "org/kopi/galite/test/Menu", 10, "2000")
    dbTest.insertIntoModule("2009",  "org/kopi/galite/test/Menu", 90, "1000", TestForm::class)

    dbTest.insertIntoUserRights("admin","2000" , true)
    dbTest.insertIntoUserRights("admin", "1000", true)
    dbTest.insertIntoUserRights("admin", "2009", true)

    val args = if (args.isNotEmpty()) {
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
              "-r",
              "-f", TestForm::class.qualifiedName

      )
    }

    JApplicationTestBase.GaliteApplication().run(args)
  }
}
