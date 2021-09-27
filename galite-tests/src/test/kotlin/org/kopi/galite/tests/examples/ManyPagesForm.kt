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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.kopi.galite.demo.Application
import org.kopi.galite.visual.domain.Fixed
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.form.dsl.DictionaryForm
import org.kopi.galite.visual.form.dsl.FormBlock
import org.kopi.galite.visual.form.dsl.insertBlock

class ManyPagesForm : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Clients"
  val clientsPage= page("Clients")
  val contactsPage= page("Contacts")
  val detailsPage= page("Details")
  val clientsBlock = clientsPage.insertBlock(Clients())
  val contactsBlock = contactsPage.insertBlock(Contacts())
  val detailsBlock = detailsPage.insertBlock(Details())

  inner class Clients : FormBlock(1, 100, "Clients") {
    val idClt = visit(domain = INT(30), position = at(1, 1)) {
      label = "ID"
      help = "The client id"
    }
  }

  inner class Contacts : FormBlock(1, 100, "Contacts") {
    val contact = visit(domain = STRING(30 ), position = at(1, 1)) {
      label = "contact"
      help = "The contact"
    }
  }

  inner class Details : FormBlock(1, 100, "Details") {
    val detail = visit(domain = STRING(30, 30, 30, Fixed.ON), position = at(1, 1)) {
      label = "detail"
      help = "The detail"
    }
  }
}

fun main() {
  Application.runForm(formName = ManyPagesForm())
}
