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
package org.kopi.galite.tests.form

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VFieldException
import org.kopi.galite.visual.form.VPosition
import org.kopi.galite.visual.list.VColumn
import org.kopi.galite.visual.list.VIntegerColumn
import org.kopi.galite.visual.list.VList
import org.kopi.galite.visual.list.VStringColumn
import org.kopi.galite.visual.visual.MessageCode

class VFieldTests : JApplicationTestBase() {
  @Test
  fun getListIDTest() {
    FormSample.model

    val vListColumn = VList("test",
                            "apps/common/Global",
                            arrayOf(VStringColumn("test", User.name, 2, 50, true)),
                            User,
                            null,
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
      assertEquals(1, listID)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun checkListVStringFieldTest() {
    FormSample.model

    val vListColumn = VList("test",
                            "apps/common/Global", arrayOf(
            VStringColumn("test", User.name, 2, 50, true)),
                            User,
                            null,
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
            VIntegerColumn("test", User.age, 2, 50, true)),
                            User,
                            null,
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

  @Test
  fun `getSearchCondition string field equal to value scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    FormSample.tb1.name.value = "name"
    val column : Column<*>? = FormSample.tb1.name.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.name.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".\"NAME\" = 'name'", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition string field equal to null scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    val column : Column<*>? = FormSample.tb1.name.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.name.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("null", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition string field like value scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    FormSample.tb1.name.value = "*name"
    val column : Column<*>? = FormSample.tb1.name.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.name.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".\"NAME\" LIKE '%name'", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition string field not like value scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    FormSample.tb1.name.vField.setSearchOperator(VConstants.SOP_NE)
    FormSample.tb1.name.value = "*name"
    val column : Column<*>? = FormSample.tb1.name.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.name.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".\"NAME\" NOT LIKE '%name'", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition upper string field not like value scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    FormSample.tb1.job.vField.setSearchOperator(VConstants.SOP_NE)
    FormSample.tb1.job.value = "*job"
    val column : Column<*>? = FormSample.tb1.job.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.job.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".JOB NOT LIKE UPPER('%job')", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition lower string field not like value scenario test`() {
    FormSample.model
    FormSample.tb1.cv.vField.setSearchOperator(VConstants.SOP_NE)
    FormSample.tb1.cv.value = "*cv"
    val column : Column<*>? = FormSample.tb1.cv.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.cv.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".\"CURRICULUM VITAE\" NOT LIKE LOWER('%cv')", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition int field equal to value scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    FormSample.tb1.id.value = 1
    val column : Column<*>? = FormSample.tb1.id.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.id.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".ID = 1", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition int field less than value scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    FormSample.tb1.id.vField.setSearchOperator(VConstants.SOP_LT)
    FormSample.tb1.id.value = 1
    val column : Column<*>? = FormSample.tb1.id.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.id.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".ID < 1", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition int field greater than value scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    FormSample.tb1.id.vField.setSearchOperator(VConstants.SOP_GT)
    FormSample.tb1.id.value = 1
    val column : Column<*>? = FormSample.tb1.id.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.id.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".ID > 1", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition int field less than or equal to value scenario test`() {
    FormSample.model
    FormSample.tb1.vBlock.clear()
    FormSample.tb1.id.vField.setSearchOperator(VConstants.SOP_LE)
    FormSample.tb1.id.value = 1
    val column : Column<*>? = FormSample.tb1.id.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.id.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".ID <= 1", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition int field greater than or equal to value scenario test`() {
    FormSample.model
    FormSample.tb1.id.vField.setSearchOperator(VConstants.SOP_GE)
    FormSample.tb1.id.value = 1
    val column : Column<*>? = FormSample.tb1.id.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.id.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".ID >= 1", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `getSearchCondition int not equal to value scenario test`() {
    FormSample.model
    FormSample.tb1.id.vField.setSearchOperator(VConstants.SOP_NE)
    FormSample.tb1.id.value = 1
    val column : Column<*>? = FormSample.tb1.id.vField.lookupColumn(User)
    val fieldSearchCondition = FormSample.tb1.id.vField.getSearchCondition(column!!)

    transaction {
      assertEquals("\"USER\".ID <> 1", fieldSearchCondition.toString())
    }
  }

  @Test
  fun `selectFromList valid scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[name] = "AUDREY"
        it[age] = 26
        it[ts] = 0
        it[uc] = 0
        it[job] = "job"
      }

      FormSample.tb4ToTestListDomain.listNames.vField.selectFromList(false)

      val userName = User.selectAll().single()[User.name]

      assertEquals(userName, FormSample.tb4ToTestListDomain.listNames.value)
      SchemaUtils.drop(User)
    }
  }
  @Ignore
  @Test
  fun `selectFromList valid scenario test with query entry`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[name] = "AUDREY"
        it[age] = 26
        it[ts] = 0
        it[uc] = 0
        it[job] = "job"
      }

      FormSample.tb4ToTestListDomain.listAges.vField.selectFromList(false)

      val userAge = User.selectAll().single()[User.age]

      assertEquals(userAge, FormSample.tb4ToTestListDomain.listAges.value)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `selectFromList scenario with exception test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      val error = Assert.assertThrows(VFieldException::class.java) { FormSample.tb4ToTestListDomain.listNames.vField.selectFromList(false) }

      assertEquals("VIS-00001: Data entry error: No matching value.", error.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `enumerateValue valid scenario test`() {
    FormSample.model
    FormSample.tb4ToTestListDomain.age.vField.enumerateValue(true)

    assertEquals(20, FormSample.tb4ToTestListDomain.age.value)
  }
}
