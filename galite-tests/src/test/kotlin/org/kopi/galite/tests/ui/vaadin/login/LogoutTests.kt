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
package org.kopi.galite.tests.ui.vaadin.login

import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.logout
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.welcome.WelcomeView

import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._expectOne

class LogoutTests: GaliteVUITestBase() {

  @Test
  fun `test logout and confirm`() {
    // Login
    login()

    // Logout and confirm
    logout()

    // Return to welcome view
    _expectNone<MainWindow>()
    _expectOne<WelcomeView>()
  }

  @Test
  fun `test logout then discard`() {
    // Login
    login()

    // Click on logout button and discard
    logout(false)

    // Main window still displayed
    _expectOne<MainWindow>()
    _expectNone<WelcomeView>()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      org.kopi.galite.tests.module.initModules()
    }
  }
}
