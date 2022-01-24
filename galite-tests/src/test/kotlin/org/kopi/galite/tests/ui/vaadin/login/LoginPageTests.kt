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
package org.kopi.galite.tests.ui.vaadin.login

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.tests.db.insertIntoModule
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.base.VInputButton
import org.kopi.galite.visual.ui.vaadin.base.VInputText
import org.kopi.galite.visual.ui.vaadin.common.VSelect
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.welcome.WelcomeView

import com.github.mvysny.kaributesting.v10._expect
import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._text
import com.github.mvysny.kaributesting.v10._value
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.textfield.PasswordField

class LoginPageTests: GaliteVUITestBase() {
  private val userNameField get() = _get<VInputText> { id = "user_name" }
  private val passwordField get() = _get<PasswordField> { id = "user_password" }
  private val errorIndicator get() = _get<Span> { id = "post_error" }
  private val localesSelect get() = _get<VSelect>()
  private val loginButton get() = _get<VInputButton> { id = "login_button" }
  private val loginBoxInfo get() = _get<Span> { classes = "k-loginBox-info" }

  @Test
  fun `test login complete successfully`() {
    // Initially welcome view is displayed
    _expect<WelcomeView>(1)

    // Fill to username and password fields then click to the login button
    userNameField._value = testUser
    passwordField._value = testPassword

    loginButton._clickAndWait(100)
    // No error shown
    _expectNone<Span> { id = "post_error" }
    // Welcome view is removed
    _expectNone<WelcomeView>()
    // Main view is displayed
    _expect<MainWindow>(1)
  }

  @Test
  fun `test login fails`() {
    userNameField._value = testUser
    passwordField._value = "incorrect password"
    loginButton._clickAndWait(100)

    // Error displayed
    assertEquals("VIS-00054: Error during database login.", errorIndicator._text)
  }

  companion object {
    /**
     * Defined your test specific modules
     */
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      // Insert one module from test application just to test the login feature.
      transaction {
        insertIntoModule("1000", "org/kopi/galite/test/Menu", 0)
      }
    }
  }
}
