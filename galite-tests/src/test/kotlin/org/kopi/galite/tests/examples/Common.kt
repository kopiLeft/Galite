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

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Image

class Common {
   class Traineeship : FormBlock(1, 1, "Training") {
    val t = table(Training)

    val trainingID = visit(domain = Domain<Int>(25), position = at(1, 1)) {
      label = "training ID"
      help = "training ID"
      columns(t.id) {
        priority = 1
      }
    }
    val trainingName = visit(domain = Domain<String>(50), position = at(2, 1)) {
      label = "training Name"
      help = "training Name"
      columns(t.trainingName) {
        priority = 1
      }
    }
    val trainingType = visit(domain = Type, position = follow(trainingName)) {
      label = "training Type"
      help = "training Type"
      columns(t.type) {
        priority = 1
      }
    }
    val trainingPrice = visit(domain = Domain<Decimal>(10), position = at(3, 1)) {
      label = "training Price"
      help = "training Price"
      columns(t.price) {
        priority = 1
      }
    }
    val active = visit(domain = Domain<Boolean>(2), position = at(4, 1)) {
      label = "active?"
      help = "active"
      columns(t.active) {
        priority = 1
      }
    }
    val photo = visit(domain = Domain<Image>(100, 100), position = at(9, 1)) {
      label = "photo"
      help = "photo"
      columns(t.photo)
    }
    val informations = visit(domain = Domain<String?>(80, 50, 10), position = at(10, 1)) {
      label = "training informations"
      help = "The training informations"
      columns(t.informations) {
        priority = 1
      }
    }
  }
}
fun addTrainings() {
  addTraining(1, "training 1", "HTML", Decimal("1149.24").value, "informations training 1")
  addTraining(2, "training 2", "JAVA", Decimal("219.6").value,  "informations training 2")
  addTraining(3, "training 3", "PYTHON", Decimal("146.9").value,  "informations training 3")
  addTraining(4, "training 4", "JAVA", Decimal("3129.7").value, "informations training 4")
}

fun addTraining(num: Int, name: String, category: String, amount: BigDecimal, info: String? = null) {
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

object Training : Table("TRAINING") {
  val id = integer("ID")
  val trainingName = varchar("Name", 25)
  val type = varchar("type", 25)
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

object Type : CodeDomain<String>() {
  init {
    "JAVA" keyOf "JAVA"
    "PYTHON" keyOf "PYTHON"
    "HTML" keyOf "HTML"
  }
}
