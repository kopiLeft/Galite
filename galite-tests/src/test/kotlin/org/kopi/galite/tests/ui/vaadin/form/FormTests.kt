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
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.ui.vaadin.common.VCaption
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.main.VWindowContainer
import org.kopi.galite.ui.vaadin.menu.ModuleList

import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.menubar.MenuBar

class FormTests: GaliteVUITestBase() {

  val modulesMenu get() = _get<ModuleList> { id = "module_list" }._get<MenuBar>()
  val mainWindow get() = _get<MainWindow>()
  val windowCaption get() =
    mainWindow
      ._get<VWindowContainer>()
      ._get<VCaption>()
  val clientFormModel = ClientForm()
  val commandFormModel = CommandForm()
  val productsFormModel = ProductForm()
  val billsFormModel = BillForm()
  val billsProductsFormModel = BillProductForm()
  val stocksFormModel = StockForm()
  val taxRulesFormModel = TaxRuleForm()
  val providersFormModel = ProviderForm()

  @Test
  fun `test forms are working`() {
    // Login
    login()

    mainWindow._expectOne<VWindowContainer>()

    // Assert title is displayed in window caption
    modulesMenu._clickItemWithCaptionAndWait("Client form")
    assertEquals(clientFormModel.title, windowCaption.getCaption())

    modulesMenu._clickItemWithCaptionAndWait("Commands form")
    assertEquals(commandFormModel.title, windowCaption.getCaption())

    modulesMenu._clickItemWithCaptionAndWait("Products form")
    assertEquals(productsFormModel.title, windowCaption.getCaption())

    modulesMenu._clickItemWithCaptionAndWait("Bills form")
    assertEquals(billsFormModel.title, windowCaption.getCaption())

    modulesMenu._clickItemWithCaptionAndWait("Bills products")
    assertEquals(billsProductsFormModel.title, windowCaption.getCaption())

    modulesMenu._clickItemWithCaptionAndWait("Stocks")
    assertEquals(stocksFormModel.title, windowCaption.getCaption())

    modulesMenu._clickItemWithCaptionAndWait("Tax rules")
    assertEquals(taxRulesFormModel.title, windowCaption.getCaption())

    modulesMenu._clickItemWithCaptionAndWait("Providers form")
    assertEquals(providersFormModel.title, windowCaption.getCaption())
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
