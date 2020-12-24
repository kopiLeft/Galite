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

import org.jetbrains.exposed.sql.selectAll
import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.kopi.galite.tests.JApplicationTestBase

class VBlockTests : JApplicationTestBase() {

  val firstBlock = FormSample.model.getBlock(0)

  @Test
    fun deleteRecordTest1() {
    //connectToDatabase()
    FormSample.tb1.name.value = "myFirstName"
    FormSample.tb1.age.value = 1
    FormSample.tb1.name.value = "mySecondName"
    FormSample.tb1.age.value = 2
    FormSample.tb1.name.value = "myThirdName"
    FormSample.tb1.age.value = 3

    val query = User.slice(User.name , User.age).selectAll()
    transaction {
      query.forEach {
        println("${it[User.name]} ,${it[User.age]} ")
      }
      val blockDeletedRecord = firstBlock.deleteRecord(0)
      query.forEach {
        println("${it[User.name]} ,${it[User.age]} ")
      }
  //    assertEquals(blockSearchCondition.toString(),
  //            "(\"USER\".\"NAME\" LIKE '%myName') AND (\"USER\".AGE > '9') AND (\"USER\".JOB > 'jobValue')")
    }
  }
}
