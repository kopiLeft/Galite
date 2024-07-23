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
package org.kopi.galite.demo.database

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.nextIntVal
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.database.*
import org.kopi.galite.demo.bill.BillForm
import org.kopi.galite.demo.billproduct.BillProductForm
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.command.CommandForm
import org.kopi.galite.demo.product.ProductForm
import org.kopi.galite.demo.provider.ProviderForm
import org.kopi.galite.demo.stock.StockForm
import org.kopi.galite.demo.tasks.TasksForm
import org.kopi.galite.demo.taxRule.TaxRuleForm
import org.kopi.galite.type.Week

const val testURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
const val testDriver = "org.h2.Driver"
const val testUser = "admin"
const val testPassword = "admin"

/**
 * Connects to the database.
 */
fun connectToDatabase(url: String = testURL,
                      driver: String = testDriver,
                      user: String = testUser,
                      password: String = testPassword,
                      schema: String? = null
) {
  if (schema != null) {
    Database.connect(url, driver = driver, user = user, password = password, databaseConfig = databaseConfig(Schema(schema)))
  } else {
    Database.connect(url, driver = driver, user = user, password = password)
  }
}

/**
 * Initialises the database with creating the necessary tables and creates users.
 */
fun initDatabase() {
  transaction {
    dropDBSchemaTables()
    dropApplicationTables()
    createDBSchemaTables()
    createApplicationTables()
    insertIntoUsers(testUser, "administrator")
    addClients()
    addTaxRules()
    addProducts()
    addSales()
    addFourns()
    addStocks()
    addCmds()
    addBillPrdts()
    addBills()
    addTasks()
    initModules()
  }
}

/**
 * Creates DBSchema tables
 */
fun createApplicationTables() {
  list_Of_GShopApplicationTables.forEach { table ->
    SchemaUtils.create(table)
  }
  listOfSequences.forEach {
    SchemaUtils.createSequence(it)
  }
}

/**
 * Drops DBSchema tables
 */
fun dropApplicationTables() {
  list_Of_GShopApplicationTables.forEach { table ->
    SchemaUtils.drop(table)
  }
  listOfSequences.forEach {
    SchemaUtils.dropSequence(it)
  }
}

val list_Of_GShopApplicationTables = listOf(Client, Product, Stock, Provider,
                                            Bill, TaxRule, Command, BillProduct, Purchase,
                                            Task)

val listOfSequences = listOf(TASKId)

fun initModules() {
  insertIntoModule("1000", "org/kopi/galite/demo/Menu", 0)
  insertIntoModule("1001", "org/kopi/galite/demo/Menu", 1, "1000", ClientForm::class)
  insertIntoModule("2000", "org/kopi/galite/demo/Menu", 100)
  insertIntoModule("2001", "org/kopi/galite/demo/Menu", 101, "2000", CommandForm::class)
  insertIntoModule("3000", "org/kopi/galite/demo/Menu", 200)
  insertIntoModule("3001", "org/kopi/galite/demo/Menu", 201, "3000", ProductForm::class)
  insertIntoModule("4000", "org/kopi/galite/demo/Menu", 300)
  insertIntoModule("4001", "org/kopi/galite/demo/Menu", 301, "4000", BillForm::class)
  insertIntoModule("4010", "org/kopi/galite/demo/Menu", 401, "4000", BillProductForm::class)
  insertIntoModule("5000", "org/kopi/galite/demo/Menu", 500)
  insertIntoModule("5001", "org/kopi/galite/demo/Menu", 501, "5000", StockForm::class)
  insertIntoModule("6000", "org/kopi/galite/demo/Menu", 600)
  insertIntoModule("6001", "org/kopi/galite/demo/Menu", 601, "6000", TaxRuleForm::class)
  insertIntoModule("7000", "org/kopi/galite/demo/Menu", 700)
  insertIntoModule("7001", "org/kopi/galite/demo/Menu", 701, "7000", ProviderForm::class)
  insertIntoModule("8000", "org/kopi/galite/demo/Menu", 800)
  insertIntoModule("8001", "org/kopi/galite/demo/Menu", 801, "8000", TasksForm::class)
}

/**
 * Creates DBSchema tables
 */
fun createDBSchemaTables() {
  list_Of_Tables.forEach { table ->
    SchemaUtils.create(table)
  }
}

/**
 * Drops DBSchema tables
 */
fun dropDBSchemaTables() {
  list_Of_Tables.forEach { table ->
    SchemaUtils.drop(table)
  }
  sequencesList.forEach { sequence ->
    SchemaUtils.dropSequence(sequence)
  }
}

