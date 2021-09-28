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
package org.kopi.galite.tests.form

import java.util.Locale

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FormBlock
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

object FormWithNullableColumn : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "form for test nullable "

  val action = menu("Action")

  val edit = menu("Edit")

  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F1   // key is optional here
    icon = "list"  // icon is optional here
  }

  val autoFill = actor(
          ident = "Autofill",
          menu = edit,
          label = "Autofill",
          help = "Autofill",
  )

  val resetBlock = actor(
          ident = "reset",
          menu = edit,
          label = "break",
          help = "Reset Block",
  ) {
    key = Key.F3   // key is optional here
    icon = "break"  // icon is optional here
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

      Order.insert {
        it[id] = 1
        it[uc] = 0
        it[ts] = 0
        it[quantity] = 10
        it[client_id] = 1
        it[product_id] = 1
      }
    }
  }

  val FormWithList = FormWithList()

  val blockWithTwoTablesInnerJoin = insertBlock(FormWithTwoTablesInnerJoin(), p1) {
    command(item = FormWithList.list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = FormWithList.resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  val blockWithTwoTablesLeftJoin = insertBlock(FormWithTwoTablesLeftJoin(), p1) {
    command(item = FormWithList.list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = FormWithList.resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  val blockWithThreeTablesLeftJoin = insertBlock(FormWithThreeTablesLeftJoin(), p1) {
    command(item = FormWithList.list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = FormWithList.resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  val blockWithThreeTablesInnerJoin = insertBlock(FormWithThreeTablesInnerJoin(), p1) {
    command(item = FormWithList.list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = FormWithList.resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  val blockWithThreeTablesInnerJoinInOneField = insertBlock(FormWithThreeTablesInnerJoinInField(), p1) {
    command(item = FormWithList.list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = FormWithList.resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  val blockWithThreeTablesLeftJoinInOneField = insertBlock(FormWithThreeTablesLeftJoinInField(), p1) {
    command(item = FormWithList.list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = FormWithList.resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  val blockThreeTablesMiddleLeftJoinInOneField = insertBlock(FormThreeTablesMiddleLeftJoinInField(), p1) {
    command(item = FormWithList.list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = FormWithList.resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  val blockThreeTablesEndLeftJoinInOneField = insertBlock(FormThreeTablesEndLeftJoinInField(), p1) {
    command(item = FormWithList.list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = FormWithList.resetBlock) {
      action = {
        resetBlock()
      }
    }
  }
}

class FormWithTwoTablesInnerJoin :  FormBlock(1, 1, "Inner Join Two Tables Test") {
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

class FormWithTwoTablesLeftJoin :  FormBlock(1, 1, "Left Join Two Tables Test") {
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

class FormWithThreeTablesLeftJoin : FormBlock(1, 1, "Left Join three Tables Test") {
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

class FormWithThreeTablesInnerJoin : FormBlock(1, 1, "Inner Join three Tables Test") {
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

class FormWithThreeTablesInnerJoinInField : FormBlock(1, 1, "Inner Join three Tables in Field Test") {
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

class FormWithThreeTablesLeftJoinInField : FormBlock(1, 1, "Left Join three Tables in Field Test") {
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

class FormThreeTablesMiddleLeftJoinInField : FormBlock(1, 1, "Left Join in middle of field three Tables Test") {
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

class FormThreeTablesEndLeftJoinInField : FormBlock(1, 1, "Left Join in the end of the field three Tables Test") {
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

fun main() {
  Application.runForm(formName = FormWithNullableColumn)
}
