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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.type.Date
import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

class FormExample : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Clients"
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )
  val clientsPage= page("Clients")
  val block = insertBlock(Clients(), clientsPage)
  val salesBlock = insertBlock(sales(), clientsPage)

  inner class Clients : FormBlock(1, 1, "Clients") {
    val idClt = visit(domain = Domain<Int>(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
    }
  }

  inner class sales : FormBlock(10, 10, "Sales") {

    val idClt = visit(domain = Domain<Int>(5), position = at(1, 1..2)) {
      label = "ID"
      help = "The item id"
    }
    val description = visit(domain = Domain<String>(25), position = at(2, 1)) {
      label = "Description"
      help = "The item description"
    }
    val price = visit(domain = Domain<Decimal>(10, 5), position = at(3, 2)) {
      label = "Price"
      help = "The item price"
    }
    val active = visit(domain = Domain<Boolean>(), position = at(4, 1)) {
      label = "Status"
      help = "Is the user account active?"
    }
    val date = visit(domain = Domain<Date>(), position = at(5, 1)) {
      label = "Date"
      help = "The date"
    }
    val month = visit(domain = Domain<Month>(), position = at(6, 1)) {
      label = "Month"
      help = "The month"
    }
    val timestamp = visit(domain = Domain<Timestamp>(), position = at(7, 1)) {
      label = "Timestamp"
      help = "The Timestamp"
    }
    val time = visit(domain = Domain<Time>(), position = at(8, 1)) {
      label = "Time"
      help = "The time"
    }
    val week = visit(domain = Domain<Week>(), position = at(9, 1)) {
      label = "Week"
      help = "The week"
    }

    init {
      border = VConstants.BRD_LINE
    }
  }
}
