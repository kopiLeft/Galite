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
import java.time.Instant
import java.time.LocalDate

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.nextIntVal
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.bill.BillForm
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.delivery.DeliveryForm
import org.kopi.galite.demo.ordReceived.OrdReceivedForm
import org.kopi.galite.demo.order.OrderForm
import org.kopi.galite.demo.product.ProductForm
import org.kopi.galite.demo.provider.ProviderForm
import org.kopi.galite.demo.quote.QuoteForm
import org.kopi.galite.demo.stock.StockForm
import org.kopi.galite.visual.db.Modules
import org.kopi.galite.visual.db.UserRights
import org.kopi.galite.visual.db.Users
import org.kopi.galite.visual.db.databaseConfig
import org.kopi.galite.visual.db.list_Of_Tables
import org.kopi.galite.visual.db.sequencesList

const val testURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
const val testDriver = "org.h2.Driver"
const val testUser = "admin"
const val testPassword = "admin"

/**
 * Connects to the database.
 */
fun connectToDatabase(
  url: String = testURL,
  driver: String = testDriver,
  user: String = testUser,
  password: String = testPassword,
  schema: String? = null
) {
  if (schema != null) {
    Database.connect(
      url,
      driver = driver,
      user = user,
      password = password,
      databaseConfig = databaseConfig(Schema(schema))
    )
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
    addProviders()
    addClients()
    addProducts()
    addOrders()
    addBills()
    addDelivery()
    addOrdsReceived()
    addsStock()
    addQuotes()
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

val list_Of_GShopApplicationTables = listOf(
  Client,
  Product, Provider, Stocks, Order, OrdReceived, Delivery, Bill, Quote
)

val listOfSequences = listOf(

  Clientid, Providerid, Productid, Stockid, Billid, Quoteid, Orderid, OrdReceivedid, Deliveryid
)

fun initModules() {
  transaction {
    insertIntoModule("1000", "org/kopi/galite/demo/Menu", 0)
    insertIntoModule("1001", "org/kopi/galite/demo/Menu", 1, "1000", ClientForm::class)

    insertIntoModule("2000", "org/kopi/galite/demo/Menu", 100)
    insertIntoModule("2001", "org/kopi/galite/demo/Menu", 101, "2000", ProviderForm::class)


    insertIntoModule("3000", "org/kopi/galite/demo/Menu", 200)
    insertIntoModule("3001", "org/kopi/galite/demo/Menu", 201, "3000", ProductForm::class)

    insertIntoModule("4000", "org/kopi/galite/demo/Menu", 300)
    insertIntoModule("4001", "org/kopi/galite/demo/Menu", 301, "4000", BillForm::class)

    insertIntoModule("5000", "org/kopi/galite/demo/Menu", 400)
    insertIntoModule("5001", "org/kopi/galite/demo/Menu", 401, "5000", OrderForm::class)

    insertIntoModule("6000", "org/kopi/galite/demo/Menu", 500)
    insertIntoModule("6001", "org/kopi/galite/demo/Menu", 501, "6000", OrdReceivedForm::class)

    insertIntoModule("7000", "org/kopi/galite/demo/Menu", 600)
    insertIntoModule("7001", "org/kopi/galite/demo/Menu", 601, "7000", DeliveryForm::class)

    insertIntoModule("8000", "org/kopi/galite/demo/Menu", 700)
    insertIntoModule("8001", "org/kopi/galite/demo/Menu", 701, "8000", QuoteForm::class)

    insertIntoModule("9000", "org/kopi/galite/demo/Menu", 800)
    insertIntoModule("9001", "org/kopi/galite/demo/Menu", 801, "9000", StockForm::class)


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
fun insertIntoUsers(
  shortname: String,
  userName: String
) {
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
fun insertIntoUserRights(
  userName: String,
  moduleName: String,
  accessUser: Boolean
) {
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
fun insertIntoModule(
  shortname: String,
  source: String,
  priorityNumber: Int,
  parentName: String = "-1",
  className: KClass<*>? = null,
  symbolNumber: Int? = null
) {
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

fun addClient(fName: String, lName: String, email: String, tel: Int) {
  Client.insert {
    it[Client.idCl] = Clientid.nextIntVal()
    it[Client.firstNameCl] = fName
    it[Client.lastNameCl] = lName
    it[Client.emailCl] = email
    it[Client.telephoneCl] = tel
  }
}

fun addClients() {
  addClient("Amal", "Ben Hssin", "amalh@mail.com", 25853250)
  addClient("Syrine", "Ben Ahmed", "syrinea@mail.com", 28569200)
  addClient("Ahmed", "Ben Jdid", "ahmedj@mail.com", 20256905)
  addClient("Mohamed", "Ben Ahmed", "mohameda@mail.com", 23000256)
  addClient("Asma", "Ahmed", "amalh@mail.com", 25853250)
  addClient("Amal", "Ben Hssin", "asmaa@mail.com", 96002558)
  addClient("Amer", "Ben Ahmed", "amera@mail.com", 26300895)
  addClient("Samar", "Ben mohamed", "samarm@mail.com", 95623001)
}

fun addProduct(priceU: BigDecimal, priceT: BigDecimal, cat: String, name: String, qty: Int, vatP: BigDecimal) {
  Product.insert {
    //it[idPro] = Providerid.nextIntVal()
    it[Product.idP] = Productid.nextIntVal()
    it[nameP] = name
    it[qtyP] = qty
    it[Product.priceUP] = priceU
    it[Product.priceTP] = priceT
    it[Product.vatP] = vatP
    it[category] = cat
  }
}

fun addProducts() {
  addProduct(BigDecimal(100.050), BigDecimal(500.250), "Charger", "Electronic", 5, BigDecimal(1.5))
  addProduct(BigDecimal(800.250), BigDecimal(1600.500), "Telephone", "Electronic", 2, BigDecimal(1.5))
  addProduct(BigDecimal(1500.500), BigDecimal(1500.500), "Laptop", "Electronic", 1, BigDecimal(0.2))
  addProduct(BigDecimal(250.000), BigDecimal(750.000), "Table", "Fourniture", 3, BigDecimal(0.1))
  addProduct(BigDecimal(20.000), BigDecimal(100.000), "Chair", "Fourniture", 5, BigDecimal(0.1))
  addProduct(BigDecimal(5.320), BigDecimal(10.640), "Fourniture", "Fourniture", 2, BigDecimal(0.1))
  addProduct(BigDecimal(3.000), BigDecimal(15.000), "Fourniture", "Fourniture", 5, BigDecimal(0.1))
  addProduct(BigDecimal(80.000), BigDecimal(400.000), "Table", "Fourniture", 5, BigDecimal(0.1))
  addProduct(BigDecimal(15.000), BigDecimal(30.000), "Fourniture", "Fourniture", 2, BigDecimal(0.1))
}

fun addProvider(fName: String, lName: String, addr: String, accountNum: String, tel: Int) {
  Provider.insert {
    it[idPro] = Providerid.nextIntVal()
    it[firstName] = fName
    it[lastName] = lName
    it[Provider.address] = addr
    it[Provider.accountNum] = accountNum
    it[telephone] = tel
  }
}

fun addProviders() {
  addProvider("Ahmed", "Ben Hssin", "Tunis", "F-0001", 25698505)
  addProvider("Mohamed", "Ben Hssin", "Ariana", "F-0002", 26960002)
  addProvider("Ahmed", "Ben jdid", "Manouba", "F-0003", 96989600)
  addProvider("Salah", "ben yahya", "Tunis", "F-0004", 23001256)
  addProvider("Ahmed", "Ben Hssin", "Tunis", "F-0005", 96987800)
  addProvider("Amani", "Ben Hssin", "Ariana", "F-0006", 98002369)
  addProvider("Samar", "Ben Mahmoud", "Tunis", "F-0007", 58002369)
  addProvider("Tarak", "nasria", "Manouba", "F-0008", 26598002)
}

fun addBill(date: LocalDate, priceT: BigDecimal, paymentM: String, payDate: LocalDate) {
  Bill.insert {
    it[idB] = Billid.nextIntVal()
    it[Bill.date] = date
    it[Bill.priceT] = priceT
    it[Bill.payMeth] = paymentM
    it[Bill.payDate] = payDate
  }
}

fun addBills() {
  addBill(LocalDate.parse("2022-01-01"), BigDecimal(2500.000), "Carte", LocalDate.parse("2022-06-02"))
  addBill(LocalDate.parse("2022-01-15"), BigDecimal(200.000), "Carte", LocalDate.parse("2022-02-15"))
  addBill(LocalDate.parse("2022-01-16"), BigDecimal(2500.000), "Carte", LocalDate.parse("2022-02-15"))
  addBill(LocalDate.parse("2022-01-21"), BigDecimal(289.500), "Carte", LocalDate.parse("2022-02-07"))
  addBill(LocalDate.parse("2022-09-08"), BigDecimal(4500.000), "Carte", LocalDate.parse("2022-10-05"))
}

fun addOrder(date: LocalDate, qty: Int) {
  Order.insert {

    it[idOrd] = Orderid.nextIntVal()
    it[qtyOrd] = qty
    it[dateOrd] = date

  }
}

fun addOrders() {
  addOrder(LocalDate.parse("2022-01-05"), 2)
  addOrder(LocalDate.parse("2022-01-05"), 3)
  addOrder(LocalDate.parse("2022-05-08"), 5)
  addOrder(LocalDate.parse("2022-04-25"), 7)
  addOrder(LocalDate.parse("2022-03-04"), 2)
  addOrder(LocalDate.parse("2022-08-02"), 5)
  addOrder(LocalDate.parse("2022-06-20"), 2)
  addOrder(LocalDate.parse("2022-04-02"), 1)
}

fun addDel(name: String, delay: LocalDate) {
  Delivery.insert {
    it[idD] = Deliveryid.nextIntVal()
    it[Delivery.name] = name
    it[Delivery.delay] = delay
  }
}

fun addDelivery() {
  addDel(("Mohamed Amine "), LocalDate.parse("2022-05-01"))
  addDel(("Ahmed Nasry"), LocalDate.parse("2022-01-11"))
  addDel(("Samar Mohamed"), LocalDate.parse("2022-03-02"))
  addDel(("Amani Louti"), LocalDate.parse("2022-01-05"))
  addDel(("Saher Ahmed"), LocalDate.parse("2022-01-28"))
}

fun addOrdReceived(total: BigDecimal) {
  OrdReceived.insert {
    it[idRec] = OrdReceivedid.nextIntVal()
    it[OrdReceived.total] = total
  }
}

fun addOrdsReceived() {
  addOrdReceived(BigDecimal(500.600))
  addOrdReceived(BigDecimal(6800.350))
  addOrdReceived(BigDecimal(25.300))
  addOrdReceived(BigDecimal(450.000))
  addOrdReceived(BigDecimal(730.100))
  addOrdReceived(BigDecimal(700.000))
  addOrdReceived(BigDecimal(900.000))
  addOrdReceived(BigDecimal(200.100))
  addOrdReceived(BigDecimal(60.000))
  addOrdReceived(BigDecimal(600.000))
}

fun addStock(qty: Int, type: String) {
  Stocks.insert {
    it[idS] = Stockid.nextIntVal()

    it[Stocks.qty] = qty
    it[Stocks.type] = type
  }
}

fun addsStock() {
  addStock(2, "Input")
  addStock(8, "Input")
  addStock(20, "Input")
  addStock(30, "Input")
  addStock(40, "Input")
  addStock(10, "Input")
  addStock(9, "Output")
  addStock(20, "Output")
  addStock(15, "Output")
  addStock(6, "Output")
  addStock(7, "Output")
}

fun addQuote(dates: LocalDate) {
  Quote.insert {
    it[idQ] = Quoteid.nextIntVal()
    it[date] = dates
  }
}

fun addQuotes() {
  addQuote(LocalDate.parse("2022-05-01"))
  addQuote(LocalDate.parse("2022-10-06"))
  addQuote(LocalDate.parse("2022-01-20"))
  addQuote(LocalDate.parse("2022-01-11"))
  addQuote(LocalDate.parse("2022-01-17"))
  addQuote(LocalDate.parse("2022-06-08"))
}