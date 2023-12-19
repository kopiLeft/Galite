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

package org.kopi.galite.tests.database

import org.junit.AfterClass
import org.junit.BeforeClass

import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.database.Connection
import org.kopi.galite.database.Users
import org.kopi.galite.tests.common.TestBase

/**
 * Creates a connection and initializes the database. Useful if your test/demo needs a connection, the initial
 * structure with necessary tables and a user (it adds the administrator user in [Users] table).
 */
open class DBSchemaTest : TestBase() {

  companion object {
    const val testURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    const val testDriver = "org.h2.Driver"
    const val testUser = "admin"
    const val testPassword = "admin"
    lateinit var connection: Connection

    /**
     * Initializes the test
     */
    @BeforeClass
    @JvmStatic
    fun init() {
      connection = Connection.createConnection(testURL, testDriver, testUser, testPassword, false)
      transaction(connection.dbConnection) {
        createDBSchemaTables()
        insertIntoUsers(testUser, "administrator")
      }
    }

    /**
     * Resets the DB
     */
    @AfterClass
    @JvmStatic
    fun reset() {
      try {
        transaction(connection.dbConnection) {
          exec("DROP ALL OBJECTS")
        }
      } finally {
        connection.poolConnection.close()
      }
    }
  }
}
