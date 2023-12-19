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
package org.kopi.galite.tests.form

import java.util.Locale

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key

object Clients : Table() {
  val id = integer("ID")
  val uc = integer("UC")
  val ts = integer("TS")
  val name = varchar("NAME", 20)
  val mail = varchar("MAIL", 20).nullable()

  override val primaryKey = PrimaryKey(id)
}

object Order : Table() {
  val id = integer("ID")
  val uc = integer("UC")
  val ts = integer("TS")
  val quantity = integer("QUANTITY")
  val client_id = integer("CLIENT_ID").references(Clients.id)
  val product_id = integer("PRODUCT_ID").references(Products.id)
  val address_id = integer("ADDRESS_ID").references(Adress.id)
  override val primaryKey = PrimaryKey(id)
}

object Products : Table() {
  val id = integer("ID")
  val uc = integer("UC")
  val ts = integer("TS")
  val description = varchar("DESCRIPTION", 20)

  override val primaryKey = PrimaryKey(id)
}

object Adress : Table() {
  val id = integer("ID")
  val uc = integer("UC")
  val ts = integer("TS")
  val country = varchar("COUNTRY", 20)
  val zip = varchar("ZIP", 20)
  val client_id = integer("CLIENT_ID").references(Clients.id)

  override val primaryKey = PrimaryKey(id)
}

object FormWithNullableColumn : DictionaryForm(title = "form for test nullable ", locale = Locale.UK) {

  val list = actor(menu = actionMenu, label = "list", help = "Display List", ) {
    key = Key.F1   // key is optional here
    icon = Icon.LIST
  }

  val autoFill = actor(menu = editMenu, label = "Autofill", help = "Autofill", command = PredefinedCommand.AUTOFILL)

  val resetBlock = actor(menu = editMenu, label = "break", help = "Reset Block", ) {
    key = Key.F3
    icon = Icon.BREAK
  }

  val p1 = page("test page")

  val preform = trigger(INIT) {
    transaction {
      SchemaUtils.create(Clients)
      SchemaUtils.create(Order)
      SchemaUtils.create(Products)

      Clients.insert {
        it[id] = 1
        it[uc] = 0
        it[ts] = 0
        it[name] = "client1"
        it[mail] = "client1@kopi.com"
      }

      Clients.insert {
        it[id] = 2
        it[uc] = 0
        it[ts] = 0
        it[name] = "client2"
        it[mail] = "client2@kopi.com"
      }

      Products.insert {
        it[id] = 1
        it[uc] = 0
        it[ts] = 0
        it[description] = "produit de test"
      }

      Adress.insert{
        it[id] = 1
        it[uc] = 0
        it[ts] = 0
        it[zip] = "1000"
        it[country] = "Tunisie"
        it[client_id] = 1
      }

      Order.insert {
        it[id] = 1
        it[uc] = 0
        it[ts] = 0
        it[quantity] = 10
        it[client_id] = 1
        it[product_id] = 1
        it[address_id] = 1
      }
    }
  }

  val FormWithList = FormWithList()

