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
package org.kopi.galite.tests.form

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.tests.form.User.age
import org.kopi.galite.tests.form.User.name

class VBlockTests : JApplicationTestBase() {
  private val firstBlock = FormSample.model.getBlock(0)

  @Test
  fun deleteRecordTest1() {
    FormSample.tb1.id.value = 1
    FormSample.tb1.name.value = "myFirstName"
    FormSample.tb1.age.value = 1
    FormSample.tb1.name.value = "mySecondName"
    FormSample.tb1.age.value = 2
    FormSample.tb1.name.value = "myThirdName"
    FormSample.tb1.age.value = 3

    transaction {
      val query = User.slice(User.name, User.age).selectAll()
      val recordList = query.map {
        mutableListOf(it[name], it[age])
      }

      assertCollectionsEquals(recordList, mutableListOf(mutableListOf("AUDREY", 23),
              mutableListOf("Fabienne BUGHIN", 25),
              mutableListOf("FABIENNE BUGHIN2", 23)))

      firstBlock.deleteRecord(0)
      val deleteRecordList = query.map {
        mutableListOf(it[name], it[age])
      }
      assertCollectionsEquals(deleteRecordList, mutableListOf(mutableListOf("Fabienne BUGHIN", 25),
              mutableListOf("FABIENNE BUGHIN2", 23)))
    }
  }

  companion object {
    init {
      val userTable = User
      transaction {
        SchemaUtils.create(User)
        userTable.insert {
          it[id] = 1
          it[name] = "AUDREY"
          it[age] = 23
          it[ts] = 0
          it[uc] = 0
        }
        userTable.insert {
          it[id] = 3
          it[name] = "Fabienne BUGHIN"
          it[age] = 25
          it[ts] = 0
          it[uc] = 0
        }
        userTable.insert {
          it[id] = 4
          it[name] = "FABIENNE BUGHIN2"
          it[age] = 23
          it[ts] = 0
          it[uc] = 0
        }
      }
    }
  }
}
