package org.kopi.galite.tests.base

import org.junit.Test
import org.kopi.galite.base.ExtendedMessageFormat
import org.kopi.galite.tests.TestBase
import java.util.Locale
import kotlin.test.assertEquals

class ExtendedMessageFormatTests : TestBase() {
    /**
     * this test returns a formatted message after applying the regular expression
     */
    @Test
    fun formatMessageTest() {
        val formattedString = extendedMessageFormat.formatMessage(arrayOf("applications"))

        assertEquals("applications", formattedString)
    }

    private val extendedMessageFormat = ExtendedMessageFormat("^\\d+(\\.\\d+)?", Locale.CHINA)
}
