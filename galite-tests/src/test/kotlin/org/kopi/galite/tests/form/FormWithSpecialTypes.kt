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
package org.kopi.galite.tests.form

import java.util.Locale

import org.jetbrains.exposed.sql.Table

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Image

object Product : Table() {
  val name = varchar("NAME", 10)
  val price = decimal("PRICE", 10, 5)
  val image = blob("image")

}

object FormWithSpecialTypes : Form() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val edit = menu("Edit")

  val autofillitem = actor(
          ident = "Autofill",
          menu = edit,
          label = "Autofill",
          help = "Autofill",
  ) {
    key  =  Key.F12         // key is optional here
    icon = "column_chart"  // icon is optional here
  }

  val blockWithSpecialTypes = insertBlock(BlockWithSpecialTypes())
}

class BlockWithSpecialTypes : FormBlock(1, 1, "Test block") {
  val p = table(Product)

  val price = visit(domain = Domain<Decimal>(width = 10, scale = 5), position = at(1, 1)) {
    label = "price"
    help = "The price"
    minValue = Decimal.valueOf("1.9")
    columns(p.price)
  }

  val name = visit(domain = Domain<String>(width = 10), position = at(1, 2)) {
    label = "name"
    help = "The product name"
    columns(p.name)
  }

  val image = visit(domain = Domain<Image>(width = 100, height = 100 ), position = at(1, 3)) {
    label = "image"
    help = "The product image"
    columns(p.image)
  }

  init {
    price.value = Decimal.valueOf("2")
  }
}

fun main() {
  Application.runForm(formName = FormWithSpecialTypes)
}
