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

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.form.VPosition
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.VCommand

class VFieldTests : JApplicationTestBase() {
  // test with form sample
  private val nameField = FormSample.model.getBlock(0).fields[1]

  @Test
  fun getListIdTest1() {
    transaction {
      println(nameField.getListID())
    }
  }

  companion object {
    init {
      FormSample.tb1.id.value = 1
      FormSample.tb1.name.value = "AUDREY"
 //     FormSample.tb1.name.value = "Fabienne BUGHIN"
 //     FormSample.tb1.name.value = "FABIENNE BUGHIN2"

      val userTable = User
      transaction {
        SchemaUtils.create(User)
        userTable.insert {
          it[id] = 1
          it[name] = "AUDREY"
        }
        userTable.insert {
          it[id] = 2
          it[name] = "Fabienne BUGHIN"
        }
        userTable.insert {
          it[id] = 3
          it[name] = "FABIENNE BUGHIN2"
        }
      }
    }
  }
  init {
    val vListColumn = VList("Benutzer",
            "apps/common/Global", arrayOf<VListColumn>(
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
}