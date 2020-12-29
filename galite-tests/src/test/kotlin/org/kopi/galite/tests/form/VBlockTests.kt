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

import org.jetbrains.exposed.sql.SortOrder
import org.kopi.galite.db.Users

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.VExecFailedException

class VBlockTests : JApplicationTestBase() {
  @Test
  fun getSearchOrder_Test() {
    FormWithList.model
    val orderBys = FormWithList.block3.vBlock.getSearchOrder_()

    assertCollectionsEquals(arrayListOf(Users.name to SortOrder.ASC), orderBys)
  }

  @Test
  fun checkUniqueIndexTest() {
    FormWithList.model
    FormWithList.block3.id[0] = 1
    FormWithList.block3.name[0] = "administrator"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
      transaction {
        FormWithList.block3.vBlock.checkUniqueIndices(0)
      }
    }
    assertEquals("VIS-00014: ID should be unique", vExecFailedException.message)
  }
}
