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

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.tests.JApplicationTestBase

class VBlockTests : JApplicationTestBase() {

  val firstBlock = FormSample.model.getBlock(0)

  @Test
  fun getSearchConditionsTest1() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    connectToDatabase()
    FormSample.tb1.name.value = "myName"
    FormSample.tb1.age.value = 6
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = firstBlock.getSearchConditions_()
    transaction {
      assertEquals(blockSearchCondition.toString(),
              "(\"USER\".\"NAME\" = 'myName') AND (\"USER\".AGE = '6') AND (\"USER\".JOB = 'jobValue')")
    }
  }

  @Test
  fun getSearchConditionsTest2() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    with(firstBlock) {
      fields[3].setSearchOperator(1)
      fields[4].setSearchOperator(1)
    }

    connectToDatabase()
    FormSample.tb1.name.value = "myName*"
    FormSample.tb1.age.value = 8
    FormSample.tb1.job.value = "jobValue"
    val blockSearchCondition = firstBlock.getSearchConditions_()

    transaction {
      assertEquals(blockSearchCondition.toString(),
              "(\"USER\".\"NAME\" LIKE 'myName%') AND (\"USER\".AGE < '8') AND (\"USER\".JOB < 'jobValue')")
    }
  }

  @Test
  fun getSearchConditionsTest3() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    with(firstBlock) {
      fields[3].setSearchOperator(2)
      fields[4].setSearchOperator(2)
    }
    connectToDatabase()
    FormSample.tb1.name.value = "*myName"
    FormSample.tb1.age.value = 9
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = firstBlock.getSearchConditions_()
    transaction {
      assertEquals(blockSearchCondition.toString(),
              "(\"USER\".\"NAME\" LIKE '%myName') AND (\"USER\".AGE > '9') AND (\"USER\".JOB > 'jobValue')")
    }
  }

  @Test
  fun getSearchConditionsTest4() {

    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    with(firstBlock) {
      fields[3].setSearchOperator(3)
      fields[4].setSearchOperator(3)
    }
    connectToDatabase()
    FormSample.tb1.name.value = "my*Name"
    FormSample.tb1.age.value = 10
    FormSample.tb1.job.value = "jobValue"

    val blockSearchCondition = firstBlock.getSearchConditions_()
    transaction {
      assertEquals(blockSearchCondition.toString(),
              "(\"USER\".\"NAME\" LIKE 'my%Name') AND (\"USER\".AGE <= '10') AND (\"USER\".JOB <= 'jobValue')")
    }
  }

  @Test
  fun getSearchConditionsTest5() {
    // OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")
    with(firstBlock) {
      fields[3].setSearchOperator(4)
      fields[4].setSearchOperator(4)
    }
    connectToDatabase()
    FormSample.tb1.name.value = "*"
    FormSample.tb1.age.value = 11
    FormSample.tb1.job.value = "job*Value"

    val blockSearchCondition = firstBlock.getSearchConditions_()
    transaction {
      assertEquals(blockSearchCondition.toString(),
              "(\"USER\".\"NAME\" LIKE '%') AND (\"USER\".AGE >= '11') AND (\"USER\".JOB > 'job')")
    }
  }
}
