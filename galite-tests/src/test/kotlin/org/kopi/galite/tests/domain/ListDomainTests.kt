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
import org.kopi.galite.domain.ListDomain
import org.kopi.galite.exceptions.InvalidValueException
import org.kopi.galite.report.ReportField
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
    class StringTestType : ListDomain<String>(20) {
      override val table = query(TestTable.selectAll())

      init {
        this["name"] =  TestTable.name
        this["id"] =    TestTable.id
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

  /**
   * Tests the creation of a domain with check
   *
   * succeed if the value does respect the check method.
   * fails with InvalidValueException otherwise.
   */
  @Test
  fun domainWithCheckTest() {
    // Declaration of the domain with length
    class StringTestType(val param: String) : ListDomain<String>(5) {
      override val table = query(TestTable.selectAll())
      init {
        convertUpper()

        check = {
          it.startsWith(param)
        }

        this["id"] =    TestTable.id
        this["name"] =  TestTable.name
      }
    }

    // Creating a field with the domain StringTestType
    val field = ReportField(StringTestType("A").also { it.kClass = String::class }, "", {})

    // test with a valid value
    val checkValid = field.checkValue("Abcdef")
    assertTrue(checkValid)

    // test with an invalid value
    assertFailsWith<InvalidValueException> {
      field.checkValue("abcdef")
    }
  }

  object TestTable : Table("TestTable") {
    val id = long("id")
    val name = varchar("name", length = 20)
  }
}
