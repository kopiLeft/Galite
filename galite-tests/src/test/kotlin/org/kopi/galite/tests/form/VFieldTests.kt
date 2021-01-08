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

import kotlin.test.assertFailsWith
import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.form.VFieldException
import org.kopi.galite.form.VPosition
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.MessageCode

class VFieldTests : JApplicationTestBase() {
  @Test
  fun getListIDTest() {
    FormSample.model

    val vListColumn = VList("test",
                            "apps/common/Global",
                            arrayOf(VStringColumn("test", "NAME", 2, 50, true)),
                            1,
                            -1,
                            0,
                            0,
                            null,
                            false)

    FormSample.tb1.name.vField.setInfo("name",
                                       1,
                                       -1,
                                       0,
                                       intArrayOf(4, 4, 4),
                                       vListColumn,
                                       arrayOf(VColumn(0, "NAME", false, false, User.name)),
                                       0,
                                       0,
                                       null,
                                       VPosition(1, 1, 2, 2, -1),
                                       2,
                                       null)
    FormSample.tb1.name.value = "name"
    assertFailsWith<VFieldException>(MessageCode.getMessage("VIS-00001")) {
      transaction {
        SchemaUtils.create(User)
        FormSample.tb1.name.vField.getListID()
      }
    }

    var listID : Int = -1
      transaction {
        User.insert {
          it[id] = 1
          it[name] = "name"
          it[age] = 23
          it[ts] = 0
          it[uc] = 0
        }
        listID = FormSample.tb1.name.vField.getListID()
      }

    assertEquals(1, listID)
  }
}