/**
 * Inserts data into [Users] table
 */
fun insertIntoUsers(shortname: String,
                    userName: String) {
  Users.insert {
    it[uc] = 0
    it[ts] = 0
    it[shortName] = shortname
    it[name] = userName
    it[character] = shortname
    it[active] = true
    it[createdOn] = LocalDateTime.now()
    it[createdBy] = 1
    it[changedOn] = LocalDateTime.now()
    it[changedBy] = 1
  }
}

/**
 * Inserts data into [UserRights] table
 */
fun insertIntoUserRights(userName: String,
                         moduleName: String,
                         accessUser: Boolean) {
  UserRights.insert {
    it[ts] = 0
    it[module] = Modules.slice(Modules.id).select { Modules.shortName eq moduleName }.single()[Modules.id]
    it[user] = Users.slice(Users.id).select { Users.shortName eq userName }.single()[Users.id]
    it[access] = accessUser
  }
}

/**
 * Inserts data into [Modules] table
 */
fun insertIntoModule(shortname: String,
                     source: String,
                     priorityNumber: Int,
                     parentName: String = "-1",
                     className: KClass<*>? = null,
                     symbolNumber: Int? = null) {
  Modules.insert {
    it[uc] = 0
    it[ts] = 0
    it[shortName] = shortname
    it[parent] = if (parentName != "-1") Modules.select { shortName eq parentName }.single()[id] else -1
    it[sourceName] = source
    it[priority] = priorityNumber
    it[objectName] = if (className != null) className.qualifiedName!! else null
    it[symbol] = symbolNumber
  }
  insertIntoUserRights(testUser, shortname, true)
}

fun addClients() {
  addClient("Oussama", "Mellouli", "Marsa, tunis", 38, "example@mail", "Tunisia", "Tunis", 2001)
  addClient("Mohamed", "Salah", "10,Rue Lac", 56, "example@mail", "Tunisia", "Megrine", 2001)
  addClient("Khaled", "Guesmi", "14,Rue Mongi Slim", 35, "example@mail", "Tunisia", "Tunis", 6000)
  addClient("Ahmed", "Bouaroua", "10,Rue du Lac", 22, "example@mail", "Tunisia", "Mourouj", 5003)
}

fun addClient(firstName: String,
              lastName: String,
              address: String,
              age: Int,
              email: String,
              country: String,
              city: String,
              zipcode: Int,
              active: Boolean = true) {
  Client.insert {
    it[firstNameClt] = firstName
    it[lastNameClt] = lastName
    it[addressClt] = address
    it[ageClt] = age
    it[mail] = email
    it[countryClt] = country
    it[cityClt] = city
    it[zipCodeClt] = zipcode
    it[activeClt] = active
  }
}

fun addProducts() {
  // This data is used in automated tests
  addProduct("description Product 0", 1, "tax 1", "Men", "Supplier 0", BigDecimal("100"), 3453656)
  addProduct("description Product 1", 2, "tax 2", "Men","Supplier 0", BigDecimal("200"), 14169288)
  addProduct("description Product 2", 3, "tax 2", "Women","Supplier 0", BigDecimal("300"), 15506985)
  addProduct("description Product 3", 1, "tax 3", "Children","Supplier 0", BigDecimal("400"), 16737894)
  for (i in 4..499) {
    val description = "description Product $i"
    val category = (1..5).random()
    val tax = "tax $category"
    val gender = listOf("Men", "Women", "Children").random()
    val supplier = listOf("Supplier 0", "Supplier 1", "Supplier 2").random()
    val price = (50..500).random().toBigDecimal()
    val color = (Int.MIN_VALUE..Int.MAX_VALUE).random()

    addProduct(description, category, tax, gender, supplier, price, color)
  }
}

fun addProduct(description: String, category: Int, taxName: String, department: String, supplier: String, price: BigDecimal, col: Int) {
  Product.insert {
    it[Product.description] = description
    it[Product.department] = department
    it[Product.supplier] = supplier
    it[Product.category] = category
    it[Product.taxName] = taxName
    it[Product.price] = price
    it[Product.color] = col
  }
}

fun addSales() {
  addSale(1, 1, 1)
  addSale(1, 2, 1)
  addSale(1, 3, 2)
  addSale(1, 4, 3)
  addSale(2, 1, 1)
  addSale(2, 2, 2)
  addSale(2, 3, 4)
  addSale(3, 4, 2)
  addSale(3, 1, 1)
  addSale(3, 2, 3)
  addSale(4, 2, 10)
}

