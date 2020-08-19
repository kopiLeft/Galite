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

package org.kopi.galite.tests.domain

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.junit.Test
import org.kopi.galite.domain.Domain
import kotlin.test.assertEquals

/**
 * Contains tests of list-domain creation and manipulation
 */
class ListDomainTests {

  /**
   * Tests the creation of a simple list domain
   */
  @Test
  fun simpleDomainWithLengthTest() {
    // Declaration of the domain with length
    class StringTestType : Domain<String>(20) {
      override val type = list {
        query = TestTable.selectAll()

        this["name"] =  TestTable.name
        this["id"] =    TestTable.id
      }
    }

    // Creating an instance of the domain StringTestType
    val domain = StringTestType()

    // test list values
    val list = domain.getValues()
    assertEquals(2, list.size)
    assertEquals(TestTable.id, list["id"])
    assertEquals(TestTable.name, list["name"])
  }

  /**
   * Tests the creation of a domain with convertUpper.
   *
   * succeed if the is converted to uppercase.
   * fails otherwise.
   */
  @Test
  fun domainWithConvertUpperTest() {
    // Declaration of the domain with length
    class StringTestType : Domain<String>(5) {
      override val type = list {
        convertUpper()

        query = TestTable.selectAll()

        this["id"] =    TestTable.id
        this["name"] =  TestTable.name
      }
    }

    // Creating an instance of the domain StringTestType
    val domain = StringTestType()

    // test converted value
    val convertedToUpper = domain.applyConvertUpper("Abcdef")
    assertEquals("ABCDEF", convertedToUpper)
  }

  object TestTable : Table("TestTable") {
    val id = long("id")
    val name = varchar("name", length = 20)
  }
}
