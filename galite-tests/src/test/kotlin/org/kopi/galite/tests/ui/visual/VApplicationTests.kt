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

package org.kopi.galite.tests.ui.visual

import java.util.Locale

import org.junit.Test

import org.kopi.galite.db.DBContext
import org.kopi.galite.ui.visual.VApplication
import org.kopi.galite.visual.Registry

class VApplicationTests {

  class GaliteRegistry: Registry("Galite", null)

  class GaliteApplication: VApplication(GaliteRegistry()) {
    override val supportedLocales: Array<Locale>
      get() = arrayOf(Locale.FRANCE,
              Locale("de", "AT"),
              Locale("ar", "TN"))
    override val sologanImage: String
      get() = "resource/slogan.png"
    override val logoImage: String
      get() = "resource/logo_kopi.png"
    override val logoHref: String
      get() = "http://"
    override val alternateLocale: Locale
      get() = Locale("de", "AT")
    override fun login(
        database: String,
        driver: String,
        username: String,
        password: String,
        schema: String
    ): DBContext? {
      return try {
        DBContext().apply {
          this.defaultConnection = this.createConnection(driver, database, username, password, true, schema)
        }
      } catch (e: Throwable) {
        null
      }
    }
  }

  val galiteApplication = GaliteApplication()

  @Test
  fun testApplication() {
  }
}
