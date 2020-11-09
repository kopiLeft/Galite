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

import org.kopi.galite.db.DBContext
import org.kopi.galite.ui.visual.VApplication
import org.kopi.galite.visual.Registry

/**
 * TestBase class for all tests.
 */
open class ApplicationTestBase : TestBase() {
  class GaliteRegistry: Registry("Galite", null)

  class GaliteApplication: VApplication(GaliteRegistry()) {
    override val sologanImage get() = "slogan.png"
    override val logoImage get() = "logo_kopi.png"
    override val logoHref get() = "http://"
    override val alternateLocale get() = Locale("de", "AT")
    override val supportedLocales get() =
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
      return try {
        DBContext().apply {
          this.defaultConnection = this.createConnection(driver, database, username, password, true, schema)
        }
      } catch (exception: Throwable) {
        null
      }
    }
  }

  init {
    setupApplication()
  }

  fun setupApplication() {
    val app = GaliteApplication()
  }
}
