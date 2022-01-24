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
import org.jetbrains.exposed.sql.javatime.timestamp

object Client : Table("CLIENTS") {
  val idClt = integer("ID").autoIncrement()
  val firstNameClt = varchar("FIRSTNAME", 25)
  val lastNameClt = varchar("LASTNAME", 25)
  val addressClt = varchar("ADDRESS", 50)
  val ageClt = integer("AGE")
  val mail = varchar("EMAIL", 25)
  val countryClt = varchar("COUNTRY", 30)
  val cityClt = varchar("CITY", 30)
  val zipCodeClt = integer("ZIP_CODE")
  val activeClt = bool("ACTIVE")

  override val primaryKey = PrimaryKey(idClt, name = "PK_CLIENT_ID")
}

object Purchase: Table("PURCHASE") {
  val id = integer("ID").autoIncrement()
  val idClt = integer("CLIENT").references(Client.idClt)
  val idPdt = integer("PRODUCT").references(Product.idPdt)
  val quantity = integer("QUANTITY")
}

object Product : Table("PRODUCTS") {
  val idPdt = integer("ID").autoIncrement()
  val description = varchar("DESCRIPTION", 50)
  val category = integer("CATEGORY")
  val department = varchar("DEPARTMENT", 10)
  val supplier = varchar("SUPPLIER", 20)
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
  val numCmd = integer("ID").autoIncrement()
  val idClt = integer("CLIENT_ID").references(Client.idClt)
  val dateCmd = date("COMMAND_DATE")
  val paymentMethod = varchar("PAYMENT_METHOD", 50)
  val statusCmd = varchar("COMMAND_STATUS", 30)

  override val primaryKey = PrimaryKey(numCmd)
}

object Bill : Table("BILLS") {
  val numBill = integer("BILL_NUMBER").autoIncrement()
  val addressBill = varchar("BILL_ADDRESS", 50)
  val dateBill = date("BILL_DATE")
  val amountWithTaxes = decimal("AMOUNT_TO_PAY", 9, 3).references(BillProduct.amountWithTaxes)
  val refCmd = integer("COMMAND_REFERENCE").references(Command.numCmd)

  override val primaryKey = PrimaryKey(numBill)
}

object Task : Table("Task") {
  val id = integer("ID").autoIncrement()
  val date = date("DATE").nullable()
  val from = timestamp("FROM")
  val to = timestamp("TO")
  val description1 = varchar("DESCRIPTION_1", 20)
  val description2 = varchar("DESCRIPTION_2", 20)
  val uc = integer("UC").autoIncrement()
  val ts = integer("TS").autoIncrement()

  override val primaryKey = PrimaryKey(id)
}

object TaxRule : Table("TAX_RULE") {
  val idTaxe = integer("ID").autoIncrement()
  val taxName = varchar("NAME", 20)
  val rate = integer("RATE_IN_PERCENTAGE")
  val informations = varchar("INFORMATION", 200).nullable()

  override val primaryKey = PrimaryKey(idTaxe)
}

val TASKId = Sequence("TASKId")
