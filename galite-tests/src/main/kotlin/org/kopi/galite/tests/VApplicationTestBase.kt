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

package org.kopi.galite.tests

import java.util.Locale

import org.kopi.galite.base.UComponent
import org.kopi.galite.chart.VChart
import org.kopi.galite.db.DBContext
import org.kopi.galite.report.VReport
import org.kopi.galite.ui.vaadin.chart.DChart
import org.kopi.galite.ui.vaadin.report.DReport
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.ui.vaadin.visual.VApplicationContext
import org.kopi.galite.ui.vaadin.visual.VFileHandler
import org.kopi.galite.ui.vaadin.visual.VImageHandler
import org.kopi.galite.ui.vaadin.visual.VUIFactory
import org.kopi.galite.ui.vaadin.visual.VWindowController
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.ImageHandler
import org.kopi.galite.visual.Registry
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.WindowController

/**
 * TestBase class for all tests.
 */
open class VApplicationTestBase : TestBase() {

  init {
    GaliteApplication()
    setupApplication()
  }

  fun setupApplication() {
    ApplicationContext.applicationContext = VApplicationContext()
    FileHandler.fileHandler = VFileHandler()
    ImageHandler.imageHandler = VImageHandler()
    WindowController.windowController = VWindowController()
    UIFactory.uiFactory = VUIFactory()
  }

  override fun getReportDisplay(model: VReport): UComponent? = DReport(model).also { it.run() }

  override fun getChartDisplay(model: VChart): UComponent? = DChart(model).also { it.run() }

  class GaliteRegistry : Registry("Galite", null)

  class GaliteApplication : VApplication(GaliteRegistry()) {
    override val sologanImage get() = "slogan.png"
    override val logoImage get() = "logo_kopi.png"
    override val logoHref get() = "http://"
    override val alternateLocale get() = Locale("de", "AT")
    override val supportedLocales
      get() =
        arrayOf(Locale.FRANCE,
                Locale("de", "AT"),
                Locale("ar", "TN"))

    override fun login(
            database: String,
            driver: String,
            username: String,
            password: String,
            schema: String?
    ): DBContext? {
      val username = "admin"
      val password = "admin"
      return try {
        DBContext().apply {
          this.defaultConnection = this.createConnection(driver, database, username, password, true, schema)
        }
      } catch (exception: Throwable) {
        null
      }
    }
  }
}
