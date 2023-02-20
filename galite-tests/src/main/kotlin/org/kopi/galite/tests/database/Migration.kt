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

import java.time.Instant

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.kopi.galite.database.Modules
import org.kopi.galite.database.UserRights
import org.kopi.galite.database.Users
import org.kopi.galite.database.list_Of_Tables
import org.kopi.galite.database.sequencesList

const val TEST_DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
const val TEST_DB_DRIVER = "org.h2.Driver"
const val TEST_DB_USER = "admin"
const val TEST_DB_USER_PASSWORD = "admin"

/**
 * Connects to the database.
 */
fun connectToDatabase(url: String = TEST_DB_URL,
                      driver: String = TEST_DB_DRIVER,
                      user: String = TEST_DB_USER,
                      password: String = TEST_DB_USER_PASSWORD
) {
  Database.connect(url, driver = driver, user = user, password = password)
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
 * Drops DBSchema tables
 */
fun dropDBSchemaTables() {
  list_Of_Tables.forEach { table ->
    SchemaUtils.drop(table)
  }
  sequencesList.forEach { sequence ->
    SchemaUtils.dropSequence(sequence)
  }
}

/**
 * Inserts data into [Users] table
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
    it[createdOn] = Instant.now()
    it[createdBy] = 1
    it[changedOn] = Instant.now()
    it[changedBy] = 1
  }
}

/**
 * Inserts data into [UserRights] table
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

/**
 * Inserts data into [Modules] table
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
  insertIntoUserRights(TEST_DB_USER, shortname, true)
}
