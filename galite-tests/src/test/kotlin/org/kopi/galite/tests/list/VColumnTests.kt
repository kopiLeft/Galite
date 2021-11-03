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

package org.kopi.galite.tests.list

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.visual.db.Users
import org.kopi.galite.visual.list.VColumn

class VColumnTests {
  @Test
  fun vColumnTest() {
    val vcolumn = VColumn(2, "testName", false, true, Users.id)

    assertEquals(2, vcolumn._getTable())
    assertEquals("T2.testName", vcolumn.getQualifiedName())
  }
}
