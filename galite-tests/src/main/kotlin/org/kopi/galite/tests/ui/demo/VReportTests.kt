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

package org.kopi.galite.tests.ui.demo

import java.util.Date
import java.util.Locale

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

import org.kopi.galite.base.UComponent
import org.kopi.galite.db.DBContext
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.print.PrintManager
import org.kopi.galite.report.Report
import org.kopi.galite.ui.report.DReport
import org.kopi.galite.ui.visual.VApplication
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.PrinterManager
import org.kopi.galite.visual.Registry
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.VMenuTree

@Route("VReport")
class VReportTests : VerticalLayout() {

  init {
    setupApplication()
    val model = SimpleReport()
    val dReport = UIFactory.uiFactory.createView(model) as DReport
    dReport.run()
    add(dReport)
  }

  private fun setupApplication() {
    val application = object : VApplication() {
      override fun login(database: String, driver: String, username: String, password: String, schema: String): DBContext {
        TODO("Not yet implemented")
      }

      override fun logout() {
        TODO("Not yet implemented")
      }

      override fun startApplication() {
        TODO("Not yet implemented")
      }

      override fun allowQuit(): Boolean {
        TODO("Not yet implemented")
      }

      override fun isNoBugReport(): Boolean {
        TODO("Not yet implemented")
      }

      override fun getStartupTime(): Date {
        TODO("Not yet implemented")
      }

      override fun getMenu(): VMenuTree {
        TODO("Not yet implemented")
      }

      override fun setGeneratingHelp() {
        TODO("Not yet implemented")
      }

      override fun isGeneratingHelp(): Boolean {
        TODO("Not yet implemented")
      }

      override fun getDBContext(): DBContext {
        TODO("Not yet implemented")
      }

      override fun getUserName(): String {
        TODO("Not yet implemented")
      }

      override fun getUserIP(): String {
        TODO("Not yet implemented")
      }

      override fun getRegistry(): Registry {
        TODO("Not yet implemented")
      }

      override fun getLocalizationManager(): LocalizationManager {
        TODO("Not yet implemented")
      }

      override fun displayError(parent: UComponent, message: String) {
        TODO("Not yet implemented")
      }

      override fun getPrintManager(): PrintManager {
        TODO("Not yet implemented")
      }

      override fun setPrintManager(printManager: PrintManager) {
        TODO("Not yet implemented")
      }

      override fun getPrinterManager(): PrinterManager {
        TODO("Not yet implemented")
      }

      override fun setPrinterManager(printerManager: PrinterManager) {
        TODO("Not yet implemented")
      }

      override fun getApplicationConfiguration(): ApplicationConfiguration {
        TODO("Not yet implemented")
      }

      override fun setApplicationConfiguration(configuration: ApplicationConfiguration) {
        TODO("Not yet implemented")
      }

      override val supportedLocales: Array<Locale>?
        get() = null
      override val logoHref: String
        get() = ""
      override val alternateLocale: Locale
        get() = Locale.FRANCE

      override fun notice(message: String) {
        TODO("Not yet implemented")
      }

      override fun error(message: String) {
        TODO("Not yet implemented")
      }

      override fun warn(message: String) {
        TODO("Not yet implemented")
      }

      override fun ask(message: String, yesIsDefault: Boolean): Int {
        TODO("Not yet implemented")
      }
    }
  }

  /**
   * Simple report with two fields
   */
  class SimpleReport : Report() {
    val field1 = field<String> {
      label = "Pays"
    }
    val field2 = field<Int> {
      label = "CodeDouanier"
    }

    init {
      add {
        this[field1] = "Tunis"
        this[field2] = 123456789
      }
      add {
        this[field1] = "France"
        this[field2] = 987654321
      }
    }
  }
}
