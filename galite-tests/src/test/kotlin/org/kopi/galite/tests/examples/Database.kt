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
package org.kopi.galite.tests.examples

import java.math.BigDecimal

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.type.Decimal

object Training : Table("TRAINING") {
  val id = integer("ID")
  val trainingName = varchar("Name", 25)
  val type = integer("type")
  val price = decimal("UNIT_PRICE", 9, 3)
  val active = bool("ACTIVE",)
  val photo = blob("PHOTO").nullable()
  val informations = varchar("INFORMATION", 200).nullable()

  override val primaryKey = PrimaryKey(id, name = "PK_TRAINING_ID")
}

object Center : Table("Center") {
  val id = integer("ID")
  val centerName = varchar("centerName", 25)
  val address = varchar("ADDRESS", 50)
  val mail = varchar("EMAIL", 25)
  val country = varchar("COUNTRY", 30)
  val city = varchar("CITY", 30)
  val zipCode = integer("ZIP_CODE")
  val refTraining = integer("TRAINING_REFERENCE").references(Training.id)

  override val primaryKey = PrimaryKey(id, name = "PK_CENTER_ID")
}

fun initData() {
  transaction {
    SchemaUtils.create(Training)
    SchemaUtils.create(Center)
    addTrainings()
    addCenters()
  }
}

fun addTrainings() {
  addTraining(1, "training 1", 3, Decimal("1149.24").value, "informations training 1")
  addTraining(2, "training 2", 1, Decimal("219.6").value, "informations training 2")
  addTraining(3, "training 3", 2, Decimal("146.9").value, "informations training 3")
  addTraining(4, "training 4", 1, Decimal("3129.7").value, "informations training 4")
}

fun addTraining(num: Int, name: String, category: Int, amount: BigDecimal, info: String? = null) {
  Training.insert {
    it[id] = num
    it[trainingName] = name
    it[type] = category
    it[price] = amount
    it[active] = true
    it[informations] = info
  }
}

fun addCenters() {
  addCenter(1, "Center 1", "10,Rue Lac", "example@mail", "Tunisia", "Megrine", 2001, 2)
  addCenter(2, "Center 2", "14,Rue Mongi Slim", "example@mail", "Tunisia", "Tunis", 6000, 1)
  addCenter(3, "Center 3", "10,Rue du Lac", "example@mail", "Tunisia", "Mourouj", 5003, 3)
  addCenter(4, "Center 4", "10,Rue du Lac", "example@mail", "Tunisia", "Megrine", 2001, 4)
}

fun addCenter(num: Int, name: String,
              centerAdress: String,
              email: String,
              centerCountry: String,
              centerCity: String,
              centerZipCode: Int,
              training: Int) {
  Center.insert {
    it[id] = num
    it[centerName] = name
    it[address] = centerAdress
    it[mail] = email
    it[country] = centerCountry
    it[city] = centerCity
    it[zipCode] = centerZipCode
    it[refTraining] = training
  }
}
