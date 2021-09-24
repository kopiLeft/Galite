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

package org.kopi.galite.tests.domain

import org.junit.Test
import org.kopi.galite.domain.CodeDomain

import kotlin.test.assertEquals

/**
 * Contains tests of code-domain creation and manipulation
 */
class CodeDomainTests {

  /**
   * Tests the creation of a domain of a type code.
   */
  @Test
  fun domainCodeTest() {
    // Declaration of the domain with codes
    class IntTestType : CodeDomain<Long>() {
      init {
        "cde1" keyOf 1L
        "cde2" keyOf 2L
      }
    }

    // Creating an instance of the domain IntTestType
    val domain = IntTestType()

    // test code values
    val codes = domain.codes
    assertEquals(2, codes.size)
    assertEquals("id$0", codes[0].ident)
    assertEquals("cde1", codes[0].label)
    assertEquals(1L, codes[0].value)
    assertEquals("id$1", codes[1].ident)
    assertEquals("cde2", codes[1].label)
    assertEquals(2L, codes[1].value)
  }
}
