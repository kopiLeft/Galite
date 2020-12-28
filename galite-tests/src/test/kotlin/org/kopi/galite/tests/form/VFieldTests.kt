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
import org.kopi.galite.form.VFieldException
import org.kopi.galite.form.VPosition
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VCommand
import kotlin.test.assertFailsWith

class VFieldTests : JApplicationTestBase() {
  private val nameField = FormSample.model.getBlock(0).fields[1]
  private val ageField = FormSample.model.getBlock(0).fields[3]

  @Test
  fun getCheckListEmptyFieldTest() {
    assertFailsWith<VFieldException>(MessageCode.getMessage("VIS-00001")) {
      transaction {
        //checks if it exists in database ( at least one element )
        ageField.checkList()
      }
    }
  }

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
     object {
      init {
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
    //CheckList function works without exceptions in this case .
      transaction {
        nameField.checkList()
      }
  }

@Test
  fun getCheckListVStringFieldTest3() {
     object {
      init {
        val userTable = User
        transaction {
          SchemaUtils.create(User)
          userTable.insert {
            it[id] = 1
            it[name] = "AUDREY"
          }
        userTable.insert {
            it[id] = 11
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
    //result = if (lineCount == 0 && (newForm == null || !isNull(block!!.activeRecord)))
    assertFailsWith<VFieldException>(MessageCode.getMessage("VIS-00001")) {
      transaction {
        nameField.checkList()
      }
    }
  }

  companion object{
    init {
      FormSample.tb1.id.value = 1
      FormSample.tb1.name.value = "AUDREY"
      transaction {
        SchemaUtils.create(User)
      }
    }
  }

  init {
    val vListColumn = VList("Benutzer",
            "apps/common/Global", arrayOf<VListColumn?>(
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
