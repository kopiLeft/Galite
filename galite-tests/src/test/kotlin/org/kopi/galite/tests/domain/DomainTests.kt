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
import org.kopi.galite.visual.field.Field
import org.kopi.galite.visual.field.Transformation.convertUpper
import org.kopi.galite.visual.exceptions.InvalidValueException

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Contains tests of domain creation and manipulation
 */
class DomainTests {

  /**
   * Tests the creation of a simple domain with length
   *
   * succeed if you respect domain length.
   * fails if you exceed domain length.
   */
  @Test
  fun simpleDomainWithLengthTest() {
    // Declaration of the domain with length
    class StringTestType : Domain<String>(5)

    // Creating a field with the domain StringTestType
    val field = Field(StringTestType())

    // test with a valid value
    val checkValid = field.checkLength("abcde")
    assertTrue(checkValid)

    // test with an invalid value
    val checkInvalid = field.checkLength("abcdef")
    assertFalse(checkInvalid)
  }

  /**
   * Tests the creation of a domain with check
   *
   * succeed if the value does respect the check method.
   * fails otherwise.
   */
  @Test
  fun domainWithCheckTest() {
    // Declaration of the domain with length
    class StringTestType(val param: String) : Domain<String>(5) {
      override val check = { value: String ->
        value.startsWith(param)
      }
    }

    // Creating a field with the domain StringTestType
    val field = Field(StringTestType("A"))

    // test with a valid value
    val checkValid = field.checkValue("Abcdef")
    assertTrue(checkValid)

    // test with an invalid value
    assertFailsWith<InvalidValueException> {
      field.checkValue("abcdef")
    }
  }

  /**
   * Tests the creation of a domain with check
   *
   * succeed if the is converted to uppercase.
   * fails otherwise.
   */
  @Test
  fun domainWithConvertUpperTest() {
    // Declaration of the domain with length
    class StringTestType : Domain<String>(5) {
      override val transformation = convertUpper()
    }

    // Creating a field with the domain StringTestType
    val field = Field(StringTestType())

    // test converted value
    val convertedToUpper = field.applyTransformation("Abcdef")
    assertEquals("ABCDEF", convertedToUpper)
  }

  /**
   * Tests the creation of a domain of a type code.
   */
  @Test
  fun domainCodeTest() {
    // Declaration of the domain with codes
    class IntTestType : Domain<Long>(5) {
      override val values = code {
        this["cde1"] = 1
        this["cde2"] = 2
      }
    }

    // Creating a field with the domain IntTestType
    val field = Field(IntTestType())

    // test code values
    val codes = field.getCodes()
    assertEquals(2, codes!!.size)
    assertEquals(1, codes["cde1"])
    assertEquals(2, codes["cde2"])
  }

  /**
   * Tests Domain of a type code with redundant code.
   */
  @Test
  fun domainRedundantCodeTest() {
    // Declaration of the domain with codes
    class IntTestType : Domain<Long>(5) {
      override val values = code {
        this["cde1"] = 1
        this["cde2"] = 2
        this["cde1"] = 7
      }
    }

    // Creating a field with the domain IntTestType
    val field = Field(IntTestType())

    // test code values
    val codes = field.getCodes()
    assertEquals(2, codes!!.size)
    assertEquals(7, codes["cde1"])
    assertEquals(2, codes["cde2"])
  }
}
