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

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert.assertThrows
import org.junit.Ignore
import org.junit.Test
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.tests.examples.Center
import org.kopi.galite.tests.examples.FormToTestSaveMultipleBlock
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.examples.centerSequence
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.db.Users
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VQueryNoRowException
import org.kopi.galite.visual.form.VSkipRecordException
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.visual.MessageCode
import org.kopi.galite.visual.visual.VExecFailedException

class VBlockTests : VApplicationTestBase() {

  val FormWithList = FormWithList()
  val formMultiple = FormToTestSaveMultipleBlock()

  fun initSampleFormTables() {
    SchemaUtils.create(User)
    User.insert {
      it[id] = 1
      it[name] = "AUDREY"
      it[age] = 26
      it[ts] = 0
      it[uc] = 0
      it[job] = "job"
    }
    User.insert {
      it[id] = 3
      it[name] = "Fabienne BUGHIN"
      it[age] = 25
      it[ts] = 0
      it[uc] = 0
      it[job] = "job"
    }
    User.insert {
      it[id] = 4
      it[name] = "FABIENNE BUGHIN2"
      it[age] = 27
      it[ts] = 0
      it[uc] = 0
      it[job] = "job"
    }
  }

  fun initMultipleBlockFormTables() {
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
  }

  @Test
  fun deleteRecordTest() {
    val FormSample = FormSample().also { it.model.reset() }

    FormSample.tb1.id.value = 1

    transaction {
      initSampleFormTables()
      FormSample.tb1.vBlock.load()
      val query = User.slice(User.name, User.age).selectAll()

      FormSample.tb1.vBlock.deleteRecord(0)
      val deleteRecordList = query.map {
        mutableListOf(it[User.name], it[User.age])
      }

      assertCollectionsEquals(mutableListOf(
          mutableListOf("Fabienne BUGHIN", 25),
          mutableListOf("FABIENNE BUGHIN2", 27)
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
    val FormSample = FormSample().also { it.model }

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
                "(\"USER\".AGE = 6) AND (UPPER(\"USER\".JOB) = 'jobValue')", blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchConditionsTest2() {
    val FormSample = FormSample().also { it.model }
    with(FormSample.tb1.vBlock) {
      fields[5].setSearchOperator(VConstants.SOP_LT)
      fields[6].setSearchOperator(VConstants.SOP_NE)
    }
    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "myName*"
    FormSample.tb1.age.value = 8
    FormSample.tb1.job.value = "jobValue*"
    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()

    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE 'myName%') AND " +
                "(\"USER\".AGE < 8) AND (UPPER(\"USER\".JOB) NOT LIKE UPPER('jobValue%'))", blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchConditionsTest3() {
    val FormSample = FormSample().also { it.model }
    with(FormSample.tb1.vBlock) {
      fields[5].setSearchOperator(VConstants.SOP_GT)
      fields[6].setSearchOperator(VConstants.SOP_EQ)
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
                "(\"USER\".AGE > 9) AND (UPPER(\"USER\".JOB) = 'jobValue')", blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchConditionsTest4() {
    val FormSample = FormSample().also { it.model }
    with(FormSample.tb1.vBlock) {
      fields[5].setSearchOperator(VConstants.SOP_LE)
      fields[6].setSearchOperator(VConstants.SOP_EQ)
    }
    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "my*Name"
    FormSample.tb1.age.value = 10
    FormSample.tb1.job.value = "*jobValue"

    val blockSearchCondition = FormSample.tb1.vBlock.getSearchConditions()
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE 'my%Name') AND " +
                "(\"USER\".AGE <= 10) AND (UPPER(\"USER\".JOB) LIKE UPPER('%jobValue'))", blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchConditionsTest5() {
    val FormSample = FormSample().also { it.model }
    with(FormSample.tb1.vBlock) {
      fields[5].setSearchOperator(VConstants.SOP_GE)
      fields[6].setSearchOperator(VConstants.SOP_EQ)
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
                "(\"USER\".AGE >= 11) AND (UPPER(\"USER\".JOB) LIKE UPPER('job%Value'))", blockSearchCondition.toString()
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
  fun `fetchRecord with existing ID scenario test`() {
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()
      //fetch record search with id 1 and there is no exception
      FormSample.tb1.vBlock.fetchRecord(1)
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.vBlock.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchRecord no such element test`() {
    val FormSample = FormSample().also { it.model }
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
    val FormSample = FormSample().also { it.model }
    transaction {
      SchemaUtils.create(User)
      initSampleFormTables()
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
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()
      FormSample.tb1.name.value = "AUDREY"
      FormSample.tb1.age.value = 26
      FormSample.tb1.vBlock.load()
      FormSample.tb1.vBlock.fetchNextRecord(0)

      val query = User.select { User.name.eq("AUDREY") and User.age.eq(26) }.single()
      val listInfoUser = listOf(query[User.id], query[User.name], query[User.age], query[User.job])

      assertEquals(listOf(1, FormSample.tb1.name.value, FormSample.tb1.age.value, FormSample.tb1.job.value), listInfoUser)
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
    val FormSample = FormSample().also { it.model }
    assertThrows(VExecFailedException::class.java) {
      FormSample.tb1.vBlock.fetchNextRecord(3)
    }
  }

  @Test
  fun `save insert simple block scenario test`() {
    val FormSample = FormSample().also { it.model }
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

      val query = User.selectAll().single()
      val listInfoUser = listOf(query[User.id], query[User.name], query[User.age], query[User.job])

      assertEquals(listOf(1, FormSample.tb1.name.value, FormSample.tb1.age.value, FormSample.tb1.job.value), listInfoUser)
      SchemaUtils.drop(User)
      SchemaUtils.dropSequence(userSequence)
    }
  }

  @Test
  fun `save update simple block scenario test`() {
    val FormSample = FormSample().also { it.model }
    transaction {
      SchemaUtils.create(User)
      initSampleFormTables()

      FormSample.tb1.id.value = 1
      FormSample.tb1.uc.value = 0
      FormSample.tb1.ts.value = 0
      FormSample.tb1.name.value = "HADDAD"
      FormSample.tb1.age.value = 27
      FormSample.tb1.job.value = "work"

      FormSample.tb1.vBlock.setMode(VConstants.MOD_UPDATE)
      FormSample.tb1.vBlock.save()

      val query = User.select { User.id eq 1 }.single()
      val listInfoUser = listOf(query[User.id], query[User.name], query[User.age], query[User.job])

      assertEquals(listOf(1, FormSample.tb1.name.value, FormSample.tb1.age.value, FormSample.tb1.job.value), listInfoUser)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `save insert multiple block scenario test`() {
    formMultiple.model
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

      formMultiple.multipleBlock.ts[0] = 0
      formMultiple.multipleBlock.uc[0] = 0
      formMultiple.multipleBlock.trainingId[0] = 1
      formMultiple.multipleBlock.centerName[0] = "center 1"
      formMultiple.multipleBlock.address[0] = "adresse 1"
      formMultiple.multipleBlock.mail[0] = "center1@gmail.com"

      formMultiple.multipleBlock.ts[1] = 0
      formMultiple.multipleBlock.uc[1] = 0
      formMultiple.multipleBlock.trainingId[1] = 1
      formMultiple.multipleBlock.centerName[1] = "center 2"
      formMultiple.multipleBlock.address[1] = "adresse 2"
      formMultiple.multipleBlock.mail[1] = "center2@gmail.com"


      formMultiple.multipleBlock.vBlock.setMode(VConstants.MOD_INSERT)
      formMultiple.multipleBlock.vBlock.save()

      val listInfoCenter = mutableListOf<Any?>()

      Center.selectAll().forEach {
        listInfoCenter.add(it[Center.id])
        listInfoCenter.add(it[Center.refTraining])
        listInfoCenter.add(it[Center.centerName])
        listInfoCenter.add(it[Center.address])
        listInfoCenter.add(it[Center.mail])
      }

      assertEquals(listOf(formMultiple.multipleBlock.centerId[0],
                          formMultiple.multipleBlock.trainingId[0],
                          formMultiple.multipleBlock.centerName[0],
                          formMultiple.multipleBlock.address[0],
                          formMultiple.multipleBlock.mail[0],
                          formMultiple.multipleBlock.centerId[1],
                          formMultiple.multipleBlock.trainingId[1],
                          formMultiple.multipleBlock.centerName[1],
                          formMultiple.multipleBlock.address[1],
                          formMultiple.multipleBlock.mail[1]) as List<Any?>,
                   listInfoCenter
      )
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `save update multiple block scenario test`() {
    formMultiple.model
    transaction {
      initMultipleBlockFormTables()
      formMultiple.multipleBlock.centerId[0] = 1
      formMultiple.multipleBlock.ts[0] = 0
      formMultiple.multipleBlock.uc[0] = 0
      formMultiple.multipleBlock.trainingId[0] = 1
      formMultiple.multipleBlock.centerName[0] = "center 111"
      formMultiple.multipleBlock.address[0] = "adresse 111"
      formMultiple.multipleBlock.mail[0] = "center111@gmail.com"

      formMultiple.multipleBlock.centerId[1] = 2
      formMultiple.multipleBlock.ts[1] = 0
      formMultiple.multipleBlock.uc[1] = 0
      formMultiple.multipleBlock.trainingId[1] = 1
      formMultiple.multipleBlock.centerName[1] = "center 222"
      formMultiple.multipleBlock.address[1] = "adresse 222"
      formMultiple.multipleBlock.mail[1] = "center222@gmail.com"

      formMultiple.multipleBlock.centerId[2] = 3
      formMultiple.multipleBlock.ts[2] = 0
      formMultiple.multipleBlock.uc[2] = 0
      formMultiple.multipleBlock.trainingId[2] = 1
      formMultiple.multipleBlock.centerName[2] = "center 333"
      formMultiple.multipleBlock.address[2] = "adresse 333"
      formMultiple.multipleBlock.mail[2] = "center333@gmail.com"

      formMultiple.multipleBlock.vBlock.setMode(VConstants.MOD_UPDATE)
      formMultiple.multipleBlock.vBlock.setRecordFetched(0, true)
      formMultiple.multipleBlock.vBlock.setRecordFetched(1, true)
      formMultiple.multipleBlock.vBlock.setRecordFetched(2, true)
      formMultiple.multipleBlock.vBlock.save()

      val listInfoCenter = mutableListOf<Any?>()

      Center.selectAll().forEach {
        listInfoCenter.add(it[Center.id])
        listInfoCenter.add(it[Center.refTraining])
        listInfoCenter.add(it[Center.centerName])
        listInfoCenter.add(it[Center.address])
        listInfoCenter.add(it[Center.mail])
      }

      assertEquals(listOf(formMultiple.multipleBlock.centerId[0],
                          formMultiple.multipleBlock.trainingId[0],
                          formMultiple.multipleBlock.centerName[0],
                          formMultiple.multipleBlock.address[0],
                          formMultiple.multipleBlock.mail[0],
                          formMultiple.multipleBlock.centerId[1],
                          formMultiple.multipleBlock.trainingId[1],
                          formMultiple.multipleBlock.centerName[1],
                          formMultiple.multipleBlock.address[1],
                          formMultiple.multipleBlock.mail[1],
                          formMultiple.multipleBlock.centerId[2],
                          formMultiple.multipleBlock.trainingId[2],
                          formMultiple.multipleBlock.centerName[2],
                          formMultiple.multipleBlock.address[2],
                          formMultiple.multipleBlock.mail[2]) as List<Any?>,
                   listInfoCenter
      )
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `delete simple block scenario test`() {
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()
      var count = User.select { User.id eq 1 }.count()

      assertEquals(1, count)

      FormSample.tb1.id.value = 1
      FormSample.tb1.uc.value = 0
      FormSample.tb1.ts.value = 0
      FormSample.tb1.name.value = "AUDREY"
      FormSample.tb1.age.value = 23
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
    formMultiple.model
    transaction {
      initMultipleBlockFormTables()
      var count = Center.selectAll().count()

      assertEquals(3, count)

      formMultiple.multipleBlock.centerId[0] = 1
      formMultiple.multipleBlock.ts[0] = 0
      formMultiple.multipleBlock.uc[0] = 0
      formMultiple.multipleBlock.trainingId[0] = 1
      formMultiple.multipleBlock.centerName[0] = "center 111"
      formMultiple.multipleBlock.address[0] = "adresse 111"
      formMultiple.multipleBlock.mail[0] = "center111@gmail.com"

      formMultiple.multipleBlock.centerId[1] = 2
      formMultiple.multipleBlock.ts[1] = 0
      formMultiple.multipleBlock.uc[1] = 0
      formMultiple.multipleBlock.trainingId[1] = 1
      formMultiple.multipleBlock.centerName[1] = "center 222"
      formMultiple.multipleBlock.address[1] = "adresse 222"
      formMultiple.multipleBlock.mail[1] = "center222@gmail.com"

      formMultiple.multipleBlock.vBlock.setMode(VConstants.MOD_UPDATE)
      formMultiple.multipleBlock.vBlock.setRecordFetched(0, true)
      formMultiple.multipleBlock.vBlock.setRecordFetched(1, true)
      formMultiple.multipleBlock.vBlock.delete()

      count = Center.selectAll().count()
      assertEquals(1, count)
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `getSearchColumns test`() {
    val FormSample = FormSample().also { it.model }
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
    val FormSample = FormSample().also { it.model }
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
      FormSample.tb1.vBlock.clear()
      FormSample.tb1.vBlock.load()

      val query = User.selectAll().single()
      val listInfoUser = listOf(query[User.id],  query[User.ts], query[User.uc], query[User.name], query[User.age], query[User.job])

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
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()

      FormSample.tb1.vBlock.clear()
      FormSample.tb1.id.value = 3
      FormSample.tb1.age.value = 25
      FormSample.tb1.vBlock.load()

      val query = User.select { User.id eq 3 }.single()
      val listInfoUser = listOf(query[User.id],  query[User.ts], query[User.uc], query[User.name], query[User.age], query[User.job])

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
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()
      FormSample.tb1.id.value = 2
      FormSample.tb1.age.value = 27

      val error = assertThrows(VQueryNoRowException::class.java) { FormSample.tb1.vBlock.load() }

      assertEquals("VIS-00022: No matching value found.", error.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `load multiple block scenario test`() {
    formMultiple.model
    transaction {
      initMultipleBlockFormTables()
      formMultiple.multipleBlock.vBlock.clear()
      formMultiple.multipleBlock.trainingId[0] = 1
      formMultiple.multipleBlock.trainingId[1] = 1
      formMultiple.multipleBlock.trainingId[2] = 1
      formMultiple.multipleBlock.vBlock.load()

      val listInfoCenter = mutableListOf<Any?>()

      Center.selectAll().forEach {
        listInfoCenter.add(it[Center.id])
        listInfoCenter.add(it[Center.refTraining])
        listInfoCenter.add(it[Center.centerName])
        listInfoCenter.add(it[Center.address])
        listInfoCenter.add(it[Center.mail])
      }

      assertEquals(listOf(formMultiple.multipleBlock.centerId[0],
                          formMultiple.multipleBlock.trainingId[0],
                          formMultiple.multipleBlock.centerName[0],
                          formMultiple.multipleBlock.address[0],
                          formMultiple.multipleBlock.mail[0],
                          formMultiple.multipleBlock.centerId[1],
                          formMultiple.multipleBlock.trainingId[1],
                          formMultiple.multipleBlock.centerName[1],
                          formMultiple.multipleBlock.address[1],
                          formMultiple.multipleBlock.mail[1],
                          formMultiple.multipleBlock.centerId[2],
                          formMultiple.multipleBlock.trainingId[2],
                          formMultiple.multipleBlock.centerName[2],
                          formMultiple.multipleBlock.address[2],
                          formMultiple.multipleBlock.mail[2]) as List<Any?>,
                   listInfoCenter
      )
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `fetchLookup valid scenario test`() {
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()

      FormSample.model.setActiveBlock(FormSample.tb1.vBlock)
      FormSample.tb1.id.value = 1
      FormSample.tb1.vBlock.fetchLookup(FormSample.tb1.id.vField)

      val query = User.select { User.name.eq("AUDREY") and User.age.eq(26) }.single()
      val listInfoUser = listOf(query[User.id], query[User.ts], query[User.uc], query[User.name], query[User.age], query[User.job])

      assertEquals(listOf(FormSample.tb1.id.value,
                          FormSample.tb1.ts.value,
                          FormSample.tb1.uc.value,
                          FormSample.tb1.name.value,
                          FormSample.tb1.age.value,
                          FormSample.tb1.job.value),
                   listInfoUser)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchLookup no matching value exception scenario test`() {
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()

      FormSample.model.setActiveBlock(FormSample.tb1.vBlock)
      FormSample.tb1.id.value = 2

      val vExecFailedException = assertThrows(VExecFailedException::class.java) {
        FormSample.tb1.vBlock.fetchLookup(FormSample.tb1.id.vField)
      }
      assertEquals("VIS-00016: No matching value in User.", vExecFailedException.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchLookup no table found exception scenario test`() {
    val FormSample = FormSample().also { it.model }
    transaction {
      FormSample.model.setActiveBlock(FormSample.tb1.vBlock)
      FormSample.tb1.id.value = 1

      val vExecFailedException = assertThrows(VExecFailedException::class.java) {
        FormSample.tb1.vBlock.fetchLookup(FormSample.tb1.id.vField)
      }

      assertEquals("Table \"USER\" not found; SQL statement:\n" +
                           "SELECT \"USER\".ID, \"USER\".TS, \"USER\".UC, \"USER\".\"NAME\", \"USER\".AGE, \"USER\".JOB, \"USER\".\"CURRICULUM VITAE\" FROM \"USER\" WHERE \"USER\".ID = ?",
                   vExecFailedException.message!!.substring(vExecFailedException.message!!.indexOf("\n") + 1, vExecFailedException.message!!.indexOf("?") + 1))
    }
  }

  @Test
  fun `fetchLookup not unique value exception scenario test`() {
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
        it[name] = "Houssem"
        it[age] = 6
        it[job] = "job"
      }
      FormSample.model.setActiveBlock(FormSample.tb1.vBlock)
      FormSample.tb1.id.value = 1

      val vExecFailedException = assertThrows(VExecFailedException::class.java) {
        FormSample.tb1.vBlock.fetchLookup(FormSample.tb1.id.vField)
      }
      assertEquals("VIS-00020: The value in User is not unique.", vExecFailedException.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `buildQueryDialog test`() {
    val FormSample = FormSample().also { it.model }
    transaction {
      initSampleFormTables()
      FormSample.tb1.vBlock.clear()

      val query = FormSample.tb1.vBlock.buildQueryDialog()!!
      val listInfoUser = mutableListOf<Any?>()

      User.selectAll().forEach {
        listInfoUser.add(it[User.name])
        listInfoUser.add(it[User.age])
        listInfoUser.add(it[User.job])
      }

      assertEquals(listOf(query.data[0][0],
                          query.data[1][0],
                          query.data[2][0],
                          query.data[0][2],
                          query.data[1][2],
                          query.data[2][2],
                          query.data[0][1],
                          query.data[1][1],
                          query.data[2][1]
                          ),
                   listInfoUser)
      SchemaUtils.drop(User)
    }
  }
}
