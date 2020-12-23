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

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.VExecFailedException

class VBlockTests : JApplicationTestBase() {
  @Test
  fun selectLookupTest() {

    FormWithList.model
    FormWithList.block2.id[0] = 0
    FormWithList.block2.name[0] = "administrator"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
      transaction {
        insertIntoUsers("admin", "administrator")
        FormWithList.block2.vBlock.refreshLookup(0)
      }
    }

    assertEquals("VIS-00016: Aucune valeur appropriée dans KOPI_USERS.", vExecFailedException.message)
  }
}
