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
package org.kopi.galite.demo

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.tests.form.FormSample
import org.kopi.galite.tests.form.FormWithFields
import org.kopi.galite.type.Decimal
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import java.math.BigDecimal

object Client : Table("CLIENTS") {
  val idClt = integer("CLIENT ID").autoIncrement()
  val nameClt = varchar("CLIENT NAME", 25)
  val fstnameClt = varchar("CLIENT FIRSTNAME", 25)
  val addressClt = varchar("CLIENT ADDRESS", 50)
  val ageClt = integer("CLIENT AGE")
  val cityClt = varchar("CLIENT CITY", 30)
  val postalCodeClt = integer("CLIENT POSTAL CODE")

  override val primaryKey = PrimaryKey(idClt, name = "PK_CLIENT_ID")
}

object Product : Table("Products") {
  val idPdt = integer("ID Product").autoIncrement()
  val designation = varchar("DESIGNATION", 50)
  val category = varchar("CATEGORIE", 30)
  val taxName = varchar("PRODUCT TAX NAME", 20).references(TaxRule.taxName)
  val price = integer("UNIT PRICE EXCLUDING VAT")
  val photo = blob("Product PHOTO").nullable()

  override val primaryKey = PrimaryKey(idPdt)
}

object Stock : Table("STOCK") {
  val idStckPdt = integer("IDSTXKPDT").references(Product.idPdt)
  val idStckProv = integer("IDSTXKFOURN").references(Provider.idProvider)
  val minAlert = integer("The MIN VALUE ALERT")

  override val primaryKey = PrimaryKey(idStckPdt, idStckProv)
}

object Provider : Table("PROVIDERS") {
  val idProvider = integer("PROVIDER ID").autoIncrement()
  val nameProvider = varchar("PROVIDER NAME", 50)
  val tel = integer("PROVIDER PHONE")
  val description = varchar("PROVIDER DESCRIPTION", 255).nullable()
  val address = varchar("PROVIDER ADDRESS ", 70)
  val postalCode = integer("PROVIDER POSTAL CODE")
  val logo = blob("PROVIDER COMPANY LOGO").nullable()

  override val primaryKey = PrimaryKey(idProvider)
}

object BillProduct : Table("BILL PRODUCT") {
  val idBPdt = integer("BILL PRODUCT ID").references(Product.idPdt)
  val quantity = integer("QUANTITY")
  val amountHT = integer("AMOUNT HT")
  val amountTTC = decimal("amount TTC", 9, 3).references(Bill.amountTTC)

  override val primaryKey = PrimaryKey(idBPdt)
}

object Command : Table("COMMANDS") {
  val numCmd = integer("COMMAND NUMBER").autoIncrement()
  val idClt = integer("ID_CLT_CMD").references(Client.idClt)
  val dateCmd = varchar("COMMAND DATE", 25)
  val paymentMethod = varchar("PAYMENT METHOD", 50)
  val statusCmd = varchar("COMMAND STATUS", 30)

  override val primaryKey = PrimaryKey(numCmd)

}

object Bill : Table("BILLS") {
  val numBill = integer("BILL NUMBER").autoIncrement()
  val addressBill = varchar("BILL ADDRESS", 30)
  val dateBill = varchar("BILL DATE", 25)
  val amountTTC = decimal("AMOUNT TO PAY", 9, 3)
  val refCmd = integer("COMMAND REFERENCE").references(Command.numCmd)

  override val primaryKey = PrimaryKey(numBill)
}

object TaxRule : Table("TAX RULE") {
  val idTaxe = integer("TAX ID").autoIncrement()
  val taxName = varchar("TAX NAME", 20)
  val rate = integer("TAX RATE IN %%")

  override val primaryKey = PrimaryKey(idTaxe)
}

@SpringBootApplication
open class StoreApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
  connectToDatabase()
  DBSchemaTest.reset()
  initDatabase()
  initModules()
  initUserRights()
  addClients()
  addTaxRule()
  addProducts()
  addFourns()
  addStock()
  addCmds()
  addBillPrdt()
  addBills()
  runApplication<StoreApplication>(*args)
}

/**
 * Initialises the database with creating the necessary tables and creates users.
 */
fun initDatabase(user: String = DBSchemaTest.connectedUser) {
  transaction {
    DBSchemaTest.createDBSchemaTables()
    DBSchemaTest.insertIntoUsers(user, "administrator")
    createStoreTables()
  }
}


