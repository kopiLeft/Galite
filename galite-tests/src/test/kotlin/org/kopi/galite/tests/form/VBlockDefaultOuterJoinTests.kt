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
package org.kopi.galite.tests.form

import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.targetTables
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.visual.db.Modules
import org.kopi.galite.visual.db.UserRights
import org.kopi.galite.visual.db.Users
import org.kopi.galite.visual.form.VBlockDefaultOuterJoin

class VBlockDefaultOuterJoinTests : JApplicationTestBase() {

  val FormWithList = FormWithList()

  @Test
  fun getSearchTablesTest() {
    FormWithList.model
    val searchTables = VBlockDefaultOuterJoin.getSearchTables(FormWithList.blockWithManyTables.block)

    assertNotNull(searchTables)

    val tables = searchTables.targetTables()

    assertCollectionsEquals(listOf(Users, UserRights, Modules), tables)
    assertCollectionsEquals(Users.columns + UserRights.columns + Modules.columns,searchTables.columns)
  }

  @Test
  fun getFetchRecordConditionTest() {
    FormWithList.model
    val block = FormWithList.blockWithManyTables
    val fetchRecordCondition = VBlockDefaultOuterJoin.getFetchRecordCondition(block.block.fields)

    assertNotNull(fetchRecordCondition)

    transaction {
      val condition = buildString {
        append("(${block.r.nameInDb()}.${block.r.user.nameInDb()}")
        append(" = ${block.u.nameInDb()}.${block.u.id.nameInDb()})")
        append(" AND ")
        append("(${block.r.nameInDb()}.${block.r.module.nameInDb()}")
        append(" = ${block.m.nameInDb()}.")
        append("${block.m.id.nameInDb()})")
      }

      assertEquals(condition, fetchRecordCondition.toString())
    }
  }

  fun Table.nameInDb(): String {
    val identifierManager = TransactionManager.current().db.identifierManager

    return identifierManager.quoteIfNecessary(this.nameInDatabaseCase())
  }

  fun Column<*>.nameInDb(): String {
    val identifierManager = TransactionManager.current().db.identifierManager

    return identifierManager.quoteIfNecessary(this.nameInDatabaseCase())
  }
}
