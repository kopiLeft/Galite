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
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Ignore
import org.junit.Test
import org.kopi.galite.db.Users
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.VExecFailedException

class VBlockTests : JApplicationTestBase() {

  @Test
  fun deleteRecordTest() {
    FormSample.model
    FormSample.tb1.id.value = 1

    transaction {

      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[name] = "AUDREY"
        it[age] = 23
        it[ts] = 0
        it[uc] = 0
      }
      User.insert {
        it[id] = 3
        it[name] = "Fabienne BUGHIN"
        it[age] = 25
        it[ts] = 0
        it[uc] = 0
      }
      User.insert {
        it[id] = 4
        it[name] = "FABIENNE BUGHIN2"
        it[age] = 23
        it[ts] = 0
        it[uc] = 0
      }

      val query = User.slice(User.name, User.age).selectAll()

      FormSample.tb1.vBlock.deleteRecord(0)
      val deleteRecordList = query.map {
        mutableListOf(it[User.name], it[User.age])
      }

      assertCollectionsEquals(deleteRecordList, mutableListOf(mutableListOf("Fabienne BUGHIN", 25),
                                                              mutableListOf("FABIENNE BUGHIN2", 23)))
    }
  }

  @Test
  fun checkUniqueIndexTest() {
    FormWithList.model
    FormWithList.block.uid[0] = 1
    FormWithList.block.name[0] = "administrator"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
      transaction {
        FormWithList.block.vBlock.checkUniqueIndices(0)
      }
    }
    assertEquals("VIS-00014: ID should be unique", vExecFailedException.message)
  }

  @Test
  @Ignore("TODO: this doesn't seem to be right")
  fun selectLookupTest() {
    FormWithList.model
    FormWithList.block3.ts[0] = 0
    FormWithList.block3.shortName[0] = "admin"
    FormWithList.block3.name[0] = "admin"
    FormWithList.block3.character[0] = "admin"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
      transaction {
        FormWithList.block3.vBlock.refreshLookup(0)
      }
    }

    assertEquals("VIS-00016: Aucune valeur appropriée dans KOPI_USERS.", vExecFailedException.message)
  }

  @Test
  fun getSearchConditionsTest1() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    FormSample.model
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "myName"
    FormSample.tb1.age.value = 6
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()
    transaction {
      assertEquals("(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" = 'myName') AND " +
                           "(\"USER\".AGE = 6) AND (\"USER\".JOB = 'jobValue')", blockSearchCondition.toString())
    }
  }

  @Test
  fun getSearchConditionsTest2() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    FormSample.model
    with(FormSample.tb1.vBlock) {
      fields[5].setSearchOperator(1)
      fields[6].setSearchOperator(1)
    }

    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "myName*"
    FormSample.tb1.age.value = 8
    FormSample.tb1.job.value = "jobValue"
    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()

    transaction {
      assertEquals("(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE 'myName%') AND " +
                           "(\"USER\".AGE < 8) AND (\"USER\".JOB < 'jobValue')", blockSearchCondition.toString())
    }
  }

  @Test
  fun getSearchConditionsTest3() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    FormSample.model
    with(FormSample.tb1.vBlock) {
      fields[5].setSearchOperator(2)
      fields[6].setSearchOperator(2)
    }
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "*myName"
    FormSample.tb1.age.value = 9
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()
    transaction {
      assertEquals("(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE '%myName') AND " +
                           "(\"USER\".AGE > 9) AND (\"USER\".JOB > 'jobValue')", blockSearchCondition.toString())
    }
  }

  @Test
  fun getSearchConditionsTest4() {

    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    FormSample.model
    with(FormSample.tb1.vBlock) {
      fields[5].setSearchOperator(3)
      fields[6].setSearchOperator(3)
    }
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "my*Name"
    FormSample.tb1.age.value = 10
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()
    transaction {
      assertEquals("(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE 'my%Name') AND " +
                           "(\"USER\".AGE <= 10) AND (\"USER\".JOB <= 'jobValue')", blockSearchCondition.toString())
    }
  }

  @Test
  fun getSearchConditionsTest5() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    FormSample.model
    with(FormSample.tb1.vBlock) {
      fields[5].setSearchOperator(4)
      fields[6].setSearchOperator(4)
    }
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "*"
    FormSample.tb1.age.value = 11
    FormSample.tb1.job.value = "job*Value"

    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()
    transaction {
      assertEquals("(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE '%') AND " +
                           "(\"USER\".AGE >= 11) AND (\"USER\".JOB >= 'job')", blockSearchCondition.toString())
    }
  }

  @Test
  fun getSearchOrder_Test() {
    FormWithList.model
    val orderBys = FormWithList.block3.vBlock.getSearchOrder()

    assertCollectionsEquals(arrayListOf(Users.name to SortOrder.ASC), orderBys)
  }
}
