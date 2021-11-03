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
import org.kopi.galite.demo.database.Client
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Purchase
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.visual.VExecFailedException

class ClientForm : ReportSelectionForm() {
  override val locale = Locale.UK
  override val title = "Clients"
  val action = menu("Action")
  val quit = actor(
    ident = "quit",
    menu = action,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE          // key is optional here
    icon = "quit"  // icon is optional here
  }
  val showHideFilter = actor(
          ident = "ShowHideFilter",
          menu = action,
          label = "ShowHideFilter",
          help = " Show Hide Filter",
  ) {
    key = Key.F4
    icon = "searchop"
  }
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "report"  // icon is optional here
  }

  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F10
    icon = "list"  // icon is optional here
  }

  val saveBlock = actor(
          ident = "saveBlock",
          menu = action,
          label = "Save Block",
          help = " Save Block",
  ) {
    key = Key.F9
    icon = "save"
  }

  val interSave = actor(
          ident = "interSave",
          menu = action,
          label = "Save and load",
          help = " Save and load",
  ) {
    key = Key.F11
    icon = "save"
  }

  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )

  val dynamicReport = actor(
          ident = "dynamicReport",
          menu = action,
          label = "DynamicReport",
          help = " Create Dynamic Report",
  ) {
    key = Key.F6      // key is optional here
    icon = "report"  // icon is optional here
  }
  val helpForm = actor(
          ident = "helpForm",
          menu = action,
          label = "Help",
          help = " Help"
  ) {
    key = Key.F1
    icon = "help"
  }
  val graph = actor (
          ident =  "graph",
          menu =   action,
          label =  "Graph",
          help =   "show graph values",
  ) {
    key  =  Key.F9          // key is optional here
    icon =  "column_chart"  // icon is optional here
  }

  val helpCmd = command(item = helpForm) {
    action = {
      showHelp()
    }
  }
  val quitCmd = command(item = quit) {
    action = {
      quitForm()
    }
  }

  val clientsPage= page("Clients")
  val contactsPage= page("Contacts")
  val detailsPage= page("Details")
  val clientsBlock = clientsPage.insertBlock(Clients())
  val salesBlock = clientsPage.insertBlock(Sales())

  inner class Clients : FormBlock(1, 100, "Clients") {
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
      columns(c.firstNameClt)
    }
    val nameClt = visit(domain = STRING(25), position = at(2, 2)) {
      label = "Last name"
      help = "The client last name"
      columns(c.lastNameClt)
    }
    val ageClt = visit(domain = INT(3), position = at(2, 3)) {
      label = "Age"
      help = "The client age"
      columns(c.ageClt)
    }
    val email = visit(domain = STRING(25), position = at(3, 1)) {
      label = "Email"
      help = "The mail adress"
      columns(c.mail)
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
        action = {
          createReport(this@Clients)
        }
      }
      command(item = dynamicReport) {
        action = {
          createDynamicReport()
        }
      }
      command(item = graph) {
        mode(Mode.UPDATE, Mode.INSERT, Mode.QUERY)
        action = {
          showChart(ChartSample())
        }
      }
      command(item = list) {
        action = {
          recursiveQuery()
        }
      }
      command(item = saveBlock) {
        action = {
          saveBlock()
        }
      }
    }
  }

  inner class Sales : FormBlock(10, 10, "Sales") {
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
    }
    val description = visit(domain = STRING(25), position = at(2, 1)) {
      label = "Description"
      help = "The item description"
      columns(P.description)
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

    init {
      border = Border.LINE

      command(item = showHideFilter) {
        action = {
          showHideFilter()
        }
      }

      command(item = report) {
        action = {
          createReport(this@Sales)
        }
      }
      command(item = dynamicReport) {
        action = {
          createDynamicReport()
        }
      }
      command(item = graph) {
        mode(Mode.UPDATE, Mode.INSERT, Mode.QUERY)
        action = {
          showChart(ChartSample())
        }
      }
      command(item = list) {
        action = {
          recursiveQuery()
        }
      }
      command(item = saveBlock) {
        action = {
          saveBlock()
        }
      }
      command(item = interSave) {
        action = {
          val b = salesBlock.vBlock
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

  override fun createReport(): Report {
    return ClientR()
  }
}

fun main() {
  runForm(formName = ClientForm())
}
