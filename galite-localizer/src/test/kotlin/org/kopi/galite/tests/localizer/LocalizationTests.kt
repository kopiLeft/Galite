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
package org.kopi.galite.tests.localizer

import java.io.File
import java.util.Locale

import kotlin.test.assertEquals

import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.localizer.localizeWindows
import org.kopi.galite.database.Connection
import org.kopi.galite.visual.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.Registry
import org.kopi.vkopi.lib.ui.swing.visual.JApplication

class LocalizationTests {

  @Test
  fun `test localization`() {
    val sourceFilePath = "src/main/resources${File.separator}${this.javaClass.`package`.name.replace(".", "/")}${File.separatorChar}"
    val generatedFile = File("${sourceFilePath}/FormSample-${Locale.FRANCE}.xml")

    try {
      localizeWindows(testURL, testDriver, testUser, testPassword, Locale.FRANCE)
      assertEquals(true, generatedFile.exists())
    } finally {
      generatedFile.delete()
    }
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun init() {
      initDatabase()
      VApplication.instance = object : JApplication(Registry("", null)) {
        override val dBConnection get() = null
        override val defaultLocale get() = Locale.UK
        override val isNoBugReport get() = true
        override fun login(
          database: String,
          driver: String,
          username: String,
          password: String,
          schema: String?,
          maxRetries: Int?,
          waitMin: Long?,
          waitMax: Long?
        ): Connection? {
          return null
        }
      }
    }
  }
}
