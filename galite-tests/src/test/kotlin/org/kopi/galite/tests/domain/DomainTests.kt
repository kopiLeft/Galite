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
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.domain.Fixed
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.TEXT
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.FormField
import org.kopi.galite.visual.form.*
import org.kopi.galite.visual.type.*
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DomainTests {
    @Test
    fun buildFormFieldTest() {
        val block = FormBlock(1, 1, "TestBlock", "Test")

        val intDomain = Domain<Int>(10, 10, 10)
        val intFormField = FormField<Int>(block, intDomain, 0, 0)
        block.initDomain(intDomain)
        assertIs<VIntegerField>(intDomain.buildFormFieldModel(intFormField))

        val stringDomain = Domain<String>(10, 10, 10)
        val stringFormField = FormField<String>(block, stringDomain, 0, 0)
        block.initDomain(stringDomain)
        assertIs<VStringField>(stringDomain.buildFormFieldModel(stringFormField))

        val decimalDomain = Domain<Decimal>(10, 10, 10)
        val decimalFormField = FormField<Decimal>(block, decimalDomain, 0, 0)
        block.initDomain(decimalDomain)
        assertIs<VDecimalField>(decimalDomain.buildFormFieldModel(decimalFormField))

        val booleanDomain = Domain<Boolean>(10, 10, 10)
        val booleanFormField = FormField<Boolean>(block, booleanDomain, 0, 0)
        block.initDomain(booleanDomain)
        assertIs<VBooleanField>(booleanDomain.buildFormFieldModel(booleanFormField))

        val dateDomain = Domain<Date>(10, 10, 10)
        val dateFormField = FormField<Date>(block, dateDomain, 0, 0)
        block.initDomain(dateDomain)
        assertIs<VDateField>(dateDomain.buildFormFieldModel(dateFormField))

        val monthDomain = Domain<Month>(10, 10, 10)
        val monthFormField = FormField<Month>(block, monthDomain, 0, 0)
        block.initDomain(monthDomain)
        assertIs<VMonthField>(monthDomain.buildFormFieldModel(monthFormField))

        val weekDomain = Domain<Week>(10, 10, 10)
        val weekFormField = FormField<Week>(block, weekDomain, 0, 0)
        block.initDomain(weekDomain)
        assertIs<VWeekField>(weekDomain.buildFormFieldModel(weekFormField))

        val timeDomain = Domain<Time>(10, 10, 10)
        val timeFormField = FormField<Time>(block, timeDomain, 0, 0)
        block.initDomain(timeDomain)
        assertIs<VTimeField>(timeDomain.buildFormFieldModel(timeFormField))

    }
}

