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
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.util.Rexec
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.ApplicationContext.Companion.applicationContext
import org.kopi.galite.visual.FileHandler.Companion.fileHandler
import org.kopi.galite.visual.ImageHandler.Companion.imageHandler
import org.kopi.galite.visual.Registry
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.WindowController.Companion.windowController
import org.kopi.vkopi.lib.ui.swing.chart.DChart
import org.kopi.vkopi.lib.ui.swing.report.DReport
import org.kopi.vkopi.lib.ui.swing.visual.JApplication
import org.kopi.vkopi.lib.ui.swing.visual.JApplicationContext
import org.kopi.vkopi.lib.ui.swing.visual.JFileHandler
import org.kopi.vkopi.lib.ui.swing.visual.JImageHandler
import org.kopi.vkopi.lib.ui.swing.visual.JUIFactory
import org.kopi.vkopi.lib.ui.swing.visual.JWindowController

/**
 * TestBase class for all tests.
 */
open class JApplicationTestBase : DBSchemaTest() {

  init {
    GaliteApplication()
    setupApplication()
  }

  fun setupApplication() {
    applicationContext = JApplicationContext()
    fileHandler = JFileHandler()
    imageHandler = JImageHandler()
    windowController = JWindowController()
    UIFactory.uiFactory = JUIFactory()
  }

  override fun getReportDisplay(model: VReport): UComponent? = DReport(model).also { it.run(false) }

  override fun getChartDisplay(model: VChart): UComponent? = DChart(model).also { it.run(false) }

  class GaliteRegistry : Registry("Galite", null)

  class GaliteApplication() : JApplication(GaliteRegistry()) {
    override fun login(
            database: String,
            driver: String,
            username: String,
            password: String,
            schema: String?
    ): DBContext? {
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
      get() = Locale.FRANCE

    init {
      ApplicationConfiguration.setConfiguration(
              object : ApplicationConfiguration() {
                override fun getVersion(): String = ""
                override fun getApplicationName(): String = ""
                override fun getInformationText(): String = ""
                override fun getLogFile(): String = ""
                override fun getDebugMailRecipient(): String = ""
                override fun mailErrors(): Boolean = false
                override fun logErrors(): Boolean = true
                override fun debugMessageInTransaction(): Boolean = true
                override fun getSMTPServer(): String = ""
                override fun getFaxServer(): String = ""
                override fun getDictionaryServer(): String = ""
                override fun getRExec(): Rexec = TODO()
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
