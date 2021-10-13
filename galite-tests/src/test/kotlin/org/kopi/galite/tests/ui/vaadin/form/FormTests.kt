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
package org.kopi.galite.tests.ui.vaadin.form

import java.util.Locale

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.demo.database.addClients
import org.kopi.galite.demo.database.addProducts
import org.kopi.galite.demo.database.addSales
import org.kopi.galite.demo.database.addTaxRules
import org.kopi.galite.demo.database.createApplicationTables
import org.kopi.galite.testing.findMultipleBlock
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.common.VCaption
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.main.VWindowContainer
import org.kopi.galite.demo.database.Client
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Purchase
import org.kopi.galite.tests.examples.initDatabase
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.form.VConstants

import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.expectRow

class FormTests: GaliteVUITestBase() {

  val mainWindow get() = _get<MainWindow>()
  val windowCaption get() =
    mainWindow
      ._get<VWindowContainer>()
      ._get<VCaption>()
  val clientForm = ClientForm().also { it.model }
  val commandForm = CommandForm()
  val productsForm = ProductForm()
  val billsForm = BillForm()
  val billsProductsForm = BillProductForm()
  val stocksForm = StockForm()
  val taxRulesForm = TaxRuleForm()
  val providersForm = ProviderForm()

  @Test
  fun `test forms are working`() {
    // Login
    login()

    mainWindow._expectOne<VWindowContainer>()

    // Assert title is displayed in window caption
    clientForm.open()
    assertEquals(clientForm.title, windowCaption.getCaption())

    commandForm.open()
    assertEquals(commandForm.title, windowCaption.getCaption())

    productsForm.open()
    assertEquals(productsForm.title, windowCaption.getCaption())

    billsForm.open()
    assertEquals(billsForm.title, windowCaption.getCaption())

    billsProductsForm.open()
    assertEquals(billsProductsForm.title, windowCaption.getCaption())

    stocksForm.open()
    assertEquals(stocksForm.title, windowCaption.getCaption())

    taxRulesForm.open()
    assertEquals(taxRulesForm.title, windowCaption.getCaption())

    providersForm.open()
    assertEquals(providersForm.title, windowCaption.getCaption())
  }

  @Test
  fun `load multiple block then check data in the grid`() {
    // Login
    login()
    // Open for block
    clientForm.open()
    clientForm.list.triggerCommand()
    val block = clientForm.salesBlock.findMultipleBlock()
    val data = arrayOf(
      arrayOf("1", "description Product 0", "1", "263,00000"),
      arrayOf("2", "description Product 1", "1", "314,00000"),
      arrayOf("3", "description Product 2", "2", "180,00000"),
      arrayOf("4", "description Product 3", "3", "65,00000")
    )

    data.forEachIndexed { index, row ->
      block.grid.expectRow(index, *row)
    }
  }

  companion object {
    /**
     * Defined your test specific modules
     */
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        createApplicationTables()
        addClients()
        addTaxRules()
        addProducts()
        addSales()
        initDatabase()
      }
    }
  }
}

class ClientForm : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Clients"
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )
  val list = actor(
    ident = "list",
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F10
    icon = "list"  // icon is optional here
  }

  val clientsBlock = insertBlock(Clients())
  val salesBlock = insertBlock(Sales())

  inner class Clients : FormBlock(1, 100, "Clients") {
    val c = table(Client)

    val idClt = visit(domain = INT(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
      columns(c.idClt)
      value = 1
    }

    val PostqryTrigger = trigger(POSTQRY) {
      salesBlock.idClt[0] = idClt.value
      salesBlock.load()
    }

    init {
      command(item = list) {
        action = {
          recursiveQuery()
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
      border = VConstants.BRD_LINE

      command(item = list) {
        action = {
          recursiveQuery()
        }
      }
    }
  }
}


class CommandForm : Form() {
  override val locale = Locale.UK
  override val title = "Commands"

  val block = insertBlock(BlockCommand()) {}

  inner class BlockCommand : FormBlock(1, 10, "Commands") {
    val numCmd = visit(domain = INT(20), position = at(1, 1)) {
      label = "Number"
      help = "The command number"
    }
  }
}

class ProductForm : Form() {
  override val locale = Locale.UK
  override val title = "Products"

  val block = insertBlock(BlockProduct())

  inner class BlockProduct : FormBlock(1, 1, "Products") {
    val idPdt = visit(domain = INT(20), position = at(1, 1)) {
      label = "ID"
      help = "The product ID"
    }
  }
}

class BillForm : Form() {
  override val locale = Locale.UK
  override val title = "Bills"

  val block = insertBlock(BlockBill()) {}

  inner class BlockBill : FormBlock(1, 1, "Bills") {
    val numBill = visit(domain = INT(20), position = at(1, 1)) {
      label = "Number"
      help = "The bill number"
    }
  }
}

class BillProductForm : Form() {
  override val locale = Locale.UK
  override val title = "Bill products"

  val block = insertBlock(BlockBillProduct()) {}

  inner class BlockBillProduct : FormBlock(1, 1, "bill product") {
    val idBPdt = visit(domain = INT(20), position = at(1, 1)) {
      label = "Product ID"
      help = "The bill product ID"
    }
  }
}

class StockForm : Form() {
  override val locale = Locale.UK
  override val title = "Stocks"

  val block = insertBlock(StockBlock()) {}

  inner class StockBlock : FormBlock(1, 1, "Stock") {
    val idStckPdt = visit(domain = INT(20), position = at(1,1)) {
      label = "Product_ID"
      help = "The product ID"
    }
  }
}

class TaxRuleForm : Form() {
  override val locale = Locale.UK
  override val title = "TaxRules"

  val block = insertBlock(TaxRuleBlock()) {}

  inner class TaxRuleBlock : FormBlock(1, 10, "TaxRule") {
    val idTaxe = visit(domain = INT(20), position = at(1, 1)) {
      label = "ID"
      help = "The tax ID"
    }
  }
}

class ProviderForm : Form() {
  override val locale = Locale.UK
  override val title = "Providers"

  val block = insertBlock(BlockProvider()) {}

  inner class BlockProvider : FormBlock(1, 1, "Providers") {
    val idProvider = visit(domain = INT(20), position = at(1, 1)) {
      label = "ID"
      help = "The provider ID"
    }
  }
}
