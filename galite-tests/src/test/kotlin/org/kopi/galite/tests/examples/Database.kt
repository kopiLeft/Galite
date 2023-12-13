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
package org.kopi.galite.tests.examples

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Sequence
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.nextIntVal

import org.kopi.galite.tests.database.createDBSchemaTables
import org.kopi.galite.tests.database.dropDBSchemaTables
import org.kopi.galite.tests.database.insertIntoUsers
import org.kopi.galite.tests.database.TEST_DB_USER

object Training : Table("TRAINING") {
  val id = integer("ID")
  val trainingName = varchar("Name", 25)
  val type = integer("type")
  val price = decimal("UNIT_PRICE", 9, 3)
  val active = bool("ACTIVE")
  val photo = blob("PHOTO").nullable()
  val informations = varchar("INFORMATION", 200).nullable()
  val uc = integer("UC").autoIncrement()
  val ts = integer("TS").autoIncrement()
  val trainDateTimestamp = timestamp("TrainDateTimestamp").nullable()
  val trainDateTime = datetime("TrainingDateTime").nullable()

  override val primaryKey = PrimaryKey(id, name = "PK_TRAINING_ID")
}

object Center : Table("Center") {
  val id = integer("ID")
  val uc = integer("UC").default(0)
  val ts = integer("TS").default(0)
  val centerName = varchar("centerName", 25)
  val address = varchar("ADDRESS", 50)
  val mail = varchar("EMAIL", 25)
  val country = varchar("COUNTRY", 30).nullable()
  val city = varchar("CITY", 30).nullable()
  val zipCode = integer("ZIP_CODE").nullable()
  val refTraining = integer("TRAINING_REFERENCE").references(Training.id)

  override val primaryKey = PrimaryKey(id, name = "PK_CENTER_ID")
}

object Trainer : Table("TRAINER") {
  val id = integer("ID")
  val uc = integer("UC").default(0)
  val ts = integer("TS").default(0)
  val trainerFirstName = varchar("trainerFirstName", 25)
  val trainerLastName = varchar("trainerLastName", 25)

  override val primaryKey = PrimaryKey(id, name = "PK_TRAINER_ID")
}

val trainingSequence = Sequence("TRAINING_ID")

val trainerSequence = Sequence("TRAINERID")

val centerSequence = Sequence("CENTER_ID_seq")

fun initDatabase() {
  dropDBSchemaTables()
  createDBSchemaTables()
  insertIntoUsers(TEST_DB_USER, "administrator")
  initData()
  initModules()
}

fun initData() {
  SchemaUtils.drop(Center)
  SchemaUtils.drop(Training)
  SchemaUtils.drop(Trainer)
  SchemaUtils.dropSequence(trainingSequence)
  SchemaUtils.dropSequence(trainerSequence)
  SchemaUtils.dropSequence(centerSequence)
  SchemaUtils.createSequence(trainingSequence)
  SchemaUtils.createSequence(trainerSequence)
  SchemaUtils.createSequence(centerSequence)
  SchemaUtils.create(Training)
  SchemaUtils.create(Center)
  SchemaUtils.create(Trainer)
  addTrainings()
  addCenters()
  addTrainer()
}

fun addTrainings() {
  addTraining("training 1", 3, BigDecimal("1149.24"), "informations training 1")
  addTraining("training 2", 1, BigDecimal("219.6"), "informations training 2")
  addTraining("training 3", 2, BigDecimal("146.9"), "informations training 3")
  addTraining("training 4", 1, BigDecimal("3129.7"), "informations training 4")
}

fun addTraining(name: String, category: Int, amount: BigDecimal, info: String? = null) {
  Training.insert {
    it[id] = trainingSequence.nextIntVal()
    it[trainingName] = name
    it[type] = category
    it[price] = amount
    it[active] = true
    it[informations] = info
    it[trainDateTimestamp] = Instant.now()
    it[trainDateTime] = LocalDateTime.now()
  }
}

fun addCenters() {
  addCenter("Center 1", "10,Rue Lac", "example@mail", "Tunisia", "Megrine", 2001, 2)
  addCenter("Center 1", "10,Rue Lac", "example@mail", "Tunisia", "Megrine", 2001, 1)
  addCenter("Center 2", "14,Rue Mongi Slim", "example@mail", "Tunisia", "Tunis", 6000, 1)
  addCenter("Center 3", "10,Rue du Lac", "example@mail", "Tunisia", "Mourouj", 5003, 3)
  addCenter("Center 4", "10,Rue du Lac", "example@mail", "Tunisia", "Megrine", 2001, 4)
}

fun addCenter(name: String,
              centerAdress: String,
              email: String,
              centerCountry: String,
              centerCity: String,
              centerZipCode: Int,
              training: Int) {
  Center.insert {
    it[id] = centerSequence.nextIntVal()
    it[centerName] = name
    it[address] = centerAdress
    it[mail] = email
    it[country] = centerCountry
    it[city] = centerCity
    it[zipCode] = centerZipCode
    it[refTraining] = training
  }
}

fun addTrainer() {
  addTrainer("first name", "LAST NAME")

}

fun addTrainer(firstName: String, lastName: String) {
  Trainer.insert {
    it[id] = trainerSequence.nextIntVal()
    it[trainerFirstName] = firstName
    it[trainerLastName] = lastName
  }
}

fun initDocumentationData() {
  dropDocumentationTables()
  SchemaUtils.create(TestTable, TestTable2, TestTriggers)
  SchemaUtils.createSequence(Sequence("TESTTABLEID"), Sequence("TESTTABLE1ID"), Sequence("TRIGGERSID"))
  TestTable.insert {
    it[id] = 1
    it[name] = "TEST-1"
  }
  TestTable.insert {
    it[id] = 2
    it[name] = "TEST-2"
  }
  TestTable.insert {
    it[id] = 3
    it[name] = "NAME"
    it[lastName] = "lastname"
  }
  TestTable2.insert {
    it[id] = 1
    it[name] = "T"
    it[refTable1] = 1
  }
  TestTriggers.insert {
    it[id] = 1
    it[INS] = "INS-1"
    it[UPD] = "UPD-1"
  }
}

fun dropDocumentationTables() {
  SchemaUtils.drop(TestTable, TestTable2, TestTriggers)
  SchemaUtils.dropSequence(Sequence("TESTTABLEID"), Sequence("TESTTABLE1ID"), Sequence("TRIGGERSID"))
}

fun initReportDocumentationData() {
  dropReportDocumentationTables()
  SchemaUtils.create(TestTable, TestTriggers)
  SchemaUtils.createSequence(Sequence("TESTTABLE1ID"))
  SchemaUtils.createSequence(Sequence("TRIGGERSID"))
  TestTable.insert {
    it[id] = 1
    it[name] = "Ahmed"
    it[lastName] = "Malouli"
    it[age] = 40
  }
  TestTable.insert {
    it[id] = 2
    it[name] = "Ahmed"
    it[lastName] = "Cherif"
    it[age] = 30
  }
  TestTable.insert {
    it[id] = 3
    it[name] = "SALAH"
    it[lastName] = "MOUELHI"
    it[age] = 30
  }
}

fun dropReportDocumentationTables() {
  SchemaUtils.drop(TestTable, TestTriggers)
  SchemaUtils.dropSequence(Sequence("TESTTABLEID"), Sequence("TRIGGERSID"))
}
