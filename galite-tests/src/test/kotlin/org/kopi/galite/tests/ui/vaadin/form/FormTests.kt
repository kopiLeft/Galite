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
package org.kopi.galite.tests.ui.vaadin.form

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.demo.addClients
import org.kopi.galite.demo.addProducts
import org.kopi.galite.demo.addSales
import org.kopi.galite.demo.addTaxRules
import org.kopi.galite.demo.bill.BillForm
import org.kopi.galite.demo.billproduct.BillProductForm
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.command.CommandForm
import org.kopi.galite.demo.createGShopApplicationTables
import org.kopi.galite.demo.product.ProductForm
import org.kopi.galite.demo.provider.ProviderForm
import org.kopi.galite.demo.stock.StockForm
import org.kopi.galite.demo.taxRule.TaxRuleForm
import org.kopi.galite.testing.findBlock
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.ui.vaadin.common.VCaption
import org.kopi.galite.ui.vaadin.form.DGridBlock
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.main.VWindowContainer

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
    val block = clientForm.salesBlock.findBlock() as DGridBlock
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
        createGShopApplicationTables()
        addClients()
        addTaxRules()
        addProducts()
        addSales()
      }
      // Using modules defined in demo application
      org.kopi.galite.demo.initModules()
    }
  }
}
