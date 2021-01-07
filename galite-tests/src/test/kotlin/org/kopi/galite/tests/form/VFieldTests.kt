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

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.form.VFieldException
import org.kopi.galite.form.VPosition
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VIntegerColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.MessageCode

class VFieldTests : JApplicationTestBase() {

  @Test
  fun checkListVStringFieldTest() {
    FormSample.model

    val vListColumn = VList("test",
                            "apps/common/Global", arrayOf(
                                VStringColumn("test", "NAME", 2, 50, true)),
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

    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "test"
    FormSample.tb1.age.value = 11
    FormSample.tb1.job.value = "job"

    assertFailsWith<VFieldException>(MessageCode.getMessage("VIS-00001")) {
      transaction {
        SchemaUtils.create(User)
        FormSample.tb1.vBlock.fields[3].validate()
      }
    }
  }

  @Test
  fun checkListVIntegerFieldTest() {
    FormSample.model

    val vListColumn = VList("test",
                            "apps/common/Global", arrayOf(
                                      VIntegerColumn("test", "AGE", 2, 50, true)),
                            1,
                            -1,
                            0,
                            0,
                            null,
                            false)

    FormSample.tb1.age.vField.setInfo("age",
                                      1,
                                      -1,
                                      0,
                                      intArrayOf(4, 4, 4),
                                      vListColumn,
                                      arrayOf(VColumn(0, "AGE", false, false, User.age)),
                                      0,
                                      0,
                                      null,
                                      VPosition(1, 1, 2, 2, -1),
                                      2,
                                      null)

    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "test"
    FormSample.tb1.age.value = 11
    FormSample.tb1.job.value = "job"

    assertFailsWith<VFieldException>(MessageCode.getMessage("VIS-00001")) {
      transaction {
        SchemaUtils.create(User)
        FormSample.tb1.vBlock.fields[5].validate()
      }
    }
  }
}
