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
package org.kopi.galite.tests.ui.swing

import java.util.Locale

import org.kopi.galite.base.UComponent
import org.kopi.galite.chart.VChart
import org.kopi.galite.db.DBContext
import org.kopi.galite.report.VReport
import org.kopi.galite.tests.common.ApplicationTestBase
import org.kopi.galite.tests.common.GaliteRegistry
import org.kopi.galite.util.Rexec
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.ImageHandler
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.WindowController
import org.kopi.vkopi.lib.ui.swing.chart.DChart
import org.kopi.vkopi.lib.ui.swing.report.DReport
import org.kopi.vkopi.lib.ui.swing.visual.JApplication
import org.kopi.vkopi.lib.ui.swing.visual.JApplicationContext
import org.kopi.vkopi.lib.ui.swing.visual.JFileHandler
import org.kopi.vkopi.lib.ui.swing.visual.JImageHandler
import org.kopi.vkopi.lib.ui.swing.visual.JUIFactory

/**
 * TestBase class for all tests.
 */
open class JApplicationTestBase : ApplicationTestBase() {

  init {
    GaliteApplication()
    setupApplication()
  }

  fun setupApplication() {
    ApplicationContext.applicationContext = applicationContext
    FileHandler.fileHandler = fileHandler
    ImageHandler.imageHandler = imageHandler
    WindowController.windowController = windowController
    UIFactory.uiFactory = uiFactory
  }

  override fun getReportDisplay(model: VReport): UComponent? = DReport(model).also { it.run(false) }

  override fun getChartDisplay(model: VChart): UComponent? = DChart(model).also { it.run(false) }

  companion object {
    val applicationContext = JApplicationContext()
    val fileHandler = JFileHandler()
    val imageHandler = JImageHandler()
    val uiFactory = JUIFactory()
  }

  class GaliteApplication : JApplication(GaliteRegistry()) {
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

    override val dBContext: DBContext? = null
    override var isGeneratingHelp: Boolean = false
    override val isNoBugReport: Boolean
      get() = true

    override val defaultLocale: Locale
      get() = Locale.UK

    init {
      ApplicationConfiguration.setConfiguration(
              object : ApplicationConfiguration() {
                override val version get(): String = "1.0"
                override val applicationName get(): String = "MyApp"
                override val informationText get(): String = "info"
                override val logFile get(): String = ""
                override val debugMailRecipient get(): String = ""
                override fun getSMTPServer(): String = ""
                override val faxServer get(): String = ""
                override val dictionaryServer get(): String = ""
                override fun mailErrors(): Boolean = false
                override fun logErrors(): Boolean = true
                override fun debugMessageInTransaction(): Boolean = true
                override val RExec get(): Rexec = TODO()
                override fun getStringFor(var1: String): String = TODO()
                override fun getIntFor(var1: String): Int {
                  val var2 = this.getStringFor(var1)
                  return var2.toInt()
                }

                override fun getBooleanFor(var1: String): Boolean {
                  return java.lang.Boolean.valueOf(this.getStringFor(var1))
                }

                override fun isUnicodeDatabase(): Boolean = false
                override fun useAcroread(): Boolean = TODO()
              }
      )
    }
  }
}
