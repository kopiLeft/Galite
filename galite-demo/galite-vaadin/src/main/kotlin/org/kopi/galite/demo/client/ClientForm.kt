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
package org.kopi.galite.demo.client

import java.util.Locale

import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.common.FormDefaultImpl
import org.kopi.galite.demo.common.IFormDefault
import org.kopi.galite.demo.database.Client
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Purchase
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.visual.VExecFailedException

class ClientForm : ReportSelectionForm(title = "Clients", locale = Locale.UK), IFormDefault by FormDefaultImpl() {

  init {
    insertMenus()
    insertCommands()
  }

  val list = actor(
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F10
    icon = Icon.LIST
  }

  val interSave = actor(
          menu = action,
          label = "Save and load",
          help = " Save and load",
  ) {
    key = Key.F11
    icon = Icon.SAVE
  }

  val dynamicReport = actor(
          menu = action,
          label = "DynamicReport",
          help = " Create Dynamic Report",
  ) {
    key = Key.F6
    icon = Icon.REPORT
  }

  val clientsPage= page("Clients")
  val detailsPage= page("Details")
  val clientsBlock = clientsPage.insertBlock(Clients())
  val salesBlock = clientsPage.insertBlock(Sales())


  inner class Clients : Block("Clients", 1, 100) {
    val c = table(Client)

    val idClt = visit(domain = INT(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
      columns(c.idClt)
      value = 1
    }
    val fstnameClt = visit(domain = STRING(25), position = at(2, 1)) {
      label = "First Name"
      help = "The client first name"
      columns(c.firstNameClt) {
        priority = 1
      }
    }
    val nameClt = visit(domain = STRING(25), position = at(2, 2)) {
      label = "Last name"
      help = "The client last name"
      columns(c.lastNameClt) {
        priority = 2
      }
    }
    val ageClt = visit(domain = INT(3), position = at(2, 3)) {
      label = "Age"
      help = "The client age"
      columns(c.ageClt) {
        priority = 3
      }
    }
    val email = visit(domain = STRING(25), position = at(3, 1)) {
      label = "Email"
      help = "The mail adress"
      columns(c.mail) {
        priority = 4
      }
    }
    val addressClt = visit(domain = STRING(20), position = at(3, 2)) {
      label = "Address"
      help = "The client address"
      columns(c.addressClt)
    }
    val countryClt = visit(domain = STRING(12), position = at(4, 1)) {
      label = "Country"
      help = "The client country"
      columns(c.countryClt)
    }
    val cityClt = visit(domain = STRING(12), position = at(4, 2)) {
      label = "City"
      help = "The client city"
      columns(c.cityClt)
    }
    val zipCodeClt = visit(domain = INT(12), position = follow(cityClt)) {
      label = "Zip code"
      help = "The client zip code"
      columns(c.zipCodeClt)
    }
    val active = visit(domain = BOOL, position = at(5, 1)) {
      label = "Active ?"
      help = "Is the user active?"
      columns(c.activeClt)
    }

    val PostqryTrigger = trigger(POSTQRY) {
      salesBlock.idClt[0] = idClt.value
      salesBlock.load()
    }

    init {
      command(item = report) {
        createReport {
          ClientR()
        }
      }
      command(item = dynamicReport) {
        createDynamicReport()
      }
      command(item = list) {
        recursiveQuery()
      }
    }
  }

  inner class Sales : Block("Sales", 10, 10) {
    val C = table(Client)
    val S = table(Purchase)
    val P = table(Product)

    val idClt = hidden(domain = INT(5)) {
      label = "ID"
      help = "The client id"
      columns(C.idClt, S.idClt)
    }

    val idPdt = hidden(domain = INT(5)) {
      label = "ID"
      help = "The product id"
      columns(P.idPdt, S.idPdt)
    }

    val id = visit(domain = INT(5), position = at(1, 1..2)) {
      label = "ID"
      help = "The item id"
      columns(S.id)
      options(FieldOption.SORTABLE)
    }
    val description = visit(domain = STRING(25), position = at(2, 1)) {
      label = "Description"
      help = "The item description"
      columns(P.description)
      options(FieldOption.SORTABLE)
    }
    val quantity = visit(domain = INT(7), position = at(2, 2)) {
      label = "Quantity"
      help = "The number of items"
      columns(S.quantity)
    }
    val price = visit(domain = DECIMAL(10, 5), position = at(2, 2)) {
      label = "Price"
      help = "The item price"
      columns(P.price)
    }
    val available = visit(domain = BOOL, position = at(2, 2)) {
      label = "available"
    }

    init {
      border = Border.LINE

      showHideFilterCmd

      command(item = report) {
        createReport {
          ClientR()
        }
      }
      command(item = dynamicReport) {
        createDynamicReport()
      }
      command(item = list) {
        recursiveQuery()
      }
      command(item = interSave) {
        val b = salesBlock.block
        val rec: Int = b.activeRecord

        b.validate()

        if (!b.isFilled()) {
          b.currentRecord = 0
          throw VExecFailedException()
        }

        transaction {
          b.save()
        }

        gotoBlock(b)
        b.gotoRecord(if (b.isRecordFilled(rec)) rec + 1 else rec)
      }
    }
  }
}

fun main() {
  runForm(formName = ClientForm())
}
