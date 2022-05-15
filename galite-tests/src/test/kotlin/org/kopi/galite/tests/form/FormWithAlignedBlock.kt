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

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.Block

class FormWithAlignedBlock : Form(title = "form for test", locale = Locale.UK) {

  val targetBlock = insertBlock(TestAlign())

  val totalBlock = block("Total block", 1, 1) {

    val totalQuantity = visit(INT(3), position = at(1, 1)) {
      label = "Total"
      help = "Total"
    }
    val totalPrice = visit(INT(7), position = at(1, 2)) {}

    align(targetBlock, totalQuantity to targetBlock.quantity, totalPrice to targetBlock.price)
  }
}

class TestAlign : Block("Test block", 10, 8) {

  init {
    options(BlockOption.NODETAIL)
  }

  val description = visit(domain = STRING(20), position = at(1, 1)) {
    label = "Description"
    help = "The description of product"
  }
  val reference = visit(domain = STRING(20), position = at(2, 1)) {
    label = "Reference"
    help = "The reference of product"
  }
  val quantity = visit(domain = INT(3), position = at(3, 1)) {
    label = "quantity"
    help = "The quantity"
  }
  val price = visit(domain = STRING(20), position = at(4, 1)) {
    label = "Price"
    help = "The price"
  }
}

fun main() {
  runForm(formName = FormWithAlignedBlock())
}