fun connectToDatabase(url: String = DBSchemaTest.testURL,
                      driver: String = DBSchemaTest.testDriver,
                      user: String = DBSchemaTest.testUser,
                      password: String = DBSchemaTest.testPassword) {
  Database.connect(url, driver = driver, user = user, password = password)
  DBSchemaTest.connectedUser = user
}

/**
 * Creates DBSchema tables
 */
fun createStoreTables() {
  list_Of_StoreTables.forEach { table ->
    SchemaUtils.create(table)
  }
}

val list_Of_StoreTables = listOf(Client, Product, Stock, Provider,
                                 Bill, TaxRule, Command, BillProduct)

fun initModules() {
  transaction {
    DBSchemaTest.insertIntoModule("2000", "org/kopi/galite/test/Menu", 10)
    DBSchemaTest.insertIntoModule("1000", "org/kopi/galite/test/Menu", 10, "2000")
    DBSchemaTest.insertIntoModule("2009", "org/kopi/galite/test/Menu", 90, "1000", FormSample::class)
    DBSchemaTest.insertIntoModule("2010", "org/kopi/galite/test/Menu", 90, "1000", FormWithFields::class)
  }
}

fun initUserRights(user: String = DBSchemaTest.connectedUser) {
  transaction {
    DBSchemaTest.insertIntoUserRights(user, "2000", true)
    DBSchemaTest.insertIntoUserRights(user, "1000", true)
    DBSchemaTest.insertIntoUserRights(user, "2009", true)
    DBSchemaTest.insertIntoUserRights(user, "2010", true)
  }
}


fun addClients() {
  transaction {
    Client.insert {
      it[idClt] = 0
      it[nameClt] = "Salah"
      it[fstnameClt] = "Mohamed"
      it[addressClt] = "10,Rue du Lac"
      it[cityClt] = "Megrine"
      it[postalCodeClt] = 2001
      it[ageClt] = 40
    }
    Client.insert {
      it[idClt] = 1
      it[nameClt] = "Guesmi"
      it[fstnameClt] = "Khaled"
      it[addressClt] = "14,Rue Mongi Slim"
      it[cityClt] = "Tunis"
      it[postalCodeClt] = 6000
      it[ageClt] = 35
    }
    Client.insert {
      it[idClt] = 2
      it[nameClt] = "Bouaroua"
      it[fstnameClt] = "Ahmed"
      it[addressClt] = "10,Rue du Lac"
      it[cityClt] = "Mourouj"
      it[postalCodeClt] = 5003
      it[ageClt] = 22
    }
  }
}

fun addProducts() {
  transaction {
    Product.insert {
      it[idPdt] = 0
      it[designation] = "designation Product 0"
      it[category] = "cat 1"
      it[taxName] = "tax 1"
      it[price] = 263
    }
    Product.insert {
      it[idPdt] = 1
      it[designation] = "designation Product 1"
      it[category] = "cat 2"
      it[taxName] = "tax 2"
      it[price] = 314
    }
    Product.insert {
      it[idPdt] = 2
      it[designation] = "designation Product 2"
      it[category] = "cat3"
      it[taxName] = "tax 2"
      it[price] = 180
    }
    Product.insert {
      it[idPdt] = 3
      it[designation] = "designation Product 3"
      it[category] = "cat 1"
      it[taxName] = "tax 3"
      it[price] = 65
    }
  }
}

fun addFourns() {
  transaction {
    Provider.insert {
      it[idProvider] = 0
      it[nameProvider] = "Radhia Jouini"
      it[tel] = 21203506
      it[address] = "address provider 1"
      it[description] = " description du Provider ayant l'id 0 "
      it[postalCode] = 2000
    }
    Provider.insert {
      it[idProvider] = 1
      it[nameProvider] = "Sarra Boubaker"
      it[tel] = 99806234
      it[address] = "address provider 2"
      it[description] = " description du Provider ayant l'id 1 "
      it[postalCode] = 3005
    }
    Provider.insert {
      it[idProvider] = 2
      it[nameProvider] = "Hamida Zaoueche"
      it[tel] = 55896321
      it[address] = "address provider 3"
      it[description] = " description du fournisseur ayant l'id 2 "
      it[postalCode] = 6008
    }
    Provider.insert {
      it[idProvider] = 3
      it[nameProvider] = "Seif Markzi"
      it[tel] = 23254789
      it[address] = "address provider 4"
      it[description] = " description du fournisseur ayant l'id 3 "
      it[postalCode] = 2006
    }

  }
}