fun addSale(client: Int, product: Int, qty: Int) {
  Purchase.insert {
    it[idClt] = client
    it[idPdt] = product
    it[quantity] = qty
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
    it[zipCode] = postalCode
  }
}

fun addTaxRules() {
  addTaxRule(0, "tax 1", 19)
  addTaxRule(1, "tax 2", 22)
  addTaxRule(2, "tax 3", 13)
  addTaxRule(3, "tax 4", 9)
  addTaxRule(4, "tax 5", 20, "<strong>ABC</strong>")
}

fun addTaxRule(id: Int, taxName: String, rate: Int, information: String? = null) {
  TaxRule.insert {
    it[idTaxe] = id
    it[TaxRule.taxName] = taxName
    it[TaxRule.rate] = rate
    it[informations] = information
  }
}

fun addBills() {
  addBill(0, "Bill address 0", LocalDate.parse("2018-09-13"), BigDecimal("3129.7"), 0, 7652806)
  addBill(1, "Bill address 1", LocalDate.parse("2020-02-16"), BigDecimal("1149.24"), 1, 2662474)
  addBill(2, "Bill address 2", LocalDate.parse("2019-05-13"), BigDecimal("219.6"), 2, 3502417)
  addBill(3, "Bill address 3", LocalDate.parse("2019-01-12"), BigDecimal("146.9"), 3, 5281517)
}

fun addBill(num: Int, address: String, date: LocalDate, amount: BigDecimal, ref: Int, col: Int) {
  Bill.insert {
    it[id] = num
    it[addressBill] = address
    it[dateBill] = date
    it[amountWithTaxes] = amount
    it[refCmd] = ref
    it[color] = col
  }
}

fun addTasks() {
  val currentWeek = Week.now()

  addTask(currentWeek.getDate(1), LocalTime.of(8, 0, 0), LocalTime.of(10, 30, 0), "Conception", "desc 2")
  addTask(currentWeek.getDate(1), LocalTime.of(14, 0, 0), LocalTime.of(16, 0, 0), "Codage", "desc 2")
  addTask(currentWeek.getDate(4), LocalTime.of(11, 0, 0), LocalTime.of(12, 30, 0), "Validation", "desc 2")
}

fun addTask(date: LocalDate, from: LocalTime, to: LocalTime, description1: String, description2: String) {
  Task.insert {
    it[id] = TASKId.nextIntVal()
    it[Task.date] = date
    it[Task.from] = LocalDateTime.of(date, from)
    it[Task.to] = LocalDateTime.of(date, to)
    it[Task.description1] = description1
    it[Task.description2] = description2
  }
}

fun addStocks() {
  addStock(1, 0, 50)
  addStock(2, 1, 100)
  addStock(3, 2, 50)
  addStock(4, 3, 20)
}

fun addStock(id: Int, idStck: Int, minAlerte: Int) {
  Stock.insert {
    it[idStckPdt] = id
    it[idStckProv] = idStck
    it[minAlert] = minAlerte
  }
}

fun addCmds() {
  addCmd(0, 1, LocalDate.parse("2020-01-03"), "check", "in preparation")
  addCmd(1, 1, LocalDate.parse("2020-01-01"), "check", "available")
  addCmd(2, 2, LocalDate.parse("2021-05-03"), "bank card", "delivered")
  addCmd(3, 3, LocalDate.parse("2021-05-13"), "cash", "canceled")
}

fun addCmd(num: Int, client: Int, date: LocalDate, payment: String, status: String) {
  Command.insert {
    it[numCmd] = num
    it[idClt] = client
    it[dateCmd] = date
    it[paymentMethod] = payment
    it[statusCmd] = status
  }
}

fun addBillPrdts() {
  addBillPrdt(1, 10, BigDecimal("2630"), BigDecimal("3129.7"))
  addBillPrdt(2, 3, BigDecimal("942"), BigDecimal("1149.24"))
  addBillPrdt(3, 1, BigDecimal("180"), BigDecimal("219.6"))
  addBillPrdt(4, 2, BigDecimal("130"), BigDecimal("146.9"))
}

fun addBillPrdt(id: Int, quantity: Int, amount: BigDecimal, amountWithTaxes: BigDecimal) {
  BillProduct.insert {
    it[idBPdt] = id
    it[BillProduct.quantity] = quantity
    it[BillProduct.amount] = amount
    it[BillProduct.amountWithTaxes] = amountWithTaxes
  }
}
