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
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.timestamp

import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Client
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Purchase
import org.kopi.galite.demo.database.Task
import org.kopi.galite.demo.database.Task.autoIncrement
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.domain.TEXT
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.FullCalendarBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.visual.VExecFailedException

class ClientForm : ReportSelectionForm() {
  override val locale = Locale.UK
  override val title = "Tasks"
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

  val clientsPage= page("Developers")
  val contactsPage= page("Contacts")
  val detailsPage= page("Details")
  val clientsBlock = clientsPage.insertBlock(Tasks())
  val salesBlock = clientsPage.insertBlock(Sales())




  inner class Tasks : FullCalendarBlock("Tasks") {
    val t = table(Task)

    val id = hidden(INT(20)) { columns(t.id) }
    val uc = hidden(domain = INT(20)) { columns(t.uc) }
    val ts = hidden(domain = INT(20)) { columns(t.ts) }

    val date = date(position = at(1, 1)) {
      label = "Date"
      columns(t.date)
    }
    val from = fromTime(position = at(2, 1)) {
      label = "From"
      columns(t.from)
    }
    val to = toTime(position = at(3, 1)) {
      label = "To"
      columns(t.to)
    }
    val text = mustFill(domain = STRING(50), position = at(4, 1)) {
      label = "Text"
      columns(t.text) {
        priority = 1
      }
    }
  }

  inner class Clients : FullCalendarBlock("Tasks") {
    val t = table(org.kopi.galite.demo.database.Task)

    val id = hidden(INT(20)) { columns(t.id) }
    val uc = hidden(domain = INT(20)) { columns(t.uc) }
    val ts = hidden(domain = INT(20)) { columns(t.ts) }

    val from = from(position = at(1, 1)) {
      label = "From"
      columns(t.from)
    }
    val to = to(position = at(2, 1)) {
      label = "To"
      columns(t.to)
    }
    val description1 = mustFill(domain = STRING(20), position = at(3, 1)) {
      label = "description1"
      columns(t.description1) {
        priority = 1
      }
    }
    val description2 = mustFill(domain = TEXT(20, 5), position = at(4, 1)) {
      label = "description2"
      columns(t.description2)
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
