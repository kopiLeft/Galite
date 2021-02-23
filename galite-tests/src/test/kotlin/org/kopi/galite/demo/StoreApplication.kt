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
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

import org.joda.time.DateTime

import org.kopi.galite.demo.bill.BillForm
import org.kopi.galite.demo.billproduct.BillProductForm
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.command.CommandForm
import org.kopi.galite.demo.product.ProductForm
import org.kopi.galite.demo.provider.ProviderForm
import org.kopi.galite.demo.stock.StockForm
import org.kopi.galite.demo.taxRule.TaxRuleForm
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.tests.form.FormSample
import org.kopi.galite.tests.form.FormWithFields
import org.kopi.galite.type.Decimal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

object Client : Table("CLIENTS") {
  val idClt = integer("ID").autoIncrement()
  val firstNameClt = varchar("FIRSTNAME", 25)
  val lastNameClt = varchar("LASTNAME", 25)
  val addressClt = varchar("ADDRESS", 50)
  val ageClt = integer("AGE")
  val countryClt = varchar("COUNTRY", 30)
  val cityClt = varchar("CITY", 30)
  val zipCodeClt = integer("ZIP_CODE")

  override val primaryKey = PrimaryKey(idClt, name = "PK_CLIENT_ID")
}

object Product : Table("PRODUCTS") {
  val idPdt = integer("ID").autoIncrement()
  val designation = varchar("DESIGNATION", 50)
  val category = varchar("CATEGORY", 30)
  val taxName = varchar("TAX", 20).references(TaxRule.taxName)
  val price = decimal("UNIT_PRICE_EXCLUDING_VAT", 9, 3)
  val photo = blob("PHOTO").nullable()

  override val primaryKey = PrimaryKey(idPdt)
}

object Stock : Table("STOCK") {
  val idStckPdt = integer("PRODUCT_ID").references(Product.idPdt)
  val idStckProv = integer("PROVIDER_ID").references(Provider.idProvider)
  val minAlert = integer("MIN_VALUE_ALERT")

  override val primaryKey = PrimaryKey(idStckPdt, idStckProv)
}

object Provider : Table("PROVIDERS") {
  val idProvider = integer("ID").autoIncrement()
  val nameProvider = varchar("NAME", 50)
  val tel = integer("PHONE")
  val description = varchar("DESCRIPTION", 70)
  val address = varchar("ADDRESS ", 50)
  val zipCode = integer("ZIP_CODE")
  val logo = blob("COMPANY_LOGO").nullable()

  override val primaryKey = PrimaryKey(idProvider)
}

object BillProduct : Table("BILL_PRODUCT") {
  val idBPdt = integer("BILL_PRODUCT_ID").references(Product.idPdt)
  val quantity = integer("QUANTITY")
  val amount = decimal("AMOUNT_BEFORE_TAXES", 9, 3)
  val amountWithTaxes = decimal("AMOUNT_INCLUDING_TAXES", 9, 3)

  override val primaryKey = PrimaryKey(idBPdt)
}

object Command : Table("COMMANDS") {
  val numCmd = integer("COMMAND_NUMBER").autoIncrement()
  val idClt = integer("CLIENT_ID").references(Client.idClt)
  val dateCmd = datetime("COMMAND_DATE").defaultExpression(CurrentDateTime())
  val paymentMethod = varchar("PAYMENT_METHOD", 50)
  val statusCmd = varchar("COMMAND_STATUS", 30)

  override val primaryKey = PrimaryKey(numCmd)
}

object Bill : Table("BILLS") {
  val numBill = integer("BILL_NUMBER").autoIncrement()
  val addressBill = varchar("BILL_ADDRESS", 50)
  val dateBill = datetime("BILL_DATE").defaultExpression(CurrentDateTime())
  val amountWithTaxes = decimal("AMOUNT_TO_PAY", 9, 3).references(BillProduct.amountWithTaxes)
  val refCmd = integer("COMMAND_REFERENCE").references(Command.numCmd)

  override val primaryKey = PrimaryKey(numBill)
}

object TaxRule : Table("TAX_RULE") {
  val idTaxe = integer("ID").autoIncrement()
  val taxName = varchar("NAME", 20)
  val rate = integer("RATE_IN_PERCENTAGE")

  override val primaryKey = PrimaryKey(idTaxe)
}

@SpringBootApplication
open class StoreApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
  connectToDatabase()
  DBSchemaTest.reset()
  Application.initDatabase()
  initModules()
  initUserRights()
  runApplication<StoreApplication>(*args)
}

object Application : DBSchemaTest() {
  /**
   * Used to run the application and show a specific form.
   */
  fun runForm(formName: Form, init: (Application.() -> Unit)? = null) {
    connectToDatabase()
    initDatabase()
    init?.invoke(this)
    org.kopi.galite.demo.desktop.Application.run(formName)
  }

