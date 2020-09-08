package org.kopi.galite.tests.base

import org.junit.Test
import org.kopi.galite.base.ExtendedMessageFormat
import org.kopi.galite.tests.TestBase
import java.util.*

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExtendedMessageFormatTests :TestBase(){
    /**
     * this test does...
     */
    @Test
    fun formatMessageTest() {
        val formattedString = extendedMessageFormat.formatMessage(arrayOf("applications"))

        assertEquals("applications", formattedString)
    }

    private val extendedMessageFormat = ExtendedMessageFormat("^\\d+(\\.\\d+)?", Locale.CHINA)
}
