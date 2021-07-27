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

import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.demo.bill.BillForm
import org.kopi.galite.demo.billproduct.BillProductForm
import org.kopi.galite.demo.client.ClientForm
import org.kopi.galite.demo.command.CommandForm
import org.kopi.galite.demo.product.ProductForm
import org.kopi.galite.demo.provider.ProviderForm
import org.kopi.galite.demo.stock.StockForm
import org.kopi.galite.demo.taxRule.TaxRuleForm
import org.kopi.galite.testing.open
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.ui.vaadin.common.VCaption
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.main.VWindowContainer

import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get

class FormTests: GaliteVUITestBase() {

  val mainWindow get() = _get<MainWindow>()
  val windowCaption get() =
    mainWindow
      ._get<VWindowContainer>()
      ._get<VCaption>()
  val clientForm = ClientForm()
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

  companion object {
    /**
     * Defined your test specific modules
     */
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      // Using modules defined in demo application
      org.kopi.galite.demo.initModules()
    }
  }
}