  /**
   * Initialises the database with creating the necessary tables and creates users.
   */
  override fun initDatabase(user: String) {
    super.initDatabase(user)
    transaction {
      createStoreTables()
      addClients()
      addTaxRules()
      addProducts()
      addFourns()
      addStocks()
      addCmds()
      addBillPrdts()
      addBills()
    }
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
    DBSchemaTest.insertIntoModule("6000", "org/kopi/galite/test/Menu", 500)
    DBSchemaTest.insertIntoModule("6001", "org/kopi/galite/test/Menu", 501, "6000", StockForm::class)
    DBSchemaTest.insertIntoModule("7000", "org/kopi/galite/test/Menu", 600)
    DBSchemaTest.insertIntoModule("7001", "org/kopi/galite/test/Menu", 601, "7000", TaxRuleForm::class)
    DBSchemaTest.insertIntoModule("8000", "org/kopi/galite/test/Menu", 700)
    DBSchemaTest.insertIntoModule("8001", "org/kopi/galite/test/Menu", 701, "8000", ProviderForm::class)
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
    DBSchemaTest.insertIntoUserRights(user, "6000", true)
    DBSchemaTest.insertIntoUserRights(user, "6001", true)
    DBSchemaTest.insertIntoUserRights(user, "7000", true)
    DBSchemaTest.insertIntoUserRights(user, "7001", true)
    DBSchemaTest.insertIntoUserRights(user, "8000", true)
    DBSchemaTest.insertIntoUserRights(user, "8001", true)
  }
}

fun addClients() {
  addClient(0, "Mohamed", "Salah", "10,Rue du Lac", 40, "Tunisia", "Megrine", 2001)
  addClient(1, "Khaled", "Guesmi", "14,Rue Mongi Slim", 35, "Tunisia", "Tunis", 6000)
  addClient(2, "Ahmed", "Bouaroua", "10,Rue du Lac", 22, "Tunisia", "Mourouj", 5003)
  addClient(3, "Mohamed", "Nouri", "23,Rue de La Liberte", 56, "Tunisia", "Megrine", 2001)
}

fun addClient(id: Int, firstName: String, lastName: String, address: String, age: Int, country: String, city: String, zipcode: Int) {
  Client.insert {
    it[idClt] = id
    it[firstNameClt] = firstName
    it[lastNameClt] = lastName
    it[addressClt] = address
    it[ageClt] = age
    it[countryClt] = country
    it[cityClt] = city
    it[zipCodeClt] = zipcode
  }
}

fun addProducts() {
  addProduct(0, "designation Product 0", "cat 1", "tax 1", Decimal("263").value)
  addProduct(1, "designation Product 1", "cat 2", "tax 2", Decimal("314").value)
  addProduct(2, "designation Product 2", "cat 3", "tax 2", Decimal("180").value)
  addProduct(3, "designation Product 3", "cat 1", "tax 3", Decimal("65").value)
}

fun addProduct(id: Int, designation: String, category: String, taxName: String, price: BigDecimal) {
  Product.insert {
    it[idPdt] = id
    it[Product.designation] = designation
    it[Product.category] = category
    it[Product.taxName] = taxName
    it[Product.price] = price
  }
}

fun addFourns() {
  addFourn(0, "Radhia Jouini", 21203506, "address provider 1", "Provider 0 description", 2000)
  addFourn(1, "Sarra Boubaker", 99806234, "address provider 2", "Provider 1 description", 3005)
  addFourn(2, "Hamida Zaoueche", 55896321, "address provider 3", "Provider 2 description", 6008)
  addFourn(3, "Seif Markzi", 23254789, "address provider 4", "Provider 3 description", 2006)
}

fun addFourn(id: Int, name: String, tel: Int, address: String, description: String, postalCode: Int) {
  Provider.insert {
    it[idProvider] = id
    it[nameProvider] = name
    it[Provider.tel] = tel
    it[Provider.address] = address
    it[Provider.description] = description
    it[Provider.zipCode] = postalCode
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
  addBill(0, "Bill address 0", DateTime.parse("2018-09-13"), Decimal("3129.7").value, 0)
  addBill(1, "Bill address 1", DateTime.parse("2020-02-16"), Decimal("1149.24").value, 1)
  addBill(2, "Bill address 2", DateTime.parse("2019-05-13"), Decimal("219.6").value, 2)
  addBill(3, "Bill address 3", DateTime.parse("2019-01-12"), Decimal("146.9").value, 3)
}

fun addBill(num: Int, address: String, date: DateTime, amount: BigDecimal, ref: Int) {
  Bill.insert {
    it[numBill] = num
    it[addressBill] = address
    it[dateBill] = date
    it[amountWithTaxes] = amount
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
  addCmd(0, 0, DateTime.parse("2020-01-03"), "check", "in preparation")
  addCmd(1, 0, DateTime.parse("2020-01-01"), "check", "available")
  addCmd(2, 1, DateTime.parse("2021-05-03"), "bank card", "delivered")
  addCmd(3, 2, DateTime.parse("2021-05-13"), "cash", "canceled")
}

fun addCmd(num: Int, id: Int, date: DateTime, payment: String, status: String) {
  Command.insert {
    it[numCmd] = num
    it[idClt] = id
    it[dateCmd] = date
    it[paymentMethod] = payment
    it[statusCmd] = status
  }
}

fun addBillPrdts() {
  addBillPrdt(0, 10, Decimal("2630").value, Decimal("3129.7").value)
  addBillPrdt(1, 3, Decimal("942").value, Decimal("1149.24").value)
  addBillPrdt(2, 1, Decimal("180").value, Decimal("219.6").value)
  addBillPrdt(3, 2, Decimal("130").value, Decimal("146.9").value)
}

fun addBillPrdt(id: Int, quantity: Int, amount: BigDecimal, amountWithTaxes: BigDecimal) {
  BillProduct.insert {
    it[idBPdt] = id
    it[BillProduct.quantity] = quantity
    it[BillProduct.amount] = amount
    it[BillProduct.amountWithTaxes] = amountWithTaxes
  }
}