fun addTaxRule() {
  transaction {
    TaxRule.insert {
      it[idTaxe] = 0
      it[taxName] = "tax 1"
      it[rate] = 19
    }
    TaxRule.insert {
      it[idTaxe] = 1
      it[taxName] = "tax 2"
      it[rate] = 22
    }
    TaxRule.insert {
      it[idTaxe] = 2
      it[taxName] = "tax 3"
      it[rate] = 13
    }
    TaxRule.insert {
      it[idTaxe] = 3
      it[taxName] = "tax 4"
      it[rate] = 9
    }
    TaxRule.insert {
      it[idTaxe] = 4
      it[taxName] = "tax 5"
      it[rate] = 20
    }
  }
}

fun addBills() {
  transaction {
    Bill.insert {
      it[numBill] = 0
      it[addressBill] = "adresse facture 0"
      it[dateBill] = "13/09/20018"
      it[amountTTC] = Decimal.valueOf("3129.7").value
      it[refCmd] = 0
    }
    Bill.insert {
      it[numBill] = 1
      it[addressBill] = "adresse facture 1"
      it[dateBill] = "16/02/2020"
      it[amountTTC] = BigDecimal(Decimal.valueOf("1149.24").toDouble())
      it[refCmd] = 1
    }
    Bill.insert {
      it[numBill] = 2
      it[addressBill] = "adresse facture 2"
      it[dateBill] = "13/05/2019"
      it[amountTTC] = Decimal.valueOf("219.6").value
      it[refCmd] = 2
    }
    Bill.insert {
      it[numBill] = 3
      it[addressBill] = "adresse facture 3"
      it[dateBill] = "12/01/2019"
      it[amountTTC] = Decimal.valueOf("146.9").value
      it[refCmd] = 3
    }
  }
}

fun addStock() {
  transaction {
    Stock.insert {
      it[idStckPdt] = 0
      it[idStckProv] = 0
      it[minAlert] = 50
    }
    Stock.insert {
      it[idStckPdt] = 1
      it[idStckProv] = 1
      it[minAlert] = 100
    }
    Stock.insert {
      it[idStckPdt] = 2
      it[idStckProv] = 2
      it[minAlert] = 50
    }
    Stock.insert {
      it[idStckPdt] = 3
      it[idStckProv] = 3
      it[minAlert] = 20
    }
  }
}

fun addCmds() {
  transaction {
    Command.insert {
      it[numCmd] = 0
      it[idClt] = 0
      it[dateCmd] = "01/01/2020"
      it[paymentMethod] = "cheque"
      it[statusCmd] = "en_cours"
    }
    Command.insert {
      it[numCmd] = 1
      it[idClt] = 0
      it[dateCmd] = "01/01/2020"
      it[paymentMethod] = "cheque"
      it[statusCmd] = "en_cours"
    }
    Command.insert {
      it[numCmd] = 2
      it[idClt] = 1
      it[dateCmd] = "20/01/2021"
      it[paymentMethod] = "carte"
      it[statusCmd] = "en_cours"
    }
    Command.insert {
      it[numCmd] = 3
      it[idClt] = 2
      it[dateCmd] = "13/05/2020"
      it[paymentMethod] = "espece"
      it[statusCmd] = "en_cours"
    }
  }
}


fun addBillPrdt() {
  transaction {
    BillProduct.insert {
      it[idBPdt] = 0
      it[quantity] = 10
      it[amountHT] = 2630
      it[amountTTC] = Decimal.valueOf("3129.7").value
    }
    BillProduct.insert {
      it[idBPdt] = 1
      it[quantity] = 3
      it[amountHT] = 942
      it[amountTTC] = Decimal.valueOf("1149.24").value
    }
    BillProduct.insert {
      it[idBPdt] = 2
      it[quantity] = 1
      it[amountHT] = 180
      it[amountTTC] =  Decimal.valueOf("219.6").value
    }
    BillProduct.insert {
      it[idBPdt] = 3
      it[quantity] = 2
      it[amountHT] = 130
      it[amountTTC] =  Decimal.valueOf("146.9").value
    }
  }
}
