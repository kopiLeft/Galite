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
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert.assertThrows
import org.junit.Test
import org.kopi.galite.db.Users
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VQueryNoRowException
import org.kopi.galite.form.VSkipRecordException
import org.kopi.galite.tests.examples.Center
import org.kopi.galite.tests.examples.FormToTestSaveMultipleBlock
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.examples.centerSequence
import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.type.Decimal
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VExecFailedException

class VBlockTests : JApplicationTestBase() {

  val FormWithList = FormWithList()

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

      assertCollectionsEquals(mutableListOf(
          mutableListOf("Fabienne BUGHIN", 25),
          mutableListOf("FABIENNE BUGHIN2", 23)
        ), deleteRecordList
      )
      SchemaUtils.drop(User)
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
  fun `test refreshLookup with no matching value in the table`() {
    FormWithList.model
    FormWithList.block.shortName[0] = "test"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
      transaction {
        FormWithList.block.vBlock.refreshLookup(0)
      }
    }

    assertEquals(MessageCode.getMessage("VIS-00016", FormWithList.block.m.tableName), vExecFailedException.message)
  }

  @Test
  fun refreshLookupTest() {
    FormWithList.model
    FormWithList.block.shortName[0] = "1000"

    transaction {
      Application.initModules()
      Application.initUserRights()
      FormWithList.block.vBlock.refreshLookup(0)
    }
  }

  @Test
  fun getSearchConditionsTest1() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    FormSample.model
    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "myName"
    FormSample.tb1.age.value = 6
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" = 'myName') AND " +
                "(\"USER\".AGE = 6) AND (\"USER\".JOB = 'jobValue')", blockSearchCondition.toString()
      )
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
    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "myName*"
    FormSample.tb1.age.value = 8
    FormSample.tb1.job.value = "jobValue"
    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()

    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE 'myName%') AND " +
                "(\"USER\".AGE < 8) AND (\"USER\".JOB < 'jobValue')", blockSearchCondition.toString()
      )
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

    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "*myName"
    FormSample.tb1.age.value = 9
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE '%myName') AND " +
                "(\"USER\".AGE > 9) AND (\"USER\".JOB > 'jobValue')", blockSearchCondition.toString()
      )
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
    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "my*Name"
    FormSample.tb1.age.value = 10
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE 'my%Name') AND " +
                "(\"USER\".AGE <= 10) AND (\"USER\".JOB <= 'jobValue')", blockSearchCondition.toString()
      )
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
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE '%') AND " +
                "(\"USER\".AGE >= 11) AND (\"USER\".JOB >= 'job')", blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchOrderTest() {
    FormWithList.model
    val orderBys = FormWithList.block3.vBlock.getSearchOrder()

    assertCollectionsEquals(arrayListOf(Users.name to SortOrder.ASC), orderBys)
  }

  @Test
  fun `fetchRecord valid scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
      }
      FormSample.tb1.vBlock.fetchRecord(1)
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.vBlock.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchRecord no such element test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)

      assertThrows(VSkipRecordException::class.java) {
        FormSample.tb1.vBlock.fetchRecord(1)
      }
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchRecord too many rows scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
      }
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
      }
      val error = assertThrows(AssertionError::class.java) { FormSample.tb1.vBlock.fetchRecord(1) }

      assertEquals("too many rows", error.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchNextRecord valid scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
        it[age] = 6
      }
      FormSample.tb1.vBlock.load()
      FormSample.tb1.vBlock.fetchNextRecord(0)
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.vBlock.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchNextRecord multi block scenario test`() {
    FormWithMultipleBlock.model
    val error = assertThrows(AssertionError::class.java) { FormWithMultipleBlock.multipleBlock.vBlock.fetchNextRecord(0)}

    assertEquals(FormWithMultipleBlock.multipleBlock.vBlock.name + " is a multi block", error.message)
  }

  @Test
  fun `fetchNextRecord exec failed scenario test`() {
    FormSample.model

    assertThrows(VExecFailedException::class.java) {
      FormSample.tb1.vBlock.fetchNextRecord(5)
    }
  }

  @Test
  fun `save insert simple block scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      SchemaUtils.createSequence(userSequence)

      FormSample.tb1.uc.value = 0
      FormSample.tb1.ts.value = 0
      FormSample.tb1.name.value = "Houssem"
      FormSample.tb1.age.value = 26
      FormSample.tb1.job.value = "job"

      FormSample.tb1.vBlock.setMode(VConstants.MOD_INSERT)
      FormSample.tb1.vBlock.save()

      val listInfoUser = mutableListOf<Any?>()

      User.selectAll().forEach {
        listInfoUser.add(it[User.id])
        listInfoUser.add(it[User.name])
        listInfoUser.add(it[User.age])
        listInfoUser.add(it[User.job])
      }
      assertEquals(listOf(1, FormSample.tb1.name.value, FormSample.tb1.age.value, FormSample.tb1.job.value), listInfoUser)
      SchemaUtils.drop(User)
      SchemaUtils.dropSequence(userSequence)
    }
  }

  @Test
  fun `save update simple block scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
        it[name] = "Houssem"
        it[age] = 26
        it[job] = "job"
      }

      FormSample.tb1.id.value = 1
      FormSample.tb1.uc.value = 0
      FormSample.tb1.ts.value = 0
      FormSample.tb1.name.value = "HADDAD"
      FormSample.tb1.age.value = 27
      FormSample.tb1.job.value = "work"

      FormSample.tb1.vBlock.setMode(VConstants.MOD_UPDATE)
      FormSample.tb1.vBlock.save()

      val listInfoUser = mutableListOf<Any?>()

      User.selectAll().forEach {
        listInfoUser.add(it[User.id])
        listInfoUser.add(it[User.name])
        listInfoUser.add(it[User.age])
        listInfoUser.add(it[User.job])
      }
      assertEquals(listOf(1, FormSample.tb1.name.value, FormSample.tb1.age.value, FormSample.tb1.job.value), listInfoUser)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `save insert multiple block scenario test`() {
    val form = FormToTestSaveMultipleBlock()

    form.model
    transaction {
      SchemaUtils.create(Training)
      SchemaUtils.create(Center)
      SchemaUtils.createSequence(centerSequence)
      Training.insert {
        it[id] = 1
        it[trainingName] = "trainingName"
        it[type] = 1
        it[price] = Decimal("1149.24").value
        it[active] = true
      }

      form.multipleBlock.ts[0] = 0
      form.multipleBlock.uc[0] = 0
      form.multipleBlock.trainingId[0] = 1
      form.multipleBlock.centerName[0] = "center 1"
      form.multipleBlock.address[0] = "adresse 1"
      form.multipleBlock.mail[0] = "center1@gmail.com"

      form.multipleBlock.ts[1] = 0
      form.multipleBlock.uc[1] = 0
      form.multipleBlock.trainingId[1] = 1
      form.multipleBlock.centerName[1] = "center 2"
      form.multipleBlock.address[1] = "adresse 2"
      form.multipleBlock.mail[1] = "center2@gmail.com"


      form.multipleBlock.vBlock.setMode(VConstants.MOD_INSERT)
      form.multipleBlock.vBlock.save()

      val listInfoCenter = mutableListOf<Any?>()

      Center.selectAll().forEach {
        listInfoCenter.add(it[Center.id])
        listInfoCenter.add(it[Center.refTraining])
        listInfoCenter.add(it[Center.centerName])
        listInfoCenter.add(it[Center.address])
        listInfoCenter.add(it[Center.mail])
      }

      assertEquals(listOf(5,
                          form.multipleBlock.trainingId[0],
                          form.multipleBlock.centerName[0],
                          form.multipleBlock.address[0],
                          form.multipleBlock.mail[0],
                          6,
                          form.multipleBlock.trainingId[1],
                          form.multipleBlock.centerName[1],
                          form.multipleBlock.address[1],
                          form.multipleBlock.mail[1],
      ),
                   listInfoCenter
      )
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `save update multiple block scenario test`() {
    val form = FormToTestSaveMultipleBlock()

    form.model
    transaction {
      SchemaUtils.create(Training)
      SchemaUtils.create(Center)
      SchemaUtils.createSequence(centerSequence)
      Training.insert {
        it[id] = 1
        it[trainingName] = "trainingName"
        it[type] = 1
        it[price] = Decimal("1149.24").value
        it[active] = true
      }
      Center.insert {
        it[id] = 1
        it[uc] = 0
        it[ts] = 0
        it[refTraining] = 1
        it[centerName] = "center 1"
        it[address] = "adresse 1"
        it[mail] = "center1@gmail.com"
      }
      Center.insert {
        it[id] = 2
        it[uc] = 0
        it[ts] = 0
        it[refTraining] = 1
        it[centerName] = "center 2"
        it[address] = "adresse 2"
        it[mail] = "center2@gmail.com"
      }

      form.multipleBlock.centerId[0] = 1
      form.multipleBlock.ts[0] = 0
      form.multipleBlock.uc[0] = 0
      form.multipleBlock.trainingId[0] = 1
      form.multipleBlock.centerName[0] = "center 111"
      form.multipleBlock.address[0] = "adresse 111"
      form.multipleBlock.mail[0] = "center111@gmail.com"

      form.multipleBlock.centerId[1] = 2
      form.multipleBlock.ts[1] = 0
      form.multipleBlock.uc[1] = 0
      form.multipleBlock.trainingId[1] = 1
      form.multipleBlock.centerName[1] = "center 222"
      form.multipleBlock.address[1] = "adresse 222"
      form.multipleBlock.mail[1] = "center222@gmail.com"

      form.multipleBlock.vBlock.setMode(VConstants.MOD_UPDATE)
      form.multipleBlock.vBlock.setRecordFetched(0, true)
      form.multipleBlock.vBlock.setRecordFetched(1, true)
      form.multipleBlock.vBlock.save()

      val listInfoCenter = mutableListOf<Any?>()

      Center.selectAll().forEach {
        listInfoCenter.add(it[Center.id])
        listInfoCenter.add(it[Center.refTraining])
        listInfoCenter.add(it[Center.centerName])
        listInfoCenter.add(it[Center.address])
        listInfoCenter.add(it[Center.mail])
      }

      assertEquals(listOf(1,
                          form.multipleBlock.trainingId[0],
                          form.multipleBlock.centerName[0],
                          form.multipleBlock.address[0],
                          form.multipleBlock.mail[0],
                          2,
                          form.multipleBlock.trainingId[1],
                          form.multipleBlock.centerName[1],
                          form.multipleBlock.address[1],
                          form.multipleBlock.mail[1]),
                   listInfoCenter
      )
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `delete simple block scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
        it[name] = "Houssem"
        it[age] = 26
        it[job] = "job"
      }
      var count = User.select { User.id eq 1 }.count()

      assertEquals(1, count)

      FormSample.tb1.id.value = 1
      FormSample.tb1.uc.value = 0
      FormSample.tb1.ts.value = 0
      FormSample.tb1.name.value = "Houssem"
      FormSample.tb1.age.value = 26
      FormSample.tb1.job.value = "job"

      FormSample.tb1.vBlock.setRecordFetched(0, true)
      FormSample.tb1.vBlock.delete()

      count = User.select { User.id eq 1 }.count()
      assertEquals(0, count)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `delete multiple block scenario test`() {
    val form = FormToTestSaveMultipleBlock()

    form.model
    transaction {
      SchemaUtils.create(Training)
      SchemaUtils.create(Center)
      SchemaUtils.createSequence(centerSequence)
      Training.insert {
        it[id] = 1
        it[trainingName] = "trainingName"
        it[type] = 1
        it[price] = Decimal("1149.24").value
        it[active] = true
      }
      Center.insert {
        it[id] = 1
        it[uc] = 0
        it[ts] = 0
        it[refTraining] = 1
        it[centerName] = "center 1"
        it[address] = "adresse 1"
        it[mail] = "center1@gmail.com"
      }
      Center.insert {
        it[id] = 2
        it[uc] = 0
        it[ts] = 0
        it[refTraining] = 1
        it[centerName] = "center 2"
        it[address] = "adresse 2"
        it[mail] = "center2@gmail.com"
      }
      Center.insert {
        it[id] = 3
        it[uc] = 0
        it[ts] = 0
        it[refTraining] = 1
        it[centerName] = "center 3"
        it[address] = "adresse 3"
        it[mail] = "center3@gmail.com"
      }
      var count = Center.selectAll().count()

      assertEquals(3, count)

      form.multipleBlock.centerId[0] = 1
      form.multipleBlock.ts[0] = 0
      form.multipleBlock.uc[0] = 0
      form.multipleBlock.trainingId[0] = 1
      form.multipleBlock.centerName[0] = "center 111"
      form.multipleBlock.address[0] = "adresse 111"
      form.multipleBlock.mail[0] = "center111@gmail.com"

      form.multipleBlock.centerId[1] = 2
      form.multipleBlock.ts[1] = 0
      form.multipleBlock.uc[1] = 0
      form.multipleBlock.trainingId[1] = 1
      form.multipleBlock.centerName[1] = "center 222"
      form.multipleBlock.address[1] = "adresse 222"
      form.multipleBlock.mail[1] = "center222@gmail.com"

      form.multipleBlock.vBlock.setMode(VConstants.MOD_UPDATE)
      form.multipleBlock.vBlock.setRecordFetched(0, true)
      form.multipleBlock.vBlock.setRecordFetched(1, true)
      form.multipleBlock.vBlock.delete()

      count = Center.selectAll().count()
      assertEquals(1, count)
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `getSearchColumns test`() {
    FormSample.model

    assertEquals(listOf(FormSample.tb1.id.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.ts.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.uc.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.name.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.age.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.job.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.cv.columns?.getColumnsModels()?.get(0)?.column),
                 FormSample.tb1.vBlock.getSearchColumns()
    )
  }

  @Test
  fun `load simple block scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
        it[name] = "Houssem"
        it[age] = 6
        it[job] = "job"
      }
      FormSample.tb1.vBlock.load()

      val listInfoUser = mutableListOf<Any?>()


      User.selectAll().forEach {
        listInfoUser.add(it[User.id])
        listInfoUser.add(it[User.ts])
        listInfoUser.add(it[User.uc])
        listInfoUser.add(it[User.name])
        listInfoUser.add(it[User.age])
        listInfoUser.add(it[User.job])
      }
      assertEquals(listOf(FormSample.tb1.id.value,
                          FormSample.tb1.ts.value,
                          FormSample.tb1.uc.value,
                          FormSample.tb1.name.value,
                          FormSample.tb1.age.value,
                          FormSample.tb1.job.value),
                   listInfoUser)
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.vBlock.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `load with set id value simple block scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
        it[name] = "Houssem"
        it[age] = 6
        it[job] = "job"
      }
      User.insert {
        it[id] = 2
        it[ts] = 0
        it[uc] = 0
        it[name] = "Hichem"
        it[age] = 27
        it[job] = "work"
      }

      FormSample.tb1.vBlock.clear()
      FormSample.tb1.id.value = 2
      FormSample.tb1.age.value = 27
      FormSample.tb1.vBlock.load()

      val listInfoUser = mutableListOf<Any?>()

      User.select { User.id eq 2 }.forEach {
        listInfoUser.add(it[User.id])
        listInfoUser.add(it[User.ts])
        listInfoUser.add(it[User.uc])
        listInfoUser.add(it[User.name])
        listInfoUser.add(it[User.age])
        listInfoUser.add(it[User.job])
      }

      assertEquals(listOf(FormSample.tb1.id.value,
                          FormSample.tb1.ts.value,
                          FormSample.tb1.uc.value,
                          FormSample.tb1.name.value,
                          FormSample.tb1.age.value,
                          FormSample.tb1.job.value),
                   listInfoUser)
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.vBlock.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `load no row exception scenario test`() {
    FormSample.model

    transaction {
      SchemaUtils.create(User)
      FormSample.tb1.id.value = 2
      FormSample.tb1.age.value = 27

      val error = assertThrows(VQueryNoRowException::class.java) { FormSample.tb1.vBlock.load() }

      assertEquals("VIS-00022: No matching value found.", error.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `load multiple block scenario test`() {
    val form = FormToTestSaveMultipleBlock()

    form.model
    transaction {
      SchemaUtils.create(Training)
      SchemaUtils.create(Center)
      SchemaUtils.createSequence(centerSequence)
      Training.insert {
        it[id] = 1
        it[trainingName] = "trainingName"
        it[type] = 1
        it[price] = Decimal("1149.24").value
        it[active] = true
      }
      Center.insert {
        it[id] = 1
        it[uc] = 0
        it[ts] = 0
        it[refTraining] = 1
        it[centerName] = "center 1"
        it[address] = "adresse 1"
        it[mail] = "center1@gmail.com"
      }
      Center.insert {
        it[id] = 2
        it[uc] = 0
        it[ts] = 0
        it[refTraining] = 1
        it[centerName] = "center 2"
        it[address] = "adresse 2"
        it[mail] = "center2@gmail.com"
      }
      Center.insert {
        it[id] = 3
        it[uc] = 0
        it[ts] = 0
        it[refTraining] = 1
        it[centerName] = "center 3"
        it[address] = "adresse 3"
        it[mail] = "center3@gmail.com"
      }
      form.multipleBlock.vBlock.clear()
      form.multipleBlock.trainingId[0] = 1
      form.multipleBlock.trainingId[1] = 1
      form.multipleBlock.trainingId[2] = 1
      form.multipleBlock.vBlock.load()

      val listInfoCenter = mutableListOf<Any?>()

      Center.selectAll().forEach {
        listInfoCenter.add(it[Center.id])
        listInfoCenter.add(it[Center.refTraining])
        listInfoCenter.add(it[Center.centerName])
        listInfoCenter.add(it[Center.address])
        listInfoCenter.add(it[Center.mail])
      }

      assertEquals(listOf(form.multipleBlock.centerId[0],
                          form.multipleBlock.trainingId[0],
                          form.multipleBlock.centerName[0],
                          form.multipleBlock.address[0],
                          form.multipleBlock.mail[0],
                          form.multipleBlock.centerId[1],
                          form.multipleBlock.trainingId[1],
                          form.multipleBlock.centerName[1],
                          form.multipleBlock.address[1],
                          form.multipleBlock.mail[1],
                          form.multipleBlock.centerId[2],
                          form.multipleBlock.trainingId[2],
                          form.multipleBlock.centerName[2],
                          form.multipleBlock.address[2],
                          form.multipleBlock.mail[2]),
                   listInfoCenter
      )
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }
}
