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
package org.kopi.galite.demo.product

import java.util.Locale

import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.desktop.runForm

import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.IMAGE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm

class ProductForm : ReportSelectionForm() {
  override val locale = Locale.UK
  override val title = "Products"
  val page = page("Product")
  val action = menu("Action")
  val edit = menu("Edit")
  val autoFill = actor(
          ident = "Autofill",
          menu = edit,
          label = "Autofill",
          help = "Autofill",
  )
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "report"  // icon is optional here
  }

  val block = page.insertBlock(BlockProduct())

  inner class BlockProduct : Block(1, 1, "Products") {
    val u = table(Product)

    val idPdt = hidden(domain = INT(20)) {
      label = "ID"
      help = "The product ID"
      columns(u.idPdt)
    }
    val description = mustFill(domain = STRING(50), position = at(1, 1)) {
      label = "Description"
      help = "The product description"
      columns(u.description)
    }
    val price = visit(domain = DECIMAL(20, 10), follow(description)) {
      label = "Price"
      help = "The product unit price excluding VAT"
      columns(u.price)
    }
    val category = mustFill(domain = Category, position = at(2, 1)) {
      label = "Category"
      help = "The product category"
      columns(u.category)
    }
    val taxName = mustFill(domain = Tax, position = at(3, 1)) {
      label = "Tax"
      help = "The product tax name"
      columns(u.taxName)
    }
    val photo = visit(domain = IMAGE(width = 100, height = 100), position = at(5, 1)) {
      label = "Image"
      help = "The product image"
    }

    init {
      blockVisibility(Access.VISIT, Mode.QUERY)

      command(item = report) {
        createReport(ProductReport())
      }
    }
  }
}

object Category : CodeDomain<Int>() {
  init {
    "shoes" keyOf 1
    "shirts" keyOf 2
    "glasses" keyOf 3
    "pullovers" keyOf 4
    "jeans" keyOf 5
  }
}

object Tax : CodeDomain<String>() {
  init {
    "0%"  keyOf "tax 0"
    "19%" keyOf "tax 1"
    "9%" keyOf "tax 2"
    "13%" keyOf "tax 3"
    "22%" keyOf "tax 4"
    "11%" keyOf "tax 5"
  }
}

fun main() {
  runForm(formName = ProductForm())
}
