/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Manidd Solutions GmbH, Wien AT
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

package org.kopi.galite.tests.report

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.domain.Domain
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report

class GroupTests {
  @Test
  fun groupTest() {
    val paysField = TestReport.pays
    val articleField = TestReport.article
    val numberField = TestReport.number
    val idField = TestReport.id

    assertEquals(paysField.group, null)
    assertEquals(paysField.groups, mutableListOf())
    assertEquals(paysField.groupID, -1)

    assertEquals(articleField.group, null)
    assertEquals(articleField.groups, mutableListOf())
    assertEquals(articleField.groupID, -1)

    assertEquals(idField.group, null)
    assertEquals(idField.groups, mutableListOf(paysField, articleField, numberField))
    assertEquals(idField.groupID, 2)
    assertEquals(idField.groupID, numberField.reportIndex)

  }

  object TestReport : Report() {
    override val locale = Locale.FRANCE
    override val title = "TestReport"
    override val reportCommands = true

    val pays = field(Domain<String>(20)) {
      label = "pays"
      help = "The user pays"
      align = FieldAlignment.CENTER
    }

    val article = field(Domain<String>(20)) {
      label = "premier article"
      help = "Article"
      align = FieldAlignment.CENTER
    }

    val number = field(Domain<Int>(55)) {
      label = "number"
      help = "number"
      align = FieldAlignment.CENTER
    }

    val id = field(Domain<Int>(6)) {
      label = "ID"
      help = "user identifier"
      align = FieldAlignment.CENTER
      group = pays
      group = article
      group = number
    }

    init {
      add {
        this[pays] = "Sami"
        this[article] = "premier article"
        this[id] = 1
      }
      add {
        this[pays] = "Sofia"
        this[article] = "deuxiem article"
        this[id] = 23
      }
    }
  }
}