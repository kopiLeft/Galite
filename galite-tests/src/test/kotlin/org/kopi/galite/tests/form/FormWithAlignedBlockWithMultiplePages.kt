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

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.FormBlock

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

  val blockSimple1 = p1.insertBlock(Sample())
  val blockMultiple1 = p1.insertBlock(ProductBlock1())
  val blockTotal1 = block(1, 1, "Total", "Total block", p1) {

    val totalWithTaxes = skipped(INT(10), position = at(1, 1)) {
      label = "Total"
    }
    val totalHT = skipped(INT(5), position = at(1, 2)) {
      label = "Total HT"
    }
    align(blockMultiple1, totalWithTaxes to blockMultiple1.amountWithTaxes, totalHT to blockMultiple1.priceHT)
  }

  val blockSimple2 = p2.insertBlock(Sample())
  val blockMultiple2 = p2.insertBlock(ProductBlock2())
  val blockTotal2 = block(1, 1, "Total", "Total block", p2) {

    val totalWithTaxes = skipped(INT(10), position = at(1, 1)) {
      label = "Total"
    }
    val totalHT = skipped(INT(5), position = at(1, 2)) {
      label = "Total HT"
    }

    align(blockMultiple2, totalWithTaxes to blockMultiple2.amountWithTaxes, totalHT to blockMultiple2.priceHT)
  }

  val blockSimple3 = p3.insertBlock(Sample())
  val blockMultiple3 = p3.insertBlock(ProductBlock3())
  val blockTotal3 = block(1, 1, "Total", "Total block", p3) {

    val totalWithTaxes = skipped(INT(10), position = at(1, 1)) {
      label = "Total"
    }
    val totalHT = skipped(INT(5), position = at(1, 2)) {}

    align(blockMultiple3, totalWithTaxes to blockMultiple3.amountWithTaxes, totalHT to blockMultiple3.priceHT)
  }

  val blockSimple4 = p4.insertBlock(Sample())
  val blockMultiple4 = p4.insertBlock(ProductBlock4())
  val blockTotal4 = block(1, 1, "Total", "Total block", p4) {

    val totalRemise = skipped(INT(15), position = at(1, 1)) {
      label = "Total"
    }
    val totalHT = skipped(INT(5), position = at(1, 2)) {
      label = "Total HT"
    }

    align(blockMultiple4, totalRemise to blockMultiple4.discountAmount, totalHT to blockMultiple4.priceHT)
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
  runForm(formName = FormWithAlignedBlockWithMultiplePages())
}
