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

package org.kopi.galite.demo.client

import java.util.Locale

import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.demo.database.Client
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Purchase
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.form.VBlock

class ClientForm : DictionaryForm(title = "Clients", locale = Locale.UK) {

  init {
    insertMenus()
    insertCommands()

    trigger(INIT) {
      salesBlock.setMode(Mode.INSERT)
    }
  }

  val list = actor(menu = actionMenu, label = "List", help = "Display List", ident = "list") {
    key = Key.F10
    icon = Icon.LIST
  }

  val clientsPage= page("Clients")
  val detailsPage= page("Details")
  val clientsBlock = clientsPage.insertBlock(Clients())
  val salesBlock = clientsPage.insertBlock(Sales())

  inner class Clients : Block("Clients", 1, 100) {
    val c = table(Client, Client.idClt)

    val clientID = visit(domain = ClientID, position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
      columns(c.idClt) {
        priority = 5
        onUpdateSkipped()
        onInsertSkipped()
      }
      value = 1
    }
    val firstName = visit(domain = STRING(25), position = at(2, 1)) {
      label = "First Name"
      help = "The client first name"
      columns(c.firstNameClt) {
        priority = 4
      }
    }
    val name = visit(domain = STRING(25), position = at(2, 2)) {
      label = "Last name"
      help = "The client last name"
      columns(c.lastNameClt) {
        priority = 3
      }
    }
    val age = visit(domain = INT(3), position = at(2, 3)) {
      label = "Age"
      help = "The client age"
      columns(c.ageClt) {
        priority = 2
      }
    }
    val email = visit(domain = STRING(25), position = at(3, 1)) {
      label = "Email"
      help = "The mail adress"
      columns(c.mail) {
        priority = 1
      }
    }
    val address = visit(domain = STRING(20), position = at(3, 2)) {
      label = "Address"
      help = "The client address"
      columns(c.addressClt)
    }
    val country = visit(domain = STRING(12), position = at(4, 1)) {
      label = "Country"
      help = "The client country"
      columns(c.countryClt)
    }
    val city = visit(domain = STRING(12), position = at(4, 2)) {
      label = "City"
      help = "The client city"
      columns(c.cityClt)
    }
    val zipCode = visit(domain = INT(12), position = follow(city)) {
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
      salesBlock.clientID[0] = clientID.value
      salesBlock.load()
    }

    /**
     * Save block
     */
    fun save(b: VBlock) {
      clientsBlock.block.validate()

      if (!salesBlock.isFilled()) {
        salesBlock.currentRecord = 0
        throw VExecFailedException("Sales block is empty.")
      }

      transaction {
        clientsBlock.block.save()
        salesBlock.block.save()
      }

      b.form.reset()
    }

    init {
      command(item = report) { createReport { ClientR() } }
      command(item = dynamicReport) { createDynamicReport() }
      command(item = insertMode, Mode.QUERY, Mode.UPDATE) { insertMode() }
      command(item = list) { recursiveQuery() }
      command(item = save, Mode.INSERT, Mode.UPDATE) { save(block) }
    }
  }

  inner class Sales : Block("Sales", 10, 10) {
    val S = table(Purchase)
    val P = table(Product)

    val clientID = hidden(domain = INT(5)) {
      alias = clientsBlock.clientID
      columns(S.idClt)
    }

    val purchaseID = skipped(domain = INT(5), position = at(1, 1..2)) {
      label = "ID"
      help = "The purchase id"
      columns(S.id)
      options(FieldOption.SORTABLE)
    }
    val productID = mustFill(domain = ProductID, position = at(1, 3)) {
      label = "Product"
      help = "The product id"
      columns(P.idPdt, S.idPdt)
      trigger(POSTCHG) {
        block.fetchLookupFirst(vField)
      }
    }
    val description = visit(domain = STRING(25), position = at(2, 1)) {
      label = "Description"
      help = "The item description"
      columns(P.description)
      options(FieldOption.SORTABLE)
    }
    val quantity = mustFill(domain = INT(7), position = at(2, 2)) {
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

      command(item = showHideFilter) { showHideFilter() }
      command(item = report) { createReport { ClientR() } }
      command(item = dynamicReport) { createDynamicReport() }
      command(item = list) { recursiveQuery() }
    }
  }

  object ClientID : ListDomain<Int>(30) {
    override val table = Client
    init {
      "Id"      keyOf Client.idClt                                      hasWidth 30
      "Name"    keyOf SqlExpressionBuilder.concat(Client.firstNameClt,
                                                  stringLiteral(" "),
                                                  Client.lastNameClt)   hasWidth 76
      "Age"     keyOf Client.ageClt                                     hasWidth 3
    }
  }

  object ProductID : ListDomain<Int>(30) {
    override val table = Product
    init {
      "Id"              keyOf Product.idPdt                                     hasWidth 30
      "Description"     keyOf Product.description                               hasWidth 50
      "Price"           keyOf Product.price                                     hasWidth 12
    }
  }
}

fun main() {
  runForm(form = ClientForm::class)
}
