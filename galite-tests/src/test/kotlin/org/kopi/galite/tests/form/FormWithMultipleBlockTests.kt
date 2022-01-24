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

import kotlin.test.assertEquals
import kotlin.test.assertTrue

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

import org.junit.Test
import org.kopi.galite.tests.examples.Center
import org.kopi.galite.tests.examples.FormToTestSaveMultipleBlock
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.examples.centerSequence
import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.visual.type.Decimal

class FormWithMultipleBlockTests : JApplicationTestBase() {

  val formMultiple = FormToTestSaveMultipleBlock()

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

  fun destroyMultipleBlockFormTables() {
    SchemaUtils.dropSequence(centerSequence)
    SchemaUtils.drop(Center)
    SchemaUtils.drop(Training)
  }

  @Test
  fun multipleBlockTest() {
    val formModel = FormWithMultipleBlock.model

    assertEquals(formModel.getBlock(1).bufferSize, 100)
    assertEquals(formModel.getBlock(1).displaySize, 100)
    assertEquals(formModel.getBlock(1).displayedFields, 1)

    val nameField = formModel.getBlock(1).fields[1]
    assertEquals(1, nameField.position!!.column)
    assertEquals(1, nameField.position!!.columnEnd)
    assertEquals(1, nameField.position!!.line)
    assertEquals(1, nameField.position!!.lineEnd)
    assertEquals(1, nameField.position!!.chartPos)
  }

  @Test
  fun `commitTrail test`() {
    val formModel = formMultiple.model

    transaction {
      try {
        initMultipleBlockFormTables()
        formMultiple.multipleBlock.load()
        assertTrue(formModel.blocks.any {
          for (i in 0 until it.bufferSize) {
            if (it.isRecordTrailed(i)) {
              return@any true
            }
          }
          return@any false
        })
        formModel.commitTrail()
        assertTrue(formModel.blocks.all {
          for (i in 0 until it.bufferSize) {
            if (it.isRecordTrailed(i)) {
              return@all false
            }
          }
          return@all true
        })
      } finally {
        destroyMultipleBlockFormTables()
      }
    }
  }

  @Test
  fun `abortTrail test`() {
    val formModel = formMultiple.model

    transaction {
      try {
        initMultipleBlockFormTables()
        formMultiple.multipleBlock.load()
        assertTrue(formModel.blocks.any {
          for (i in 0 until it.bufferSize) {
            if (it.isRecordTrailed(i)) {
              return@any true
            }
          }
          return@any false
        })
        formModel.abortTrail()
        assertTrue(formModel.blocks.all {
          for (i in 0 until it.bufferSize) {
            if (it.isRecordTrailed(i)) {
              return@all false
            }
          }
          return@all true
        })
      } finally {
        destroyMultipleBlockFormTables()
      }
    }
  }
}
