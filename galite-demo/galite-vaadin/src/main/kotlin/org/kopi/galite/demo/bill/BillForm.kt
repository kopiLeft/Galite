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
package org.kopi.galite.demo.bill

import java.awt.Color
import java.util.Locale

import org.kopi.galite.demo.database.Bill
import org.kopi.galite.demo.database.Command
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.domain.COLOR
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.VBlock

class BillForm : Form(title = "Bills", locale = Locale.UK) {

  init {
    insertMenus()
    insertCommands()
  }

  val tb1 = insertBlock(BlockBill()) {
    options(BlockOption.NODETAIL)

    trigger(POSTQRY) {
      val color: Color = testColor.value as Color? ?: Color.BLACK
      val vColor = VColor(color.red, color.green, color.blue)

      addressBill.vField.setColor(vColor, VColor.WHITE)
    }
  }

  inner class BlockBill : Block("Bills", 100, 10) {

    init {
      command(item = deleteLine) { effacerLigne(block) }
      command(item = menuQuery, Mode.QUERY) {
        transaction {
          tb1.load()
        }
        setMode(Mode.UPDATE)
      }
      command(item = save, Mode.INSERT, Mode.UPDATE) {
        save(block)
      }
    }

    val u = table(Bill)
    val v = table(Command)

    val testColor = visit(domain = COLOR, position = at(1,2)) {
      label =" Color"
      help = "This is a test color field"
      columns(u.color)
    }
    val numBill = hidden(domain = INT(20)) {
      label = "Number"
      help = "The bill number"
      columns(u.id)
    }
    val addressBill = visit(domain = STRING(30), position = at(1, 1)) {
      label = "Address"
      help = "The bill address"
      columns(u.addressBill)
    }
    val dateBill = visit(domain = DATE, position = at(2, 1)) {
      label = "Date"
      help = "The bill date"
      columns(u.dateBill)
    }
    val amountWithTaxes = visit(domain = DECIMAL(20, 10), position = at(3, 1)) {
      label = "Amount to pay"
      help = "The bill amount to pay"
      columns(u.amountWithTaxes)
    }
    val refCmd = visit(domain = INT(20), position = at(4, 1)) {
      label = "Command reference"
      help = "The command reference"
      columns(u.refCmd, v.numCmd)
    }
  }

  private fun effacerLigne(b: VBlock) {
    val rec: Int = b.activeRecord

    if ( rec == -1) return
    if (b.isRecordFilled(rec)) {
      b.setRecordDeleted(rec, true)
      transaction { b.refreshLookup(rec) }
    }
    b.form.gotoBlock(b)
    b.gotoRecord(rec + 1)
  }

  fun save(b: VBlock) {
    b.validate()
    transaction {
      tb1.save()
    }
  }
}

fun main() {
  runForm(form = BillForm::class)
}
