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
package org.kopi.galite.demo.client

import java.util.Locale

import org.kopi.galite.demo.Client
import org.kopi.galite.demo.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import org.kopi.galite.tests.chart.ChartSample
import org.kopi.galite.tests.form.FormWithChart
import org.kopi.galite.type.Decimal

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
    icon = "preview"  // icon is optional here
  }

  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F1   // key is optional here
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
    icon = "preview"  // icon is optional here
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
          menu =   FormWithChart.action,
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

  val block = insertBlock(Clients(), clientsPage) {
    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }
    command(item = dynamicReport) {
      action = {
        createDynamicReport()
      }
    }
    command(item = graph) {
      mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
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

  val salesBlock = insertBlock(sales(), clientsPage) {

    command(item = showHideFilter) {
      action = {
        showHideFilter()
      }
    }

    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }
    command(item = dynamicReport) {
      action = {
        createDynamicReport()
      }
    }
    command(item = graph) {
      mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
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

  override fun createReport(): Report {
    return ClientR()
  }
}

class Clients : FormBlock(1, 1, "Clients") {
  val u = table(Client)

  val idClt = visit(domain = Domain<Int>(30), position = at(1, 1..2)) {
    label = "ID"
    help = "The client id"
    columns(u.idClt)
  }
  val fstnameClt = visit(domain = Domain<String>(25), position = at(2, 1)) {
    label = "First Name"
    help = "The client first name"
    columns(u.firstNameClt)
  }
  val nameClt = visit(domain = Domain<String>(25), position = at(2, 2)) {
    label = "Last name"
    help = "The client last name"
    columns(u.lastNameClt)
  }
  val ageClt = visit(domain = Domain<Int>(3), position = at(2, 3)) {
    label = "Age"
    help = "The client age"
    columns(u.ageClt)
  }
  val email = visit(domain = Domain<String>(25), position = at(3, 1)) {
    label = "Email"
    help = "The mail adress"
    columns(u.mail)
  }
  val addressClt = visit(domain = Domain<String>(20), position = at(3, 2)) {
    label = "Address"
    help = "The client address"
    columns(u.addressClt)
  }
  val countryClt = visit(domain = Domain<String>(12), position = at(4, 1)) {
    label = "Country"
    help = "The client country"
    columns(u.countryClt)
  }
  val cityClt = visit(domain = Domain<String>(12), position = at(4, 2)) {
    label = "City"
    help = "The client city"
    columns(u.cityClt)
  }
  val zipCodeClt = visit(domain = Domain<Int>(12), position = follow(cityClt)) {
    label = "Zip code"
    help = "The client zip code"
    columns(u.zipCodeClt)
  }
  val active = visit(domain = Domain<Boolean>(), position = at(5, 1)) {
    label = "Active ?"
    help = "Is the user active?"
  }

  init {
    nameClt[0] = "test"
  }
}

class sales : FormBlock(10, 10, "Sales") {

  val idClt = visit(domain = Domain<String>(5), position = at(1, 1..2)) {
    label = "ID"
    help = "The item id"
  }
  val description = visit(domain = Domain<String>(25), position = at(2, 1)) {
    label = "Description"
    help = "The item description"
  }
  val quantity = visit(domain = Domain<Int>(7), position = at(2, 2)) {
    label = "Quantity"
    help = "The number of items"
  }
  val price = visit(domain = Domain<Decimal>(10, 5), position = at(2, 2)) {
    label = "Price"
    help = "The item price"
  }

  init {
    border = VConstants.BRD_LINE
  }
}

fun main() {
  Application.runForm(formName = ClientForm())
}
