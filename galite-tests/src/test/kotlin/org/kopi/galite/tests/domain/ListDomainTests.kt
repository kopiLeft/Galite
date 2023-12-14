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

package org.kopi.galite.tests.domain

import kotlin.test.assertEquals

import org.junit.Test

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.tests.form.*
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.ListDomain

/**
 * Contains tests of list-domain creation and manipulation
 */
class ListDomainTests : VApplicationTestBase() {

  val listDomainExpression = FormWithListDomains()

  /**
   * Tests the creation of a simple list domain
   */
  @Test
  fun simpleDomainWithLengthTest() {
    // Declaration of the domain with length
    class StringTestType : ListDomain<String>(20) {
      override val table = query(TestTable.selectAll())

      init {
        "name" keyOf TestTable.name
        "id" keyOf TestTable.id
      }
    }

    // Creating an instance of the domain StringTestType
    val domain = StringTestType()

    // test list values
    val list = domain.columns
    assertEquals(2, list.size)
    assertEquals(TestTable.name, list[0].column)
    assertEquals(TestTable.id, list[1].column)
  }

  @Test
  fun `test ListDomain with ExpressionWithColumnType`() {
    // Declaration of the domain
    transaction(connection.dbConnection) {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[name] = "John Doe"
        it[age] = 26
        it[ts] = 0
        it[uc] = 0
        it[job] = "myJob"
      }

      val firstUser = User.selectAll().map { Triple(it[User.name], it[User.job], it[User.age]) }.firstOrNull()

      // Select values from Listdomain
      listDomainExpression.userListBlock.name.vField.selectFromList(false)
      listDomainExpression.userListBlock.age.vField.selectFromList(false)
      listDomainExpression.userListBlock.job.vField.selectFromList(false)

      // Test fields values
      assertEquals(firstUser?.first, listDomainExpression.userListBlock.name.value)
      assertEquals(firstUser?.second, listDomainExpression.userListBlock.job.value)
      assertEquals(firstUser?.third?.minus(1), listDomainExpression.userListBlock.age.value)

      // Test Listdomain values
      assertEquals("(\"USER\".UC + \"USER\".TS)", Names.columns[1].column.toString())
      assertEquals("(\"USER\".ID % 2)", Names.columns[2].column.toString())
      assert(AgesUsers.columns[0].column.toString().split(".").last().compareTo("AGE") == 0)
      assertEquals("BIGINT", Jobs.columns[1].column.columnType.sqlType())
      assertEquals("(newUser.ID % 5)", Jobs.columns[2].column.toString())

      SchemaUtils.drop(User)
    }
  }

  object TestTable : Table("TestTable") {
    val id = long("id")
    val name = varchar("name", length = 20)
  }
}
