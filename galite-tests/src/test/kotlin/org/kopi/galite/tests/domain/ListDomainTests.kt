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

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

import kotlin.test.assertEquals

import org.junit.Test

import org.kopi.galite.tests.form.*
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.ListDomain

/**
 * Contains tests of list-domain creation and manipulation
 */
class ListDomainTests : VApplicationTestBase() {

  val ListDomainExpression = ListDomainExpressionTest()

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
  fun `test generated ListDomain with ExpressionWithColumnType`() {
    // Declaration of the domain
    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 7
        it[name] = "SOULAIMA"
        it[age] = 26
        it[ts] = 0
        it[uc] = 0
        it[job] = "Ingénieur"
      }

      val firstUser = User.selectAll().map { Triple(it[User.name], it[User.job], it[User.age]) }.firstOrNull()

      // Affecte une valeur pour chaque field
      ListDomainExpression.blockListDomainExpressionTest.listNames.vField.selectFromList(false)
      ListDomainExpression.blockListDomainExpressionTest.listAges.vField.selectFromList(false)
      ListDomainExpression.blockListDomainExpressionTest.job.vField.selectFromList(false)

      // vérification des valeurs des fiels
      assertEquals(firstUser?.first, ListDomainExpression.blockListDomainExpressionTest.listNames.value)
      assertEquals(firstUser?.second, ListDomainExpression.blockListDomainExpressionTest.job.value)
      assertEquals(firstUser?.third?.minus(1), ListDomainExpression.blockListDomainExpressionTest.listAges.value)

      // vérification des valeurs des listes domains
      assertEquals("(\"USER\".UC + \"USER\".TS)", Names.columns[1].column.toString())
      assertEquals("(\"USER\".ID % 2)", Names.columns[2].column.toString())
      assert(AgesUsers.columns[0].column.toString().contains("AGE"))
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
