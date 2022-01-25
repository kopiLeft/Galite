/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import java.math.BigDecimal

import kotlin.test.assertIs

import org.junit.Test
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.FormField
import org.kopi.galite.visual.form.VBooleanField
import org.kopi.galite.visual.form.VDateField
import org.kopi.galite.visual.form.VDecimalField
import org.kopi.galite.visual.form.VIntegerField
import org.kopi.galite.visual.form.VMonthField
import org.kopi.galite.visual.form.VStringField
import org.kopi.galite.visual.form.VTimeField
import org.kopi.galite.visual.form.VWeekField
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Week

class DomainTests {

    @Test
    fun buildFormFieldTest() {
        val block = Block("TestBlock", 1, 1)
        val intDomain = Domain<Int>(10, 10, 10)
        val intFormField = FormField(block, intDomain, 0, 0)

        block.initDomain(intDomain)
        assertIs<VIntegerField>(intDomain.buildFormFieldModel(intFormField))

        val stringDomain = Domain<String>(10, 10, 10)
        val stringFormField = FormField(block, stringDomain, 0, 0)

        block.initDomain(stringDomain)
        assertIs<VStringField>(stringDomain.buildFormFieldModel(stringFormField))

        val decimalDomain = Domain<BigDecimal>(10, 10, 10)
        val decimalFormField = FormField(block, decimalDomain, 0, 0)

        block.initDomain(decimalDomain)
        assertIs<VDecimalField>(decimalDomain.buildFormFieldModel(decimalFormField))

        val booleanDomain = Domain<Boolean>(10, 10, 10)
        val booleanFormField = FormField(block, booleanDomain, 0, 0)

        block.initDomain(booleanDomain)
        assertIs<VBooleanField>(booleanDomain.buildFormFieldModel(booleanFormField))

        val dateDomain = Domain<Date>(10, 10, 10)
        val dateFormField = FormField(block, dateDomain, 0, 0)

        block.initDomain(dateDomain)
        assertIs<VDateField>(dateDomain.buildFormFieldModel(dateFormField))

        val monthDomain = Domain<Month>(10, 10, 10)
        val monthFormField = FormField(block, monthDomain, 0, 0)

        block.initDomain(monthDomain)
        assertIs<VMonthField>(monthDomain.buildFormFieldModel(monthFormField))

        val weekDomain = Domain<Week>(10, 10, 10)
        val weekFormField = FormField(block, weekDomain, 0, 0)

        block.initDomain(weekDomain)
        assertIs<VWeekField>(weekDomain.buildFormFieldModel(weekFormField))

        val timeDomain = Domain<Time>(10, 10, 10)
        val timeFormField = FormField(block, timeDomain, 0, 0)

        block.initDomain(timeDomain)
        assertIs<VTimeField>(timeDomain.buildFormFieldModel(timeFormField))
    }
}
