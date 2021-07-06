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

package org.kopi.galite.tests.ui.vaadin

import org.junit.Before
import org.junit.BeforeClass
import org.kopi.galite.ui.vaadin.base.VInputButton
import org.kopi.galite.ui.vaadin.base.VInputText

import com.github.mvysny.kaributesting.v10.MockVaadin
import com.github.mvysny.kaributesting.v10.Routes
import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._clickItemWithCaption
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.contextmenu.HasMenuItems
import com.vaadin.flow.component.textfield.PasswordField

/**
 * The high level class for all classes containing UI tests
 */
open class VUITestBase : VApplicationTestBase() {
  fun setupRoutes() {
    MockVaadin.setup(routes!!)
  }

  companion object {
    fun discoverRooterByPackage(packageName: String) {
      routes = Routes().autoDiscoverViews(packageName)
    }

    fun discoverRooterClass(clazz: Class<*>) {
      routes = Routes().autoDiscoverViews(clazz.`package`.name)
    }

    var routes: Routes? = null
  }
}

open class GaliteVUITestBase: VUITestBase() {
  protected fun login() {
    // Fill to username and password fields then click to the login button
    _get<VInputText> { id = "user_name" }._value = testUser
    _get<PasswordField> { id = "user_password" }._value = testPassword
    _get<VInputButton> { id = "login_button" }._clickAsynch()
  }

  @Before
  fun createRoutes() {
    setupRoutes()
  }

  protected fun HasMenuItems._clickItemWithCaptionAsynch(caption: String, action: () -> Unit) {
    _clickItemWithCaption(caption)
    MockVaadin.runUIQueue()
    Thread.sleep(500)
    MockVaadin.runUIQueue()
    action()
  }

  protected fun ClickNotifier<*>._clickAsynch(action: (() -> Unit)? = null) {
    this._click()
    MockVaadin.runUIQueue()
    Thread.sleep(200)
    MockVaadin.runUIQueue()
    action?.invoke()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setupVaadin() {
      discoverRooterClass(GaliteApplication::class.java)
    }
  }
}
