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

import java.math.BigDecimal

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.bill.BillForm
import org.kopi.galite.demo.billproduct.BillProductForm
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.command.CommandForm
import org.kopi.galite.demo.product.ProductForm
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.tests.form.FormSample
import org.kopi.galite.tests.form.FormWithFields
import org.kopi.galite.type.Decimal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

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
  val amountTTC = decimal("amount TTC", 9, 3)

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
  val amountTTC = decimal("AMOUNT TO PAY", 9, 3).references(BillProduct.amountTTC)
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
  transaction {
    addClients()
    addTaxRules()
    addProducts()
    addFourns()
    addStocks()
    addCmds()
    addBillPrdts()
    addBills()
  }
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
    DBSchemaTest.insertIntoModule("1000", "org/kopi/galite/test/Menu", 0)
    DBSchemaTest.insertIntoModule("1001", "org/kopi/galite/test/Menu", 1, "1000", ClientForm::class)
    DBSchemaTest.insertIntoModule("1010", "org/kopi/galite/test/Menu", 5, "1000")
    DBSchemaTest.insertIntoModule("1101", "org/kopi/galite/test/Menu", 10, "1010", FormSample::class)
    DBSchemaTest.insertIntoModule("1110", "org/kopi/galite/test/Menu", 15, "1010", FormWithFields::class)
    DBSchemaTest.insertIntoModule("2000", "org/kopi/galite/test/Menu", 100)
    DBSchemaTest.insertIntoModule("2001", "org/kopi/galite/test/Menu", 101, "2000", CommandForm::class)
    DBSchemaTest.insertIntoModule("3000", "org/kopi/galite/test/Menu", 200)
    DBSchemaTest.insertIntoModule("3001", "org/kopi/galite/test/Menu", 201, "3000", ProductForm::class)
    DBSchemaTest.insertIntoModule("4000", "org/kopi/galite/test/Menu", 300)
    DBSchemaTest.insertIntoModule("4001", "org/kopi/galite/test/Menu", 301, "4000", BillForm::class)
    DBSchemaTest.insertIntoModule("5000", "org/kopi/galite/test/Menu", 400)
    DBSchemaTest.insertIntoModule("5001", "org/kopi/galite/test/Menu", 401, "5000", BillProductForm::class)
  }
}

fun initUserRights(user: String = DBSchemaTest.connectedUser) {
  transaction {
    DBSchemaTest.insertIntoUserRights(user, "1000", true)
    DBSchemaTest.insertIntoUserRights(user, "1001", true)
    DBSchemaTest.insertIntoUserRights(user, "1010", true)
    DBSchemaTest.insertIntoUserRights(user, "1101", true)
    DBSchemaTest.insertIntoUserRights(user, "1110", true)
    DBSchemaTest.insertIntoUserRights(user, "2000", true)
    DBSchemaTest.insertIntoUserRights(user, "2001", true)
    DBSchemaTest.insertIntoUserRights(user, "3000", true)
    DBSchemaTest.insertIntoUserRights(user, "3001", true)
    DBSchemaTest.insertIntoUserRights(user, "4000", true)
    DBSchemaTest.insertIntoUserRights(user, "4001", true)
    DBSchemaTest.insertIntoUserRights(user, "5000", true)
    DBSchemaTest.insertIntoUserRights(user, "5001", true)
  }
}

fun addClients() {
  addClient(0, "Salah", "Mohamed", "10,Rue du Lac", "Megrine", 2001, 40)
  addClient(1, "Guesmi", "Khaled", "14,Rue Mongi Slim", "Tunis", 6000, 35)
  addClient(2, "Bouaroua", "Ahmed", "10,Rue du Lac", "Mourouj", 5003, 22)
}

fun addClient(id: Int, name: String, fstName: String, address: String, city: String, postalCode: Int, age: Int) {
  Client.insert {
    it[idClt] = id
    it[nameClt] = name
    it[fstnameClt] = fstName
    it[addressClt] = address
    it[cityClt] = city
    it[postalCodeClt] = postalCode
    it[ageClt] = age
  }
}

fun addProducts() {
  addProduct(0, "designation Product 0", "cat 1", "tax 1", 263)
  addProduct(1, "designation Product 1", "cat 2", "tax 2", 314)
  addProduct(2, "designation Product 2", "cat 3", "tax 2", 180)
  addProduct(3, "designation Product 3", "cat 1", "tax 3", 65)
}

