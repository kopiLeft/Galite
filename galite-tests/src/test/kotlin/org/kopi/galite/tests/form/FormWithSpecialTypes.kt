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
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.db.month
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.IMAGE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.MONTH
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TIMESTAMP
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.type.Timestamp

object Product : Table() {
  val id = integer("ID").autoIncrement().nullable()
  val uc = integer("UC")
  val ts = integer("TS")
  val name = varchar("NAME", 10)
  val price = decimal("PRICE", 10, 5).nullable()
  val image = blob("IMAGE")
  val date = timestamp("MAN_DATE")
  val month = month("EXP_MONTH").nullable()
}

class FormWithSpecialTypes : Form(title = "form for test", locale = Locale.UK) {

  val action = menu("Action")
  val edit = menu("Edit")

  val save = actor(
          menu = action,
          label = "save",
          help = "save",
  ) {
    key = Key.F2
    icon = Icon.SAVE
  }

  val autofillitem = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  ) {
    key = Key.F12
    icon = Icon.COLUMN_CHART
  }


  val blockWithSpecialTypes = insertBlock(BlockWithSpecialTypes()) {
    command(item = save) {
      setMode(Mode.INSERT)
      transaction {
        SchemaUtils.create(p)
        saveBlock()
        p.selectAll().forEach {
          println("Image successfully inserted!")
          println(it[p.image])
        }
      }
    }
  }
}

class BlockWithSpecialTypes : Block("Test block", 1, 1) {
  val p = table(Product)
  val i = index(message = "ID should be unique")

  val id = hidden(domain = INT(20)) {
    label = "id"
    help = "The user id"
    columns(p.id)
  }
  val uc = hidden(domain = INT(20)) {
    label = "uc"
    columns(p.uc)
  }

  val ts = hidden(domain = INT(20)) {
    label = "ts"
    columns(p.ts)
  }

  val price = visit(domain = DECIMAL(width = 10, scale = 5), position = at(1, 1)) {
    label = "price"
    help = "The price"
    minValue = Decimal.valueOf("1.9")
    columns(p.price)
  }

  val name = visit(domain = STRING(width = 10), position = at(1, 2)) {
    label = "name"
    help = "The product name"
    columns(p.name)
  }

  val image = visit(domain = IMAGE(width = 100, height = 100), position = at(1, 3)) {
    label = "image"
    help = "The product image"
    columns(p.image)
  }

  val date = mustFill(domain = TIMESTAMP, position = at(2, 1)) {
    label = "Date"
    help = "The date of manufacturing"
    columns(p.date)
  }

  val expiration = visit(domain = MONTH, position = at(3, 1)) {
    label = "Expiration"
    help = "The expiration month"
    columns(p.month)
  }

  init {
    price.value = Decimal.valueOf("2")
    id.value = 1
    uc.value = 0
    ts.value = 0
    expiration.value = null
    date.value = Timestamp.now()
    expiration.value = Month.now()
  }
}

fun main() {
  runForm(formName = FormWithSpecialTypes())
}
