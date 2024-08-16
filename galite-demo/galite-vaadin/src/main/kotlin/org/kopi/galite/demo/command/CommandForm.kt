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
package org.kopi.galite.demo.command

import java.util.Locale

import org.kopi.galite.demo.database.Client
import org.kopi.galite.demo.database.Command
import org.kopi.galite.demo.database.Purchase
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.domain.COLOR
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Key

class CommandForm : DictionaryForm(title = "Commands", locale = Locale.UK) {
  val page = page("Command")

  init {
    insertMenus()
    insertCommands()
  }

  val list = actor(menu = actionMenu, label = "List", help = "Display List", ident = "list") {
    key = Key.F1
    icon = Icon.LIST
  }

  val tb1 = page.insertBlock(BlockCommand()) {
    command(item = report) {
      createReport {
        CommandR()
      }
    }

    command(item = list) {
      recursiveQuery()
    }

    command(item = _break) {
      resetBlock()
    }

    command(item = serialQuery) {
      serialQuery()
    }

    command(item = dynamicReport) {
      createDynamicReport()
    }
    command(item = list) { recursiveQuery() }
    command(item = save, Mode.INSERT, Mode.UPDATE) { save(block) }
  }

  val purchases = page.insertBlock(BlockPurchase()) {
    command(item = deleteLine) { deleteLine(block) }
  }

  inner class BlockCommand : Block("Commands", 1, 10) {
    val u = table(Command)
    val v = table(Client)

    val numCmd = hidden(domain = INT(20)) {
      label = "Number"
      help = "The command number"
      columns(u.numCmd)
    }

    val idClt = mustFill(domain = INT(25), position = at(1, 1)) {
      label = "Client ID"
      help = "The client ID"
      columns(u.idClt, v.idClt) {
        priority = 1
      }
    }
    val paymentMethod = mustFill(domain = Payment, position = at(3, 1)) {
      label = "Payment method"
      help = "The payment method"
      columns(u.paymentMethod) {
        priority = 1
      }
    }
    val statusCmd = mustFill(domain = CommandStatus, position = at(4, 1)) {
      label = "Command status"
      help = "The command status"
      columns(u.statusCmd) {
        priority = 1
      }
    }

    init {
      blockVisibility(Access.VISIT, Mode.QUERY)
    }

    val PostqryTrigger = trigger(POSTQRY) {
      purchases.idClt[0] = idClt.value
      purchases.load()
      for (rec in 0 until purchases.block.bufferSize) {
        if (purchases.block.isRecordFilled(rec)) {
          purchases.colorRecord(rec)
        }
      }
    }

    /**
     * Save block
     */
    fun save(b: VBlock) {
      tb1.block.validate()

      if (!purchases.isFilled()) {
        purchases.currentRecord = 0
        throw VExecFailedException("Purchase block is empty.")
      }

      transaction {
        tb1.block.save()
        purchases.block.save()
      }

      b.form.reset()
    }
  }

  inner class BlockPurchase : Block("Purchase", 10, 10) {
    val u = table(Purchase)

    val numPurchase = hidden(domain = INT(20)) {
      label = "Number"
      help = "The purchase number"
      columns(u.id)
    }
    val idClt = mustFill(domain = INT(25), position = at(1, 1)) {
      label = "Client ID"
      help = "The client ID"
      columns(u.idClt)
    }
    val idProduct = mustFill(domain = INT(20), position = at(2, 1)) {
      label = "Product ID"
      help = "The Product ID"
      columns(u.idPdt)
    }
    val quantity = mustFill(domain = INT(7), position = at(4, 1)) {
      label = "Quantity"
      help = "quantity of the current purchase"
      columns(u.quantity)
    }

    val color = mustFill(domain = COLOR, position = at(4, 1)) {
      label = "color"
      help = "color [for test purpose] "
      columns(u.color)
    }

    init {
      Border.LINE
      blockVisibility(Access.VISIT, Mode.QUERY)
      options(BlockOption.NODETAIL)
    }

    /**
     * Delete line from no detail block.
     */
    fun deleteLine(b: VBlock) {
      val rec: Int = b.activeRecord

      if ( rec == -1) return
      if (b.isRecordFilled(rec)) {
        b.setRecordDeleted(rec, true)
        transaction { b.refreshLookup(rec) }
      }
      b.form.gotoBlock(b)
      b.gotoRecord(rec + 1)
    }

    /**
     * Color record using color field.
     */
    fun colorRecord(rec: Int) {
      if (block.isRecordFilled(rec)) {
        var backgroundColor = VColor.WHITE

        color.vField.getColor(rec)?.let { backgroundColor = VColor(it.red, it.green, it.blue) }
        for (field in block.fields) {
          if (field.getType() != VField.MDL_FLD_COLOR) {
            field.setColor(rec, VColor.BLACK, backgroundColor)
            field.fireColorChanged(rec)
          }
        }
      }
    }
  }

  object Payment : CodeDomain<String>() {
    init {
      "cash" keyOf "cash"
      "check" keyOf "check"
      "bank card" keyOf "bank card"
    }
  }

  object CommandStatus : CodeDomain<String>() {
    init {
      "in preparation" keyOf "in preparation"
      "available" keyOf "available"
      "delivered" keyOf "delivered"
      "canceled" keyOf "canceled"
    }
  }
}

fun main() {
  runForm(form = CommandForm::class)
}