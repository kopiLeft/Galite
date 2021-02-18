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

package org.kopi.galite.tests.db

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.AfterClass
import org.junit.BeforeClass
import org.kopi.galite.tests.TestBase
import org.kopi.galite.db.Modules
import org.kopi.galite.db.UserRights
import org.kopi.galite.db.Users
import org.kopi.galite.db.list_Of_Tables

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
    var connectedUser = testUser

    /**
     * Initializes the test
     */
    @BeforeClass
    @JvmStatic
    fun init() {
      Database.connect(testURL, testDriver, testUser, testPassword)
      transaction {
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
      transaction {
        exec("DROP ALL OBJECTS")
      }
    }

    /**
     * Creates DBSchema tables
     */
    fun createDBSchemaTables() {
      list_Of_Tables.forEach { table ->
        SchemaUtils.create(table)
      }
    }

    /**
     * Creates DBSchema tables
     */
    fun dropDBSchemaTables() {
      list_Of_Tables.forEach { table ->
        SchemaUtils.drop(table)
      }
    }

    /**
     * this test insert data into Module table
     */
    fun insertIntoModule(shortname: String,
                         source: String,
                         priorityNumber: Int,
                         parentName: String = "-1",
                         className: KClass<*>? = null,
                         symbolNumber: Int? = null) {
      Modules.insert {
        it[uc] = 0
        it[ts] = 0
        it[shortName] = shortname
        it[parent] = if (parentName != "-1") Modules.select { shortName eq parentName }.single()[id] else -1
        it[sourceName] = source
        it[priority] = priorityNumber
        it[objectName] = if (className != null) className.qualifiedName!! else null
        it[symbol] = symbolNumber
      }
    }

    /**
     * this test insert data into Users table
     */
    fun insertIntoUsers(shortname: String,
                        userName: String) {
      Users.insert {
        it[uc] = 0
        it[ts] = 0
        it[shortName] = shortname
        it[name] = userName
        it[character] = shortname
        it[active] = true
        it[createdOn] = DateTime.now()
        it[createdBy] = 1
        it[changedOn] = DateTime.now()
        it[changedBy] = 1
      }
    }

    /**
     * this test insert data into UserRights table
     */
    fun insertIntoUserRights(userName: String,
                             moduleName: String,
                             accessUser: Boolean) {
      UserRights.insert {
        it[ts] = 0
        it[module] = Modules.slice(Modules.id).select { Modules.shortName eq moduleName }.single()[Modules.id]
        it[user] = Users.slice(Users.id).select { Users.shortName eq userName }.single()[Users.id]
        it[access] = accessUser
      }
    }
  }

  /**
   * Connects to the database.
   */
  fun connectToDatabase(url: String = testURL,
                        driver: String = testDriver,
                        user: String = testUser,
                        password: String = testPassword) {
    Database.connect(url, driver = driver, user = user, password = password)
    connectedUser = user
  }


  /**
   * Initialises the database with creating the necessary tables and creates users.
   */
  open fun initDatabase(user: String = connectedUser) {
    transaction {
      createDBSchemaTables()

      insertIntoUsers(user, "administrator")
    }
  }
}
