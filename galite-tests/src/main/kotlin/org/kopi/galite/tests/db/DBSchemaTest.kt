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
import kotlin.test.BeforeTest

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.joda.time.DateTime
import org.kopi.galite.tests.TestBase
import org.kopi.galite.db.Modules
import org.kopi.galite.db.UserRights
import org.kopi.galite.db.Users
import org.kopi.galite.db.list_Of_Tables

class DBSchemaTest : TestBase() {

  /**
   * this test create tables from DBSchema
   */
  @BeforeTest
  fun createDBSchemaTest() {
    list_Of_Tables.forEach { table ->
        SchemaUtils.create(table)
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
        it[parent] = if(parentName != "-1") Modules.select { shortName eq parentName }.single()[id] else -1
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
                           accessUser : Boolean) {
    UserRights.insert {
      it[ts] = 0
      it[module] = Modules.slice(Modules.id).select{ Modules.shortName eq  moduleName}.single()[Modules.id]
      it[user] = Users.slice(Users.id).select{ Users.shortName eq  userName}.single()[Users.id]
      it[access] = accessUser
    }
  }
}
