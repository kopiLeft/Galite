package org.kopi.galite.tests.domain

import org.junit.Test
import org.kopi.galite.domain.Domain
import org.kopi.galite.domain.Field
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
}
