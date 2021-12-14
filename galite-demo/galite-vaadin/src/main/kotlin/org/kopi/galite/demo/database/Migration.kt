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
package org.kopi.galite.demo.database

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.nextIntVal
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.bill.BillForm
import org.kopi.galite.demo.billproduct.BillProductForm
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.command.CommandForm
import org.kopi.galite.demo.product.ProductForm
import org.kopi.galite.demo.provider.ProviderForm
import org.kopi.galite.demo.stock.StockForm
import org.kopi.galite.demo.tasks.TasksForm
import org.kopi.galite.demo.taxRule.TaxRuleForm
import org.kopi.galite.visual.db.Modules
import org.kopi.galite.visual.db.UserRights
import org.kopi.galite.visual.db.Users
import org.kopi.galite.visual.db.databaseConfig
import org.kopi.galite.visual.db.list_Of_Tables
import org.kopi.galite.visual.db.sequencesList
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.type.Week

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
  }
  initModules()
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
  transaction {
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
}

/**
 * Creates DBSchema tables
 */
fun createDBSchemaTables() {
  list_Of_Tables.forEach { table ->
    SchemaUtils.create(table)
  }
  sequencesList.forEach { sequence ->
    SchemaUtils.createSequence(sequence)
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
    it[createdOn] = Instant.now()
    it[createdBy] = 1
    it[changedOn] = Instant.now()
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
 * Inserts data into [Module] table
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
  addClient(1, "Oussama", "Mellouli", "Marsa, tunis", 38, "example@mail", "Tunisia", "Tunis", 2001)
  addClient(2, "Mohamed", "Salah", "10,Rue Lac", 56, "example@mail", "Tunisia", "Megrine", 2001)
  addClient(3, "Khaled", "Guesmi", "14,Rue Mongi Slim", 35, "example@mail", "Tunisia", "Tunis", 6000)
  addClient(4, "Ahmed", "Bouaroua", "10,Rue du Lac", 22, "example@mail", "Tunisia", "Mourouj", 5003)
}

fun addClient(id: Int,
              firstName: String,
              lastName: String,
              address: String,
              age: Int,
              email: String,
              country: String,
              city: String,
              zipcode: Int,
              active: Boolean = true) {
  Client.insert {
    it[idClt] = id
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
  addProduct(0, "description Product 0", 1, "tax 1", "Men", "Supplier 0", Decimal("263").value)
  addProduct(1, "description Product 1", 2, "tax 2", "Men","Supplier 0", Decimal("314").value)
  addProduct(2, "description Product 2", 3, "tax 2", "Women","Supplier 0", Decimal("180").value)
  addProduct(3, "description Product 3", 1, "tax 3", "Children","Supplier 0", Decimal("65").value)
}

fun addSales() {
  addSale(1, 0, 1, 1)
  addSale(1, 1, 1, 2)
  addSale(1, 2, 2, 6)
  addSale(1, 3, 3, 3)
}

fun addProduct(id: Int, description: String, category: Int, taxName: String, department: String, supplier: String, price: BigDecimal) {
  Product.insert {
    it[idPdt] = id
    it[Product.description] = description
    it[Product.department] = department
    it[Product.supplier] = supplier
    it[Product.category] = category
    it[Product.taxName] = taxName
    it[Product.price] = price
  }
}

fun addSale(client: Int, product: Int, qty: Int, i: Int) {
  Purchase.insert {
    it[id] = i
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
    it[Provider.zipCode] = postalCode
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
  addBill(0, "Bill address 0", LocalDate.parse("2018-09-13"), Decimal("3129.7").value, 0)
  addBill(1, "Bill address 1", LocalDate.parse("2020-02-16"), Decimal("1149.24").value, 1)
  addBill(2, "Bill address 2", LocalDate.parse("2019-05-13"), Decimal("219.6").value, 2)
  addBill(3, "Bill address 3", LocalDate.parse("2019-01-12"), Decimal("146.9").value, 3)
}

fun addBill(num: Int, address: String, date: LocalDate, amount: BigDecimal, ref: Int) {
  Bill.insert {
    it[numBill] = num
    it[addressBill] = address
    it[dateBill] = date
    it[amountWithTaxes] = amount
    it[refCmd] = ref
  }
}

fun addTasks() {
  val currentWeek = Week.now()

  addTask(currentWeek.getDate(1).toSql().toLocalDate(), LocalTime.of(8, 0, 0), LocalTime.of(10, 30, 0), "Conception", "desc 2")
  addTask(currentWeek.getDate(1).toSql().toLocalDate(), LocalTime.of(14, 0, 0), LocalTime.of(16, 0, 0), "Codage", "desc 2")
  addTask(currentWeek.getDate(4).toSql().toLocalDate(), LocalTime.of(11, 0, 0), LocalTime.of(12, 30, 0), "Validation", "desc 2")
}

fun addTask(date: LocalDate, from: LocalTime, to: LocalTime, description1: String, description2: String) {
  Task.insert {
    it[id] = TASKId.nextIntVal()
    it[Task.date] = date
    it[Task.from] = LocalDateTime.of(date, from).atZone(ZoneId.systemDefault()).toInstant()
    it[Task.to] = LocalDateTime.of(date, to).atZone(ZoneId.systemDefault()).toInstant()
    it[Task.description1] = description1
    it[Task.description2] = description2
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
