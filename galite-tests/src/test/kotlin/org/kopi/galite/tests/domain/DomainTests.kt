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
import org.kopi.galite.domain.Field
import org.kopi.galite.domain.Transfomation.convertUpper
import org.kopi.galite.domain.exceptions.InvalidValueException
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
        class StringTestType: Domain<String>(5)

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
     * succeed the value does respect the check method.
     * fails otherwise.
     */
    @Test
    fun dmainWithCheckTest() {
        // Declaration of the domain with length
        class StringTestType: Domain<String>(5) {
            override val check = { value: String ->
                value.startsWith("A")
            }
        }

        // Creating a field with the domain StringTestType
        val field = Field(StringTestType())

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
     * succeed the value does respect the check method.
     * fails otherwise.
     */
    @Test
    fun dmainWithConvertUpper() {
        // Declaration of the domain with length
        class StringTestType: Domain<String>(5) {
            override val transformation = convertUpper()
        }

        // Creating a field with the domain StringTestType
        val field = Field(StringTestType())

        // test converted value
        val convertedToUpper = field.convertUpper("Abcdef")
        assertEquals("ABCDEF", convertedToUpper)
    }
}
