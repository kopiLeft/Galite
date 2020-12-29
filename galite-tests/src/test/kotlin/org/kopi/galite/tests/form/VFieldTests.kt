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
package org.kopi.galite.tests.form

import kotlin.test.Test

import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.form.VPosition
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.VCommand

class VFieldTests : JApplicationTestBase() {
  init {
    val vListColumn = VList("Benutzer",
            "apps/common/Global", arrayOf(
            VStringColumn(null, "NAME", 2, 50, true)),
            1,
            -1,
            0,
            0,
            null,
            false)

    FormSample.model.getBlock(0).fields[1].setInfo("name", 1, -1, 0, intArrayOf(4, 4, 4),
            vListColumn,
            arrayOf<VColumn?>(
                    VColumn(0, "NAME", false, false, User.name)
            ),
            0, 0, null as Array<VCommand>?, VPosition(1, 1, 2, 2, -1), 2,
            null)

    FormSample.model.getBlock(0).fields[1].list = vListColumn
  }

  @Test
  fun autoCompleteStartsWithTest() {
    transaction {
      FormSample.model.getBlock(0).fields[1].list!!.autocompleteType = 1
      var suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("AUD")
      var suggestionsResultList = suggestionsResult!!.map { it[0] }

      println("Starts with AUD")
      println(suggestionsResultList)

      var expectedList = mutableListOf("AUDREY")

      assertCollectionsEquals(expectedList, suggestionsResultList)
      suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("BarB")
      suggestionsResultList = suggestionsResult!!.map { it[0] }
      println("Starts with BarB")
      println(suggestionsResultList)

      expectedList = mutableListOf("BARBEY", "BARBEY1")

      assertCollectionsEquals(expectedList, suggestionsResultList)

      suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("BARBEY")
      suggestionsResultList = suggestionsResult!!.map { it[0] }
      println("Starts with BARBEY")
      println(suggestionsResultList)

      expectedList = mutableListOf("BARBEY", "BARBEY1")

      assertCollectionsEquals(expectedList, suggestionsResultList)

      suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("User")
      suggestionsResultList = suggestionsResult!!.map { it[0] }
      println("Starts with User")
      println(suggestionsResultList)

      expectedList = mutableListOf("USER0", "user1", "user2")

      assertCollectionsEquals(expectedList, suggestionsResultList)
    }
  }

  @Test
  fun autoCompleteContainsTest() {
    transaction {
      FormSample.model.getBlock(0).fields[1].list!!.autocompleteType = 2
      var suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("A")
      var suggestionsResultList = suggestionsResult!!.map { it[0] }

      println("Contains A")
      println(suggestionsResultList)
      var expectedList = mutableListOf("AUDREY", "BARBEY", "BARBEY1", "FABIENNE BUGHIN2", "Fabienne BUGHIN")

      assertCollectionsEquals(expectedList, suggestionsResultList)

      suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("s")
      suggestionsResultList = suggestionsResult!!.map { it[0] }
      println("Contains s")
      println(suggestionsResultList)

      expectedList = mutableListOf("USER0", "user1", "user2")

      assertCollectionsEquals(expectedList, suggestionsResultList)

      suggestionsResult = FormSample.model.getBlock(0).fields[1].getSuggestions("0")
      suggestionsResultList = suggestionsResult!!.map { it[0] }
      println("Contains 0")
      println(suggestionsResultList)
      expectedList = mutableListOf("USER0")
      assertCollectionsEquals(expectedList, suggestionsResultList)
    }
  }
}

