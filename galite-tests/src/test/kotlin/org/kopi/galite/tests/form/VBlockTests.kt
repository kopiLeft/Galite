/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

import org.kopi.galite.database.Users
import org.kopi.galite.tests.database.connectToDatabase
import org.kopi.galite.tests.examples.Center
import org.kopi.galite.tests.examples.FormToTestSaveMultipleBlock
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.examples.centerSequence
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VQueryNoRowException
import org.kopi.galite.visual.form.VSkipRecordException

class VBlockTests : VApplicationTestBase() {

  val FormWithList = FormWithList()
  val formMultiple = FormToTestSaveMultipleBlock()

  @Before
  fun `override application context connection`() {
    ApplicationContext.applicationContext.getApplication().dBConnection = connection
  }

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
      it[price] = BigDecimal("1149.24")
      it[active] = true
    }
    Center.insert {
      it[id] = 1
      it[refTraining] = 1
      it[centerName] = "center 1"
      it[address] = "adresse 1"
      it[mail] = "center1@gmail.com"
    }
    Center.insert {
      it[id] = 2
      it[refTraining] = 1
      it[centerName] = "center 2"
      it[address] = "adresse 2"
      it[mail] = "center2@gmail.com"
    }
    Center.insert {
      it[id] = 3
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

    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initSampleFormTables()
      FormSample.tb1.block.load()
      val query = User.slice(User.name, User.age).selectAll()

      FormSample.tb1.block.deleteRecord(0)
      val deleteRecordList = query.map {
        listOf(it[User.name], it[User.age])
      }

      assertCollectionsEquals(
        listOf(listOf("Fabienne BUGHIN", 25), listOf("FABIENNE BUGHIN2", 27)),
        deleteRecordList
      )
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun checkUniqueIndexTest() {
    FormWithList.blockWithManyTables.uid[0] = 1
    FormWithList.blockWithManyTables.name[0] = "administrator"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
     println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
      transaction {
        FormWithList.blockWithManyTables.block.checkUniqueIndices(0)
      }
    }
    assertEquals("VIS-00014: ID should be unique", vExecFailedException.message)
  }

  fun addCenterIndicesData(id: Int, center: String, address: String, mail: String) {
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      Center.insert {
        it[Center.id] = id
        it[refTraining] = 1
        it[centerName] = center
        it[Center.address] = address
        it[Center.mail] = mail
      }
    }
  }

  @Test
  fun checkCombinedUniqueIndexTest() {
    var i = 0

    try {
      println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
      transaction {
        SchemaUtils.create(Training)
        SchemaUtils.create(Center)
        Training.insert {
          it[id] = 1
          it[trainingName] = "trainingName"
          it[type] = 1
          it[price] = BigDecimal("1149.24")
          it[active] = true
        }
      }

      // Failing senario should throw exception for Index 0
      addCenterIndicesData(i++, "1", "1", "2")
      formMultiple.multipleBlock.centerName[0] = "1"
      formMultiple.multipleBlock.address[0] = "1"
      formMultiple.multipleBlock.mail[0] = "6"
      val vExecFailedException = assertFailsWith<VExecFailedException> {
        println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
        transaction {
          formMultiple.multipleBlock.block.checkUniqueIndices(0)
        }
      }
      assertEquals("VIS-00014: Index 0", vExecFailedException.message)


      // Failing senario should throw exception for Index 1
      addCenterIndicesData(i++, "2", "8", "3")
      formMultiple.multipleBlock.centerName[0] = "7"
      formMultiple.multipleBlock.address[0] = "8"
      formMultiple.multipleBlock.mail[0] = "3"
      val vExecFailedException2 = assertFailsWith<VExecFailedException> {
        println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
        transaction {
          formMultiple.multipleBlock.block.checkUniqueIndices(0)
        }
      }
      assertEquals("VIS-00014: Index 1", vExecFailedException2.message)

      // Should works without throwing exception
      addCenterIndicesData(i++, "9", "7", "88")
      formMultiple.multipleBlock.centerName[0] = "9"
      formMultiple.multipleBlock.address[0] = "12"
      formMultiple.multipleBlock.mail[0] = "13"
      println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
      transaction {
        formMultiple.multipleBlock.block.checkUniqueIndices(0)
      }

      // Should works without throwing exception
      addCenterIndicesData(i++, "14", "16", "9")
      formMultiple.multipleBlock.centerName[0] = "15"
      formMultiple.multipleBlock.address[0] = "17"
      formMultiple.multipleBlock.mail[0] = "9"
      println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
      transaction {
        formMultiple.multipleBlock.block.checkUniqueIndices(0)
      }
    } finally {
      println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
      transaction {
        SchemaUtils.drop(Center)
        SchemaUtils.drop(Training)
      }
    }
  }

  @Test
  fun `test refreshLookup with no matching value in the table`() {
    FormWithList.blockWithManyTables.shortName[0] = "test"

    val vExecFailedException = assertFailsWith<VExecFailedException> {
      println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
      transaction {
        FormWithList.blockWithManyTables.block.refreshLookup(0)
      }
    }

    assertEquals(MessageCode.getMessage("VIS-00016", FormWithList.blockWithManyTables.m.tableName), vExecFailedException.message)
  }

  @Test
  fun refreshLookupTest() {
    FormWithList.blockWithManyTables.shortName[0] = "1000"

    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initModules()
      FormWithList.blockWithManyTables.block.refreshLookup(0)
    }
  }

  @Test
  fun getSearchConditionsTest1() {
    val FormSample = FormSample()

    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "myName"
    FormSample.tb1.age.value = 6
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = FormSample.tb1.block.getSearchConditions()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" = 'myName') AND " +
                "(\"USER\".AGE = 6) AND (UPPER(\"USER\".JOB) = UPPER('jobValue'))", blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchConditionsTest2() {
    val FormSample = FormSample()
    with(FormSample.tb1.block) {
      fields[5].setSearchOperator(VConstants.SOP_LT)
      fields[6].setSearchOperator(VConstants.SOP_NE)
    }
    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "myName*"
    FormSample.tb1.age.value = 8
    FormSample.tb1.job.value = "jobValue*"
    val blockSearchCondition = FormSample.tb1.block.getSearchConditions()

    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE 'myName%') AND " +
                "(\"USER\".AGE < 8) AND (UPPER(\"USER\".JOB) NOT LIKE UPPER('jobValue%'))",
        blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchConditionsTest3() {
    val FormSample = FormSample()
    with(FormSample.tb1.block) {
      fields[5].setSearchOperator(VConstants.SOP_GT)
      fields[6].setSearchOperator(VConstants.SOP_EQ)
    }

    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "*myName"
    FormSample.tb1.age.value = 9
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = FormSample.tb1.block.getSearchConditions()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE '%myName') AND " +
                "(\"USER\".AGE > 9) AND (UPPER(\"USER\".JOB) = UPPER('jobValue'))", blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchConditionsTest4() {
    val FormSample = FormSample()
    with(FormSample.tb1.block) {
      fields[5].setSearchOperator(VConstants.SOP_LE)
      fields[6].setSearchOperator(VConstants.SOP_EQ)
    }
    FormSample.tb1.id.value = null
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "my*Name"
    FormSample.tb1.age.value = 10
    FormSample.tb1.job.value = "*jobValue"

    val blockSearchCondition = FormSample.tb1.block.getSearchConditions()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE 'my%Name') AND " +
                "(\"USER\".AGE <= 10) AND (UPPER(\"USER\".JOB) LIKE UPPER('%jobValue'))",
        blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchConditionsTest5() {
    val FormSample = FormSample()
    with(FormSample.tb1.block) {
      fields[5].setSearchOperator(VConstants.SOP_GE)
      fields[6].setSearchOperator(VConstants.SOP_EQ)
    }
    FormSample.tb1.uc.value = 0
    FormSample.tb1.ts.value = 0
    FormSample.tb1.name.value = "*"
    FormSample.tb1.age.value = 11
    FormSample.tb1.job.value = "job*Value"

    val blockSearchCondition = FormSample.tb1.block.getSearchConditions()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      assertEquals(
        "(\"USER\".TS = 0) AND (\"USER\".UC = 0) AND (\"USER\".\"NAME\" LIKE '%') AND " +
                "(\"USER\".AGE >= 11) AND (UPPER(\"USER\".JOB) LIKE UPPER('job%Value'))",
        blockSearchCondition.toString()
      )
    }
  }

  @Test
  fun getSearchOrderTest() {
    val orderBys = FormWithList.block3.block.getSearchOrder()

    assertCollectionsEquals(arrayListOf(Users.name to SortOrder.ASC), orderBys)
  }

  @Test
  fun `fetchRecord with existing ID scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initSampleFormTables()
      //fetch record search with id 1 and there is no exception
      FormSample.tb1.block.fetchRecord(1)
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.block.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchRecord no such element test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      SchemaUtils.create(User)

      assertThrows(VSkipRecordException::class.java) {
        FormSample.tb1.block.fetchRecord(1)
      }
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchRecord too many rows scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      SchemaUtils.create(User)
      initSampleFormTables()
      User.insert {
        it[id] = 1
        it[ts] = 0
        it[uc] = 0
      }
      val error = assertThrows(AssertionError::class.java) { FormSample.tb1.block.fetchRecord(1) }

      assertEquals("too many rows", error.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchNextRecord valid scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initSampleFormTables()
      FormSample.tb1.name.value = "AUDREY"
      FormSample.tb1.age.value = 26
      FormSample.tb1.block.load()
      FormSample.tb1.block.fetchNextRecord(0)

      val query = User.select { User.name.eq("AUDREY") and User.age.eq(26) }.single()
      val listInfoUser = listOf(query[User.id], query[User.name], query[User.age], query[User.job])

      assertEquals(
        listOf(1, FormSample.tb1.name.value, FormSample.tb1.age.value, FormSample.tb1.job.value),
        listInfoUser
      )
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.block.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchNextRecord multi block scenario test`() {
    val error = assertThrows(AssertionError::class.java) {
      FormWithMultipleBlock.multipleBlock.block.fetchNextRecord(0)
    }

    assertEquals(FormWithMultipleBlock.multipleBlock.block.name + " is a multi block", error.message)
  }

  @Test
  fun `fetchNextRecord exec failed scenario test`() {
    val FormSample = FormSample()
    assertThrows(VExecFailedException::class.java) {
      FormSample.tb1.block.fetchNextRecord(3)
    }
  }

  @Test
  fun `save insert simple block scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      SchemaUtils.create(User)
      SchemaUtils.createSequence(userSequence)

      FormSample.tb1.uc.value = 0
      FormSample.tb1.ts.value = 0
      FormSample.tb1.name.value = "Houssem"
      FormSample.tb1.age.value = 26
      FormSample.tb1.job.value = "job"

      FormSample.tb1.setMode(Mode.INSERT)
      FormSample.tb1.block.save()

      val query = User.selectAll().single()
      val listInfoUser = listOf(query[User.id], query[User.name], query[User.age], query[User.job])

      assertEquals(
        listOf(1, FormSample.tb1.name.value, FormSample.tb1.age.value, FormSample.tb1.job.value),
        listInfoUser
      )
      SchemaUtils.drop(User)
      SchemaUtils.dropSequence(userSequence)
    }
  }

  @Test
  fun `save update simple block scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      SchemaUtils.create(User)
      initSampleFormTables()

      FormSample.tb1.id.value = 1
      FormSample.tb1.uc.value = 0
      FormSample.tb1.ts.value = 0
      FormSample.tb1.name.value = "HADDAD"
      FormSample.tb1.age.value = 27
      FormSample.tb1.job.value = "work"

      FormSample.tb1.setMode(Mode.UPDATE)
      FormSample.tb1.block.save()

      val query = User.select { User.id eq 1 }.single()
      val listInfoUser = listOf(query[User.id], query[User.name], query[User.age], query[User.job])

      assertEquals(
        listOf(1, FormSample.tb1.name.value, FormSample.tb1.age.value, FormSample.tb1.job.value),
        listInfoUser
      )
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `save insert multiple block scenario test`() {
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      SchemaUtils.create(Training)
      SchemaUtils.create(Center)
      SchemaUtils.createSequence(centerSequence)
      Training.insert {
        it[id] = 1
        it[trainingName] = "trainingName"
        it[type] = 1
        it[price] = BigDecimal("1149.24")
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


      formMultiple.multipleBlock.setMode(Mode.INSERT)
      formMultiple.multipleBlock.block.save()

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
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
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

      formMultiple.multipleBlock.setMode(Mode.UPDATE)
      formMultiple.multipleBlock.block.setRecordFetched(0, true)
      formMultiple.multipleBlock.block.setRecordFetched(1, true)
      formMultiple.multipleBlock.block.setRecordFetched(2, true)
      formMultiple.multipleBlock.block.save()

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
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
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

      FormSample.tb1.block.setRecordFetched(0, true)
      FormSample.tb1.block.delete()

      count = User.select { User.id eq 1 }.count()
      assertEquals(0, count)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `delete multiple block scenario test`() {
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
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

      formMultiple.multipleBlock.setMode(Mode.UPDATE)
      formMultiple.multipleBlock.block.setRecordFetched(0, true)
      formMultiple.multipleBlock.block.setRecordFetched(1, true)
      formMultiple.multipleBlock.block.delete()

      count = Center.selectAll().count()
      assertEquals(1, count)
      SchemaUtils.drop(Training, Center)
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `getSearchColumns test`() {
    val FormSample = FormSample()
    assertEquals(listOf(FormSample.tb1.id.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.ts.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.uc.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.name.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.age.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.job.columns?.getColumnsModels()?.get(0)?.column,
                        FormSample.tb1.cv.columns?.getColumnsModels()?.get(0)?.column),
                 FormSample.tb1.block.getSearchColumns()
    )
  }

  @Test
  fun `load simple block scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
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
      FormSample.tb1.block.clear()
      FormSample.tb1.block.load()

      val query = User.selectAll().single()
      val listInfoUser = listOf(query[User.id], query[User.ts], query[User.uc], query[User.name], query[User.age], query[User.job])

      assertEquals(listOf(FormSample.tb1.id.value,
                          FormSample.tb1.ts.value,
                          FormSample.tb1.uc.value,
                          FormSample.tb1.name.value,
                          FormSample.tb1.age.value,
                          FormSample.tb1.job.value),
                   listInfoUser)
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.block.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `load with set id value simple block scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initSampleFormTables()

      FormSample.tb1.block.clear()
      FormSample.tb1.id.value = 3
      FormSample.tb1.age.value = 25
      FormSample.tb1.block.load()

      val query = User.select { User.id eq 3 }.single()
      val listInfoUser = listOf(query[User.id], query[User.ts], query[User.uc], query[User.name], query[User.age], query[User.job])

      assertEquals(listOf(FormSample.tb1.id.value,
                          FormSample.tb1.ts.value,
                          FormSample.tb1.uc.value,
                          FormSample.tb1.name.value,
                          FormSample.tb1.age.value,
                          FormSample.tb1.job.value),
                   listInfoUser)
      assertEquals(VConstants.MOD_UPDATE, FormSample.tb1.block.getMode())
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `load no row exception scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initSampleFormTables()
      FormSample.tb1.id.value = 2
      FormSample.tb1.age.value = 27

      val error = assertThrows(VQueryNoRowException::class.java) { FormSample.tb1.block.load() }

      assertEquals("VIS-00022: No matching value found.", error.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `load multiple block scenario test`() {
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initMultipleBlockFormTables()
      formMultiple.multipleBlock.block.clear()
      formMultiple.multipleBlock.trainingId[0] = 1
      formMultiple.multipleBlock.trainingId[1] = 1
      formMultiple.multipleBlock.trainingId[2] = 1
      formMultiple.multipleBlock.block.load()

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
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initSampleFormTables()

      FormSample.model.setActiveBlock(FormSample.tb1.block)
      FormSample.tb1.id.value = 1
      FormSample.tb1.block.fetchLookup(FormSample.tb1.id.vField)

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
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initSampleFormTables()

      FormSample.model.setActiveBlock(FormSample.tb1.block)
      FormSample.tb1.id.value = 2

      val vExecFailedException = assertThrows(VExecFailedException::class.java) {
        FormSample.tb1.block.fetchLookup(FormSample.tb1.id.vField)
      }
      assertEquals("VIS-00016: No matching value in User.", vExecFailedException.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `fetchLookup no table found exception scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      FormSample.model.setActiveBlock(FormSample.tb1.block)
      FormSample.tb1.id.value = 1

      val vExecFailedException = assertThrows(VExecFailedException::class.java) {
        FormSample.tb1.block.fetchLookup(FormSample.tb1.id.vField)
      }

      assertTrue(
        vExecFailedException.message!!.contains(
          "Table \"USER\" not found; SQL statement:\n" +
                  "SELECT \"USER\".ID, \"USER\".TS, \"USER\".UC, \"USER\".\"NAME\", \"USER\".AGE, \"USER\".JOB, \"USER\".\"CURRICULUM VITAE\" FROM \"USER\" WHERE \"USER\".ID = ?"
        )
      )
    }
  }

  @Test
  fun `fetchLookup not unique value exception scenario test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
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
      FormSample.model.setActiveBlock(FormSample.tb1.block)
      FormSample.tb1.id.value = 1

      val vExecFailedException = assertThrows(VExecFailedException::class.java) {
        FormSample.tb1.block.fetchLookup(FormSample.tb1.id.vField)
      }
      assertEquals("VIS-00020: The value in User is not unique.", vExecFailedException.message)
      SchemaUtils.drop(User)
    }
  }

  @Test
  fun `buildQueryDialog test`() {
    val FormSample = FormSample()
    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      initSampleFormTables()
      FormSample.tb1.block.clear()

      val query = FormSample.tb1.block.buildQueryDialog()!!
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

  @Test
  fun `getActiveCommands test`() {
    val model = formMultiple.multipleBlock.block

    model.setCommandsEnabled(true)
    assertEquals(1, model.activeCommands.size)
    assertEquals(formMultiple.saveBlock, model.activeCommands[0].actor)
  }

  @Test
  fun `setMode test`() {
    val model = formMultiple.multipleBlock.block

    model.setMode(VConstants.MOD_INSERT)
    assertEquals(VConstants.MOD_INSERT, model.getMode())
    // all fields are configured with onInsertHidden()
    assertTrue(model.fields.all {
      for (counter in 0..model.recordCount) {
        if (it.getAccess(counter) != VConstants.ACS_HIDDEN)
          return@all false
      }
      return@all true
    })
  }

  @Test
  fun `test active field after setting mode`() {
    val model = formMultiple.block.block

    model.enter()
    model.setMode(VConstants.MOD_INSERT)
    assertNotNull(model.activeField)

    model.activeField!!.leave(false)
    formMultiple.block.name.vField.enter()

    model.setMode(VConstants.MOD_INSERT)
    assertNull(model.activeField)
  }

  @Test
  fun `set and reset Color test`() {
    val model = formMultiple.multipleBlock.block
    val recordId = 0

    model.setColor(recordId, VColor.BLACK, VColor.WHITE)
    assertTrue(model.fields.filter { !it.isInternal() }.all {
      it.getForeground(recordId) == VColor.BLACK && it.getBackground(recordId) == VColor.WHITE
    })

    model.resetColor(recordId)
    assertTrue(model.fields.filter { !it.isInternal() }.all {
      it.getForeground(recordId) == null && it.getBackground(recordId) == null
    })
  }

  @Test
  fun `leave block test`() {
    val formSample = FormSample()
    val blockModel = formSample.tb1.block

    formSample.model.getActiveBlock()?.leave(false)
    assertNotEquals(blockModel, formSample.model.getActiveBlock())
    assertNull(blockModel.activeField)
  }

  @Test
  fun `sort test`() {
    val blockModel = formMultiple.multipleBlock.block

    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      try {
        initMultipleBlockFormTables()
        blockModel.load()
        val numberOfFilledRecords = blockModel.numberOfFilledRecords

        blockModel.sort(-1, 1) // sorting without column
        assertTrue(blockModel.sortedRecords.indices.all { blockModel.sortedRecords[it] == it })
        blockModel.sort(4, -1) // sorting using the center name column
        assertFalse(blockModel.sortedRecords.slice(0 until numberOfFilledRecords).indices.all { blockModel.sortedRecords[it] == it })
      } finally {
        SchemaUtils.drop(Training, Center)
      }
      SchemaUtils.dropSequence(centerSequence)
    }
  }

  @Test
  fun `getNumberOfValidRecord test`() {
    val blockModel = formMultiple.multipleBlock.block

    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      try {
        initMultipleBlockFormTables()
        formMultiple.multipleBlock.load()
        assertEquals(20, blockModel.numberOfValidRecord)
        assertEquals(3, blockModel.numberOfFilledRecords)
      } finally {
        SchemaUtils.drop(Training, Center)
        SchemaUtils.dropSequence(centerSequence)
      }
    }
  }

  @Test
  fun `enter test`() {
    val formSample = FormSample()
    val blockModel = formSample.tb1.block

    blockModel.enter()
    assertEquals(blockModel, formSample.model.getActiveBlock())
    assertEquals(blockModel.activeField, formSample.tb1.name.vField) // fields 1s -> 3rd are hidden
  }

  @Test
  fun `getReportSearchColumns test`() {
    val formSample = FormSample()

    assertEquals(
      listOf(
        formSample.tb1.name.columns?.getColumnsModels()?.get(0)?.column,
        formSample.tb1.age.columns?.getColumnsModels()?.get(0)?.column,
        formSample.tb1.job.columns?.getColumnsModels()?.get(0)?.column,
        formSample.tb1.cv.columns?.getColumnsModels()?.get(0)?.column,
        formSample.tb1.id.columns?.getColumnsModels()?.get(0)?.column
      ),
      formSample.tb1.block.getReportSearchColumns()
    )
  }

  @Test
  fun `trailRecord test`() {
    val singleBlockModel = FormWithList.block3.block

    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      FormWithList.block3.load()

      singleBlockModel.trailRecord(singleBlockModel.currentRecord)
      assertTrue(singleBlockModel.isRecordTrailed(singleBlockModel.currentRecord))
    }
  }

  @Test
  fun `abortTrail test`() {
    val singleBlockModel = FormWithList.block3.block

    println("Pool name (VBlockTest): ${ApplicationContext.getDBConnection()?.poolConnection?.poolName}")
    transaction {
      FormWithList.block3.load()

      singleBlockModel.trailRecord(singleBlockModel.currentRecord)
      assertTrue(singleBlockModel.isRecordTrailed(singleBlockModel.currentRecord))

      singleBlockModel.abortTrail()
      assertFalse(singleBlockModel.isRecordTrailed(singleBlockModel.currentRecord))
    }
  }
}
