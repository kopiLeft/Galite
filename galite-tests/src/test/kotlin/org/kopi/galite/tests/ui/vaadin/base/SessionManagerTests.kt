/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.tests.ui.vaadin.base

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.logout
import org.kopi.galite.tests.db.DBSchemaTest
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.ui.vaadin.base.SessionManager

import com.vaadin.flow.server.VaadinSession
import org.kopi.galite.monitoring.Application
import org.kopi.galite.monitoring.UserData

class SessionManagerTests : GaliteVUITestBase() {

  @Test
  fun `register session test`() {
    val currentSession = VaadinSession.getCurrent()
    val userData = SessionManager.userFor(currentSession)
    val browser = currentSession.browser
    val expectedUserData = UserData("",
                                    SessionManager.getOS(browser),
                                    Application(SessionManager.getBrowser(browser), browser.browserMajorVersion, browser.browserMinorVersion),
                                    browser.address,
                                    1,
                                    false)
    assertEquals(expectedUserData, userData)
  }

  @Test
  fun `loggedin changes after login and logout`() {
    val currentSession = VaadinSession.getCurrent()
    val userData = SessionManager.userFor(currentSession)!!

    login()
    assertEquals(true, userData.loggedin)

    logout()
    assertEquals(false, userData.loggedin)
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        DBSchemaTest.insertIntoModule("1000", "org/kopi/galite/test/Menu", 0)
      }
    }
  }
}
