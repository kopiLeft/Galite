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

import kotlin.test.assertFailsWith
import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.db.Users
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.VExecFailedException

class VBlockTests : JApplicationTestBase() {

  @Test
  fun deleteRecordTest() {
    FormSample.model
    FormSample.tb1.id.value = 1

    transaction {

      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[name] = "AUDREY"
        it[age] = 23
        it[ts] = 0
        it[uc] = 0
      }
      User.insert {
        it[id] = 3
        it[name] = "Fabienne BUGHIN"
        it[age] = 25
        it[ts] = 0
        it[uc] = 0
      }
      User.insert {
        it[id] = 4
        it[name] = "FABIENNE BUGHIN2"
        it[age] = 23
        it[ts] = 0
        it[uc] = 0
      }

      val query = User.slice(User.name, User.age).selectAll()

      FormSample.tb1.vBlock.deleteRecord(0)
      val deleteRecordList = query.map {
        mutableListOf(it[User.name], it[User.age])
      }

      assertCollectionsEquals(deleteRecordList, mutableListOf(mutableListOf("Fabienne BUGHIN", 25),
                                                              mutableListOf("FABIENNE BUGHIN2", 23)))
    }
  }

  @Test
  fun getSearchOrder_Test() {
    FormWithList.model
    val orderBys = FormWithList.block3.vBlock.getSearchOrder_()

    assertCollectionsEquals(arrayListOf(Users.name to SortOrder.ASC), orderBys)
  }

  @Test
  fun checkUniqueIndexTest() {
    FormWithList.model
    FormWithList.block3.id[0] = 1
    FormWithList.block3.name[0] = "administrator"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
      transaction {
        FormWithList.block3.vBlock.checkUniqueIndices(0)
      }
    }
    assertEquals("VIS-00014: ID should be unique", vExecFailedException.message)
  }

  @Test
  fun selectLookupTest() {
    FormWithList.model
    FormWithList.block3.ts[0] = 0
    FormWithList.block3.shortName[0] = "admin"
    FormWithList.block3.name[0] = "admin"
    FormWithList.block3.character[0] = "admin"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
      transaction {
        FormWithList.block3.vBlock.refreshLookup(0)
      }
    }

    assertEquals("VIS-00016: Aucune valeur appropriée dans KOPI_USERS.", vExecFailedException.message)
  }
}