fun addProduct(id: Int, designation: String, category: String, taxName: String, price: Int) {
  Product.insert {
    it[idPdt] = id
    it[Product.designation] = designation
    it[Product.category] = category
    it[Product.taxName] = taxName
    it[Product.price] = price
  }
}

fun addFourns() {
  addFourn(0, "Radhia Jouini", 21203506, "address provider 1", "description du Provider ayant l'id 0", 2000)
  addFourn(1, "Sarra Boubaker", 99806234, "address provider 2", " description du Provider ayant l'id 1", 3005)
  addFourn(2, "Hamida Zaoueche", 55896321, "address provider 3", " description du Provider ayant l'id 2", 6008)
  addFourn(3, "Seif Markzi", 23254789, "address provider 4", " description du Provider ayant l'id 3", 2006)
}

fun addFourn(id: Int, name: String, tel: Int, address: String, description: String, postalCode: Int) {
  Provider.insert {
    it[idProvider] = id
    it[nameProvider] = name
    it[Provider.tel] = tel
    it[Provider.address] = address
    it[Provider.description] = description
    it[Provider.postalCode] = postalCode
  }
}

fun addTaxRules() {
  addTaxRule(0, "tax 1", 19)
  addTaxRule(1, "tax 2", 22)
  addTaxRule(2, "tax 3", 13)
  addTaxRule(3, "tax 4", 9)
  addTaxRule(4, "tax 5", 20)
}

fun addTaxRule(id: Int, taxName: String, rate: Int) {
  TaxRule.insert {
    it[idTaxe] = id
    it[TaxRule.taxName] = taxName
    it[TaxRule.rate] = rate
  }
}

fun addBills() {
  addBill(0, "addresse facture 0", "13/09/20018", Decimal.valueOf("3129.7").value, 0)
  addBill(1, "addresse facture 1", "16/02/2020", Decimal.valueOf("1149.24").value, 1)
  addBill(2, "addresse facture 2", "13/05/2019", Decimal.valueOf("219.6").value, 2)
  addBill(3, "addresse facture 3", "12/01/2019", Decimal.valueOf("146.9").value, 3)
}

fun addBill(num: Int, address: String, date: String, amount: BigDecimal, ref: Int) {
  Bill.insert {
    it[numBill] = num
    it[addressBill] = address
    it[dateBill] = date
    it[amountTTC] = amount
    it[refCmd] = ref
  }
}

fun addStocks() {
  addStock(0, 0, 50)
  addStock(1, 1, 100)
  addStock(2, 2, 50)
  addStock(3, 3, 20)
}

fun addStock(id: Int, idStck: Int, minAlerte: Int) {
  Stock.insert {
    it[idStckPdt] = id
    it[idStckProv] = idStck
    it[minAlert] = minAlerte
  }
}

fun addCmds() {
  addCmd(0, 0, "01/01/2020", "cheque", "en_cours")
  addCmd(1, 0, "01/01/2020", "cheque", "en_cours")
  addCmd(2, 1, "20/01/2021", "carte", "en_cours")
  addCmd(3, 2, "13/05/2021", "espece", "en_cours")
}

fun addCmd(num: Int, id: Int, date: String, payment: String, status: String) {
  Command.insert {
    it[numCmd] = num
    it[idClt] = id
    it[dateCmd] = date
    it[paymentMethod] = payment
    it[statusCmd] = status
  }
}

fun addBillPrdts() {
  addBillPrdt(0, 10, 2630, Decimal.valueOf("3129.7").value)
  addBillPrdt(1, 3, 942, Decimal.valueOf("1149.24").value)
  addBillPrdt(2, 1, 180, Decimal.valueOf("219.6").value)
  addBillPrdt(3, 2, 130, Decimal.valueOf("146.9").value)
}

fun addBillPrdt(id: Int, quantity: Int, amountHT: Int, amountTTC: BigDecimal) {
  BillProduct.insert {
    it[idBPdt] = id
    it[BillProduct.quantity] = quantity
    it[BillProduct.amountHT] = amountHT
    it[BillProduct.amountTTC] = amountTTC
  }
}
