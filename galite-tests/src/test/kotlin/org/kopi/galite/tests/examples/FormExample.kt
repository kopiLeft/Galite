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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.MONTH
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TIME
import org.kopi.galite.visual.domain.TIMESTAMP
import org.kopi.galite.visual.domain.WEEK
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Block

class FormExample : DictionaryForm(title = "Clients", locale = Locale.UK) {
  override val action = menu("Action")
  val autoFill = actor(
    menu = action,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )
  val clientsPage= page("Clients")
  val block = clientsPage.insertBlock(Clients()) {
    trigger(PREBLK) {
      idClt.value = 50
    }
  }
  val salesBlock = clientsPage.insertBlock(Sales())
  val salesSimpleBlock = clientsPage.insertBlock(SalesSimpleBlock())

  inner class Clients : Block("Clients", 1, 1) {
    val idClt = visit(domain = INT(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
    }
  }

  inner class Sales : Block("Sales", 10, 10) {

    val idClient = visit(domain = INT(5), position = at(1, 1..2)) {
      label = "ID"
      help = "The item id"
    }
    val description = visit(domain = STRING(25), position = at(2, 1)) {
      label = "Description"
      help = "The item description"
    }
    val price = visit(domain = DECIMAL(10, 5), position = at(3, 2)) {
      label = "Price"
      help = "The item price"
    }
    val active = visit(domain = BOOL, position = at(4, 1)) {
      label = "Status"
      help = "Is the user account active?"
    }
    val date = visit(domain = DATE, position = at(5, 1)) {
      label = "Date"
      help = "The date"
    }
    val month = visit(domain = MONTH, position = at(6, 1)) {
      label = "Month"
      help = "The month"
    }
    val timestamp = visit(domain = TIMESTAMP, position = at(7, 1)) {
      label = "Timestamp"
      help = "The Timestamp"
    }
    val time = visit(domain = TIME, position = at(8, 1)) {
      label = "Time"
      help = "The time"
    }
    val week = visit(domain = WEEK, position = at(9, 1)) {
      label = "Week"
      help = "The week"
    }
    val codeDomain = visit(domain = Type, position = at(10, 1)) {
      label = "codeDomain"
      help = "A code-domain field"
    }

    init {
      border = Border.LINE
    }
  }

  inner class SalesSimpleBlock : Block("Sales", 1, 1) {

    val idClt = visit(domain = INT(5), position = at(1, 1..2)) {
      label = "ID"
      help = "The item id"
      trigger(POSTCHG) {
        description.value = "sales description"
      }
    }
    val description = visit(domain = STRING(25), position = at(2, 1)) {
      label = "Description"
      help = "The item description"
    }
    val information = visit(domain = STRING(25), position = at(2, 2)) {
      label = "information"
      help = "The item information"
      trigger(VALUE) {
        "sales information"
      }
    }
    val action = visit(domain =  STRING(25), position = at(3, 1)) {
      label = "action"
      help = "The item action"
      trigger(ACTION) {
         description.value = "item description"
      }
    }
    val price = visit(domain = DECIMAL(10, 5), position = at(3, 2)) {
      label = "Price"
      help = "The item price"
    }
    val active = visit(domain = BOOL, position = at(4, 1)) {
      label = "Status"
      help = "Is the user account active?"
    }
    val date = visit(domain = DATE, position = at(5, 1)) {
      label = "Date"
      help = "The date"
    }
    val month = visit(domain = MONTH, position = at(6, 1)) {
      label = "Month"
      help = "The month"
    }
    val timestamp = visit(domain = TIMESTAMP, position = at(7, 1)) {
      label = "Timestamp"
      help = "The Timestamp"
    }
    val time = visit(domain = TIME, position = at(8, 1)) {
      label = "Time"
      help = "The time"
    }
    val week = visit(domain = WEEK, position = at(9, 1)) {
      label = "Week"
      help = "The week"
    }
    val codeDomain = visit(domain = Type, position = at(10, 1)) {
      label = "codeDomain"
      help = "A code-domain field"
    }

    init {
      border = Border.LINE

      trigger(INIT) {
        description.value = "description value"
      }
    }
  }
}
