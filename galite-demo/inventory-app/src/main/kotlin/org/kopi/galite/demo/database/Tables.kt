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

import org.jetbrains.exposed.sql.Sequence
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object Client : Table("CLIENT") {
  val idCl = integer("ID")
  val firstNameCl = varchar("FIRSTNAME", 25)
  val lastNameCl = varchar("LASTNAME", 25)
  val telephoneCl = integer("TELEPHONE")
  val emailCl = varchar("EMAIL", 20)
  override val primaryKey = PrimaryKey(idCl)
}

object Order : Table("ORDER") {
  val idOrd = integer("ID")
  val qtyOrd = integer("QUANTITY_ORDER")
  val dateOrd = date("DATE_ORDER")
  override val primaryKey = PrimaryKey(Order.idOrd)
}

object Provider : Table("PROVIDER") {
  val idPro = integer("ID")
  val firstName = varchar("FIRSTNAME", 15)
  val lastName = varchar("LASTNAME", 15)
  val address = varchar("ADDRESS", 15)
  val accountNum = varchar("ACCOUNT_NUMBER", 15)
  val telephone = integer("TELEPHONE")
  override val primaryKey = PrimaryKey(Provider.idPro)
}

object Product : Table("PRODUCT") {
  val idP = integer("ID")
  val nameP = varchar("NAME_PRODUCT", 10)
  val qtyP = integer("QUANTITY_PRODUCT")
  val vatP = decimal("VALUE_ADD_TAX_PRODUCT", 2, 1)
  val priceUP = decimal("PRICE_UNITY ", 10, 5)
  val priceTP = decimal("PRICE/TOTAL", 10, 5)
  val category = varchar("CATEGORY", 10)
  override val primaryKey = PrimaryKey(idP)
}

object Bill : Table("BILL") {
  val idB = integer("ID")
  val date = date("DATE")
  val priceT = decimal("TOTAL_PRICE", 10, 5)
  val payMeth = varchar("PAYMENT_METHOD", 10)
  val payDate = date("PAYMENT_DATE")
  override val primaryKey = PrimaryKey(Bill.idB)
}

object Delivery : Table("DELIVERY") {
  val idD = integer("ID")
  val name = varchar("NAME", 20)
  val delay = date("DELAY")
  override val primaryKey = PrimaryKey(Delivery.idD)
}

object OrdReceived : Table("ORDRED_RECEIVED") {
  val idRec = integer("ID")
  val total = decimal("TOTAL", 10, 5)
  override val primaryKey = PrimaryKey(OrdReceived.idRec)
}

object Stocks : Table("STOCK") {
  val idS = integer("ID")
  val qty = integer("QUANTITY")
  val type = varchar("TYPE_ARTICLE", 15)
  override val primaryKey = PrimaryKey(Stocks.idS)
}

object Quote : Table("QUOTE") {
  val idQ = integer("ID")
  val date = date("Date")
  override val primaryKey = PrimaryKey(Quote.idQ)
}

/**
 * Sequences
 */
val Clientid = Sequence("ClientId")
val Providerid = Sequence("ProviderId")
val Productid = Sequence("ProductId")
val Stockid = Sequence("StockId")
val Billid = Sequence("BillId")
val Quoteid = Sequence("QuoteId")
val Orderid = Sequence("OrderId")
val OrdReceivedid = Sequence("OrdReceivedId")
val Deliveryid = Sequence("DeliveryId")
