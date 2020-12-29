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

import org.junit.Test
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.form.VFieldException
import org.kopi.galite.form.VPosition
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VCommand

class VFieldTests : JApplicationTestBase() {
  private val nameField = FormSample.model.getBlock(0).fields[1]

  @Test
  fun getCheckListVStringFieldTest1() {
    assertFailsWith<VFieldException>(MessageCode.getMessage("VIS-00001")) {
      transaction {
        nameField.checkList()
      }
    }
  }

@Test
  fun getCheckListVStringFieldTest2() {

    assertFailsWith<VFieldException>(MessageCode.getMessage("VIS-00001")) {
      transaction {
        User.insert {
          it[id] = 1
          it[name] = "AUDREY"
        }
        User.insert {
          it[id] = 11
          it[name] = "AUDREY"
        }
        User.insert {
          it[id] = 2
          it[name] = "Fabienne BUGHIN"
        }
        User.insert {
          it[id] = 3
          it[name] = "FABIENNE BUGHIN2"
        }
        nameField.checkList()
      }
    }
  }

  init {
    transaction {
      SchemaUtils.create(User)
    }
    FormSample.tb1.id.value = 1
    FormSample.tb1.name.value = "AUDREY"

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
            arrayOf(
                    VColumn(0, "NAME", false, false, User.name)
            ),
            0, 0, null as Array<VCommand>?, VPosition(1, 1, 2, 2, -1), 2,
            null)

    FormSample.model.getBlock(0).fields[1].list = vListColumn
  }
}
