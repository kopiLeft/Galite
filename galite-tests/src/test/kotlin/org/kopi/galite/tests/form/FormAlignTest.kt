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

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock

object FormAlignTest : Form() {
  override val locale = Locale.UK
  override val title = "form for test"

  val p1 = page("test page")

  val targetBlock = insertBlock(TestAlign(), p1)

  val totalBlock = block(1, 1, "Total", "Total block") {

    val totalQuantity = visit(Domain<Int>(3), position = at(1, 1)) {
      label = "Total"
      help = "Total"
    }
    val totalPrice = visit(Domain<Int>(7), position = at(1, 2)) {}

    align(targetBlock, totalPrice to targetBlock.price , totalQuantity to targetBlock.quantity)
  }
}

class TestAlign : FormBlock(10, 5, "Test block") {

  val designation = visit(domain = Domain<String>(20), position = at(1, 1)) {
    label = "Designation"
    help = "The designation of product"
  }
  val reference = visit(domain = Domain<String>(20), position = at(2, 1)) {
    label = "Reference"
    help = "The reference of product"
  }
  val quantity = visit(domain = Domain<Int>(3), position = at(3, 1)) {
    label = "quantity"
    help = "The quantity"
  }
  val price = visit(domain = Domain<String>(20), position = at(4, 1)) {
    label = "Price"
    help = "The price"
  }
}

fun main() {
  Application.runForm(formName = FormAlignTest)
}
