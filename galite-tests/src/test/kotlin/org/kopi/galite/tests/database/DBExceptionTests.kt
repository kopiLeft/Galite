/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
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

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import org.junit.Test

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.tests.common.ApplicationTestBase
import org.kopi.galite.database.DBNoRowException
import org.kopi.galite.database.DBTooManyRowsException
import org.kopi.galite.database.into

class DBExceptionTests : ApplicationTestBase() {

  /**
   * Tests select-into throws DBNoRowException and DBTooManyRowsException
   */
  @Test
  fun selectIntoTest() {
    transaction(connection.dbConnection) {
      try {
        SchemaUtils.create(Book)
        Book.insert {
          it[id] = 0
          it[title] = "b1"
        }
        Book.insert {
          it[id] = 1
          it[title] = "b1"
        }

        Book.selectAll().where { Book.id eq 0 }.into {
          assertEquals(0, it[Book.id])
          assertEquals("b1", it[Book.title])
        }

        assertFailsWith<DBNoRowException> {
          Book.selectAll().where { Book.id eq 2 }.into {}
        }

        assertFailsWith<DBTooManyRowsException> {
          Book.selectAll().where { Book.title eq "b1" }.into {}
        }
      } finally {
        SchemaUtils.drop(Book)
      }
    }
  }

  object Book: Table("Book") {
    val id = integer("id")
    val title = varchar("title", 20)
  }
}
