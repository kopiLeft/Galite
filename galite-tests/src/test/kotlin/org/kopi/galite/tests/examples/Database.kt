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
package org.kopi.galite.tests.examples

import java.math.BigDecimal

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Sequence
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.nextIntVal
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.visual.type.Decimal

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

val trainingSequence = Sequence("TRAININGID")

val centerSequence = Sequence("CENTERID")

val trainerSequence = Sequence("TRAINERID")

fun initData() {
  transaction {
    SchemaUtils.drop(Training)
    SchemaUtils.dropSequence(trainingSequence)
    SchemaUtils.drop(Center)
    SchemaUtils.dropSequence(centerSequence)
    SchemaUtils.drop(Trainer)
    SchemaUtils.dropSequence(trainerSequence)
    SchemaUtils.create(Training)
    SchemaUtils.createSequence(trainingSequence)
    SchemaUtils.create(Center)
    SchemaUtils.createSequence(centerSequence)
    SchemaUtils.create(Trainer)
    SchemaUtils.createSequence(trainerSequence)
    addTrainings()
    addCenters()
    addTrainer()
  }
}

fun addTrainings() {
  addTraining("training 1", 3, Decimal("1149.24").value, "informations training 1")
  addTraining("training 2", 1, Decimal("219.6").value, "informations training 2")
  addTraining("training 3", 2, Decimal("146.9").value, "informations training 3")
  addTraining("training 4", 1, Decimal("3129.7").value, "informations training 4")
}

fun addTraining(name: String, category: Int, amount: BigDecimal, info: String? = null) {
  Training.insert {
    it[id] = trainingSequence.nextIntVal()
    it[trainingName] = name
    it[type] = category
    it[price] = amount
    it[active] = true
    it[informations] = info
  }
}

fun addCenters() {
  addCenter("Center 1", "10,Rue Lac", "example@mail", "Tunisia", "Megrine", 2001, 2)
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