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

import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.db.Users
import org.kopi.galite.form.VBlockDefaultOuterJoin
import org.kopi.galite.tests.JApplicationTestBase

class VBlockDefaultOuterJoinTests : JApplicationTestBase() {

  @Test
  fun getSearchTablesTest() {
    FormWithList.model
    val searchTables = VBlockDefaultOuterJoin.getSearchTables(FormWithList.block.vBlock)

    assertNotNull(searchTables)

    val tables = searchTables.selectAll().targets

    assertCollectionsEquals(arrayListOf(Users), tables)
    assertEquals(Users.columns,searchTables.columns)
  }

  @Test
  fun getFetchRecordConditionTest() {
    connectToDatabase()
    initDatabase()

    FormWithList.model
    val block = FormWithList.block
    val fetchRecordCondition = VBlockDefaultOuterJoin.getFetchRecordCondition(block.vBlock.fields)

    assertNotNull(fetchRecordCondition)

    transaction {
      val condition = buildString {
        append("(${block.r.nameInDatabaseCase()}.${block.r.user.nameInDatabaseCase()}")
        append(" = ${block.u.nameInDatabaseCase()}.${block.u.id.nameInDatabaseCase()})")
        append(" AND ")
        append("(${block.r.nameInDatabaseCase()}.${block.r.module.nameInDatabaseCase()}")
        append(" = \"MODULE\".")
        append("${block.m.id.nameInDatabaseCase()})")
      }

      assertEquals(condition, fetchRecordCondition.toString())
    }
  }
}