  val blockWithTwoTablesInnerJoin = p1.insertBlock(FormWithTwoTablesInnerJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockWithTwoTablesLeftJoin = p1.insertBlock(FormWithTwoTablesLeftJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockWithThreeTablesLeftJoin = p1.insertBlock(FormWithThreeTablesLeftJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockWithThreeTablesInnerJoin = p1.insertBlock(FormWithThreeTablesInnerJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockWithThreeTablesInnerJoinInOneField = p1.insertBlock(FormWithThreeTablesInnerJoinInField()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockWithThreeTablesLeftJoinInOneField = p1.insertBlock(FormWithThreeTablesLeftJoinInField()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockThreeTablesMiddleLeftJoinInOneField = p1.insertBlock(FormThreeTablesMiddleLeftJoinInField()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockThreeTablesEndLeftJoinInOneField = p1.insertBlock(FormThreeTablesEndLeftJoinInField()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockFourTablesLeftInnerLeftJoin = p1.insertBlock(FormFourTablesLeftInnerLeftJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockFourTablesInnerLeftInnerJoin = p1.insertBlock(FormFourTablesInnerLeftInnerJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockTwoTablesInnerJoinNoJoin = p1.insertBlock(FormTwoTablesInnerJoinNoJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockTwoTablesNoJoinInnerJoin = p1.insertBlock(FormTwoTablesNoJoinInnerJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockTwoTablesLeftJoinNoJoin = p1.insertBlock(FormTwoTablesLeftJoinNoJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }

  val blockTwoTablesNoJoinLeftJoin = p1.insertBlock(FormTwoTablesNoJoinLeftJoin()) {
    command(item = FormWithList.list) {
      recursiveQuery()
    }

    command(item = FormWithList.resetBlock) {
      resetBlock()
    }
  }
}

class FormWithTwoTablesInnerJoin :  Block("Inner Join Two Tables Test", 1, 1) {
  val c = table(Clients)
  val o = table(Order)

  val id_user = skipped(domain = INT(20), position = at(1, 1)) {
    columns(c.id, o.client_id)
  }

  val name = visit(domain = STRING(20), position = at(2, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(2, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(3, 1)) {
    columns(o.quantity)
  }
}

class FormWithTwoTablesLeftJoin :  Block("Left Join Two Tables Test", 1, 1) {
  val c = table(Clients)
  val o = table(Order)

  val id_user = skipped(domain = INT(20), position = at(1, 1)) {
    columns(o.client_id, nullable(c.id))
  }

  val name = visit(domain = STRING(20), position = at(2, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(2, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(3, 1)) {
    columns(o.quantity)
  }
}

class FormWithThreeTablesLeftJoin : Block("Left Join three Tables Test", 1, 1) {
  val o = table(Order)
  val c = table(Clients)
  val p = table(Products)

  val id_order = skipped(domain = INT(20), position = at(1, 1)) {
    columns(o.id)
  }

  val id_user = skipped(domain = INT(20), position = at(2, 1)) {
    columns(c.id, nullable(o.client_id))
  }
  val id_product = skipped(domain = INT(20), position = at(3, 1)) {
    columns(p.id, nullable(o.product_id))
  }
  val ts = hidden(domain = INT(20)) {
    columns(c.ts)
  }
  val uc = hidden(domain = INT(20)) {
    columns(c.uc)
  }
  val name = visit(domain = STRING(20), position = at(4, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(4, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(5, 1)) {
    columns(o.quantity)
  }

  val productName = visit(domain = STRING(20), position = at(6, 1)) {
    columns(p.description)
  }
}

class FormWithThreeTablesInnerJoin : Block("Inner Join three Tables Test", 1, 1) {
  val o = table(Order)
  val c = table(Clients)
  val p = table(Products)

  val id_order = skipped(domain = INT(20), position = at(1, 1)) {
    columns(o.id)
  }

  val id_user = skipped(domain = INT(20), position = at(2, 1)) {
    columns(c.id, o.client_id)
  }
  val id_product = skipped(domain = INT(20), position = at(3, 1)) {
    columns(p.id, o.product_id)
  }
  val ts = hidden(domain = INT(20)) {
    columns(c.ts)
  }
  val uc = hidden(domain = INT(20)) {
    columns(c.uc)
  }
  val name = visit(domain = STRING(20), position = at(4, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(4, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(5, 1)) {
    columns(o.quantity)
  }

  val productName = visit(domain = STRING(20), position = at(6, 1)) {
    columns(p.description)
  }
}

class FormWithThreeTablesInnerJoinInField : Block("Inner Join three Tables in Field Test", 1, 1) {
  val o = table(Order)
  val c = table(Clients)
  val a = table(Adress)

  val id_order = skipped(domain = INT(20), position = at(1, 1)) {
    columns(o.id)
  }

  val id_user = skipped(domain = INT(20), position = at(2, 1)) {
    columns(c.id, o.client_id, a.client_id)
  }

  val ts = hidden(domain = INT(20)) {
    columns(c.ts)
  }
  val uc = hidden(domain = INT(20)) {
    columns(c.uc)
  }
  val name = visit(domain = STRING(20), position = at(4, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(4, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(5, 1)) {
    columns(o.quantity)
  }

  val country = visit(domain = STRING(20), position = at(6, 1)) {
    columns(a.country)
  }

  val zipCode = visit(domain = STRING(20), position = at(6, 2)) {
    columns(a.zip)
  }
}

class FormWithThreeTablesLeftJoinInField : Block("Left Join three Tables in Field Test", 1, 1) {
  val o = table(Order)
  val c = table(Clients)
  val a = table(Adress)

  val id_order = skipped(domain = INT(20), position = at(1, 1)) {
    columns(o.id)
  }

  val id_user = skipped(domain = INT(20), position = at(2, 1)) {
    columns(c.id, nullable(o.client_id), nullable(a.client_id))
  }

  val ts = hidden(domain = INT(20)) {
    columns(c.ts)
  }
  val uc = hidden(domain = INT(20)) {
    columns(c.uc)
  }
  val name = visit(domain = STRING(20), position = at(4, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(4, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(5, 1)) {
    columns(o.quantity)
  }

  val country = visit(domain = STRING(20), position = at(6, 1)) {
    columns(a.country)
  }

  val zipCode = visit(domain = STRING(20), position = at(6, 2)) {
    columns(a.zip)
  }
}

class FormThreeTablesMiddleLeftJoinInField : Block("Left Join in middle of field three Tables Test", 1, 1) {
  val o = table(Order)
  val c = table(Clients)
  val a = table(Adress)

  val id_order = skipped(domain = INT(20), position = at(1, 1)) {
    columns(o.id)
  }

  val id_user = skipped(domain = INT(20), position = at(2, 1)) {
    columns(c.id, nullable(o.client_id), a.client_id)
  }

  val ts = hidden(domain = INT(20)) {
    columns(c.ts)
  }
  val uc = hidden(domain = INT(20)) {
    columns(c.uc)
  }
  val name = visit(domain = STRING(20), position = at(4, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(4, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(5, 1)) {
    columns(o.quantity)
  }

  val country = visit(domain = STRING(20), position = at(6, 1)) {
    columns(a.country)
  }

  val zipCode = visit(domain = STRING(20), position = at(6, 2)) {
    columns(a.zip)
  }
}

class FormThreeTablesEndLeftJoinInField : Block("Left Join in the end of the field three Tables Test", 1, 1) {
  val o = table(Order)
  val c = table(Clients)
  val a = table(Adress)

  val id_order = skipped(domain = INT(20), position = at(1, 1)) {
    columns(o.id)
  }

  val id_user = skipped(domain = INT(20), position = at(2, 1)) {
    columns(c.id, a.client_id, nullable(o.client_id))
  }

  val ts = hidden(domain = INT(20)) {
    columns(c.ts)
  }
  val uc = hidden(domain = INT(20)) {
    columns(c.uc)
  }
  val name = visit(domain = STRING(20), position = at(4, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(4, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(5, 1)) {
    columns(o.quantity)
  }

  val country = visit(domain = STRING(20), position = at(6, 1)) {
    columns(a.country)
  }

  val zipCode = visit(domain = STRING(20), position = at(6, 2)) {
    columns(a.zip)
  }
}

class FormFourTablesLeftInnerLeftJoin : Block("Left then inner then left join Test", 1, 1) {
  val o = table(Order)
  val c = table(Clients)
  val a = table(Adress)
  val p = table(Products)

  val id_user = skipped(domain = INT(20), position = at(1, 1)) {
    columns(c.id, nullable(o.client_id))
  }
  val id_address = skipped(domain = INT(20), position = at(2, 1)) {
    columns( a.id, o.address_id)
  }
  val id_product = skipped(domain = INT(20), position = at(3, 1)) {
    columns( p.id , nullable(o.product_id))
  }
  val name = visit(domain = STRING(20), position = at(4, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(4, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(5, 1)) {
    columns(o.quantity)
  }
}

class FormFourTablesInnerLeftInnerJoin : Block("Inner then left then inner join Test", 1, 1) {
  val a = table(Adress)
  val o = table(Order)
  val c = table(Clients)
  val p = table(Products)

  val id_address = skipped(domain = INT(20), position = at(2, 1)) {
    columns(a.id, o.address_id)
  }
  val id_user = skipped(domain = INT(20), position = at(1, 1)) {
    columns(nullable(c.id), nullable(o.client_id))
  }
  val id_product = skipped(domain = INT(20), position = at(3, 1)) {
    columns(o.product_id, p.id)
  }
  val name = visit(domain = STRING(20), position = at(4, 1)) {
    columns(c.name)
  }

  val mail = visit(domain = STRING(20), position = at(4, 2)) {
    columns(c.mail)
  }

  val quantity = visit(domain = INT(10), position = at(5, 1)) {
    columns(o.quantity)
  }
}

class FormTwoTablesInnerJoinNoJoin : Block("Inner join then no-join Test", 1, 1) {
  val a = table(Adress)
  val o = table(Order)

  val id_address = skipped(domain = INT(20), position = at(2, 1)) {
    columns(a.id, o.address_id)
  }
  val id_user = visit(domain = INT(20), position = at(1, 1)) {
    columns(o.client_id)
  }
}

class FormTwoTablesNoJoinInnerJoin : Block("No join then inner join Test", 1, 1) {
  val a = table(Adress)
  val o = table(Order)

  val id_user = skipped(domain = INT(20), position = at(1, 1)) {
    columns(o.client_id)
  }
  val id_address = visit(domain = INT(20), position = at(2, 1)) {
    columns(a.id, o.address_id)
  }
}

class FormTwoTablesLeftJoinNoJoin : Block("Left join then no-join Test", 1, 1) {
  val a = table(Adress)
  val o = table(Order)

  val id_client = skipped(domain = INT(20), position = at(2, 1)) {
    columns(a.client_id, o.client_id)
  }
  val quantity = visit(domain = INT(20), position = at(2, 1)) {
    columns(o.quantity)
  }
}

class FormTwoTablesNoJoinLeftJoin : Block("No join then left join Test", 1, 1) {
  val a = table(Adress)
  val o = table(Order)

  val quantity = skipped(domain = INT(20), position = at(2, 1)) {
    columns(o.quantity)
  }
  val id_client = visit(domain = INT(20), position = at(2, 1)) {
    columns(a.client_id, o.client_id)
  }
}


fun main() {
  runForm(formName = FormWithNullableColumn)
}
