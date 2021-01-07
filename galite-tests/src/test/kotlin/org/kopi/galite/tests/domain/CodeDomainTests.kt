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

import org.junit.Test
import org.kopi.galite.domain.Domain
import org.kopi.galite.report.ReportField

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Contains tests of code-domain creation and manipulation
 */
class CodeDomainTests {

  /**
   * Tests the creation of a simple domain with length
   *
   * succeed if you respect domain length.
   * fails if you exceed domain length.
   */
  @Test
  fun simpleDomainWithLengthTest() {
    // Declaration of the domain with length
    class StringTestType : Domain<String>(5) {
      override val type = code {
        this["cde1"] = "1"
      }
    }

    // Creating a field with the domain StringTestType
    val field = ReportField(StringTestType().also { it.kClass = String::class }, "", {})

    // test with a valid value
    val checkValid = field.checkLength("abcde")
    assertTrue(checkValid)

    // test with an invalid value
    val checkInvalid = field.checkLength("abcdef")
    assertFalse(checkInvalid)
  }

  /**
   * Tests the creation of a domain of a type code.
   */
  @Test
  fun domainCodeTest() {
    // Declaration of the domain with codes
    class IntTestType : Domain<Long>(5) {
      override val type = code {
        this["cde1"] = 1L
        this["cde2"] = 2L
      }
    }

    // Creating an instance of the domain IntTestType
    val domain = IntTestType()

    // test code values
    val codes = domain.type.codes
    assertEquals(2, codes.size)
    assertEquals("id$0", codes[0].ident)
    assertEquals("cde1", codes[0].label)
    assertEquals(1L, codes[0].value)
    assertEquals("id$1", codes[1].ident)
    assertEquals("cde2", codes[1].label)
    assertEquals(2L, codes[1].value)
  }

  /**
   * Tests applying convertUpper on Code Domain
   *
   * must fails with UnsupportedOperationException because convertUpper
   * is used only with List Domain
   */
  @Test
  fun applyConvertUpperOnCodeDomainTest() {
    // Declaration of the domain with length
    class StringTestType : Domain<String>(5) {
      override val type = code {
        this["cde1"] = "1"
      }
    }

    // Creating an instance of the domain StringTestType
    val domain = StringTestType()

    // test converted value
    assertFailsWith<UnsupportedOperationException> {
      domain.applyConvertUpper("Abcdef")
    }
  }

  /**
   * Tests making check on Code Domain
   *
   * must fails with UnsupportedOperationException because convertUpper
   * is used only with List Domain
   */
  @Test
  fun applyConvertCheckOnCodeDomainTest() {
    // Declaration of the domain with length
    class StringTestType : Domain<String>(5) {
      override val type = code {
        this["cde1"] = "1"
      }
    }

    // Creating a field with the domain StringTestType
    val field = ReportField(StringTestType().also { it.kClass = String::class }, "", {})

    // test check
    assertFailsWith<UnsupportedOperationException> {
      field.checkValue("Abcdef")
    }
  }

}
