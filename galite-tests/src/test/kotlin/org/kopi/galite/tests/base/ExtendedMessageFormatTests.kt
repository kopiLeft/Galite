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

package org.kopi.galite.tests.base

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.common.TestBase
import org.kopi.galite.visual.base.ExtendedMessageFormat

class ExtendedMessageFormatTests : TestBase() {
    /**
     * this test returns a formatted message after applying the regular expression
     */
    @Test
    fun formatMessageTest() {
        val formattedString = extendedMessageFormat.formatMessage(arrayOf(7))

        assertEquals("Number: 7.", formattedString)
    }

    private val extendedMessageFormat = ExtendedMessageFormat("Number: {0,number,integer}.", Locale.US)
}
