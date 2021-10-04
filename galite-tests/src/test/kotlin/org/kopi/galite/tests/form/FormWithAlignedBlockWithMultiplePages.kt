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

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.insertBlock

class FormWithAlignedBlockWithMultiplePages : Form() {
  override val locale = Locale.UK
  override val title = "form for test"

  val p1 = page("page 1")
  val p2 = page("page 2")
  val p3 = page("page 3")
  val p4 = page("page 4")
  val p5 = page("page 5")
  val p6 = page("page 6")
  val p7 = page("page 7")

  val blockSimpel_1 = p1.insertBlock(Sample())
  val blockMultiple_1 = p1.insertBlock(ProductBlock1())
  val blockTotal_1 = block(1, 1, "Total", "Total block", p1) {

    val TotalWithTaxes = skipped(INT(10), position = at(1, 1)) {
      label = "Total"
    }
    val TotalHT = skipped(INT(5), position = at(1, 2)) {}

    align(blockMultiple_1, TotalWithTaxes to blockMultiple_1.amountWithTaxes, TotalHT to blockMultiple_1.priceHT)
  }

  val blockSimpel_2 = p2.insertBlock(Sample())
  val blockMultiple_2 = p2.insertBlock(ProductBlock2())
  val blockTotal_2 = block(1, 1, "Total", "Total block", p2) {

    val TotalWithTaxes = skipped(INT(10), position = at(1, 1)) {
      label = "Total"
    }
    val TotalHT = skipped(INT(5), position = at(1, 2)) {}

    align(blockMultiple_2, TotalWithTaxes to blockMultiple_2.amountWithTaxes, TotalHT to blockMultiple_2.priceHT)
  }

  val blockSimpel_3 = p3.insertBlock(Sample())
  val blockMultiple_3 = p3.insertBlock(ProductBlock3())
  val blockTotal_3 = block(1, 1, "Total", "Total block", p3) {

    val TotalWithTaxes = skipped(INT(10), position = at(1, 1)) {
      label = "Total"
    }
    val TotalHT = skipped(INT(5), position = at(1, 2)) {}

    align(blockMultiple_3, TotalWithTaxes to blockMultiple_3.amountWithTaxes, TotalHT to blockMultiple_3.priceHT)
  }

  val blockSimpel_4 = p4.insertBlock(Sample())
  val blockMultiple_4 = p4.insertBlock(ProductBlock4())
  val blockTotal_4 = block(1, 1, "Total", "Total block", p4) {

    val SommeRemise = skipped(INT(15), position = at(1, 1)) {
      label = "Total"
    }
    val TotalHT = skipped(INT(5), position = at(1, 2)) {}

    align(blockMultiple_4, SommeRemise to blockMultiple_4.discountAmount, TotalHT to blockMultiple_4.priceHT)
  }
}

class ProductBlock1 : FormBlock(10, 5, "ProductBlock") {
  val idProduct = visit(domain = INT(10), position = at(1, 1)) {
    label = "ID Prod"
  }
  val amountWithTaxes = visit(domain = INT(10), position = at(1, 2)) {
    label = "Amount Tax incl."
  }
  val priceHT = visit(domain = INT(5), position = at(1, 3)) {
    label = "price HT"
  }
}

class ProductBlock2 : FormBlock(10, 5, "ProductBlock") {
  init {
    options(BlockOption.NODETAIL)
  }

  val idProduct = visit(domain = INT(10), position = at(1, 1)) {
    label = "ID Prod"
  }
  val amountWithTaxes = visit(domain = INT(10), position = at(1, 2)) {
    label = "Amount Tax incl."
  }
  val priceHT = visit(domain = INT(5), position = at(1, 3)) {
    label = "price HT"
  }
}

class ProductBlock3 : FormBlock(10, 5, "ProductBlock") {
  val idProduct = visit(domain = INT(10), position = at(1, 1)) {
    label = "ID Prod"
  }
  val designation = visit(domain = STRING(20), position = at(1, 2)) {
    label = "designation"
  }
  val type = visit(domain = STRING(10), position = at(1, 3)) {
    label = "Type"
  }
  val amountWithTaxes = visit(domain = INT(10), position = at(1, 4)) {
    label = "Amount Tax incl."
  }
  val priceHT = visit(domain = INT(5), position = at(1, 5)) {
    label = "price HT"
  }
}

class ProductBlock4 : FormBlock(10, 5, "ProductBlock") {
  val idProduct = visit(domain = INT(10), position = at(1, 1)) {
    label = "ID Prod"
  }
  val designation = visit(domain = STRING(20), position = at(1, 2)) {
    label = "designation"
  }
  val type = visit(domain = STRING(10), position = at(1, 3)) {
    label = "Type"
  }
  val discountAmount = visit(domain = INT(15), position = at(1, 4)) {
    label = "Discount amount"
  }
  val priceTTC = visit(domain = INT(10), position = at(1, 5)) {
    label = "Amount Tax incl."
  }
  val priceHT = visit(domain = INT(5), position = at(1, 6)) {
    label = "price HT"
  }
}

class Sample : FormBlock(1, 1, "Sample") {
  val id = visit(domain = INT(10), position = at(1, 1)) {
    label = "ID"
  }
}

fun main() {
  Application.runForm(formName = FormWithAlignedBlockWithMultiplePages())
}
