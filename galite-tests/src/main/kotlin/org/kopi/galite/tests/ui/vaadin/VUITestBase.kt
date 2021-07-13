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
import com.github.mvysny.kaributesting.v10.TestingLifecycleHook
import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._clickItemWithCaption
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._value
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.HasMenuItems
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.shared.communication.PushMode

/**
 * The high level class for all classes containing UI tests
 */
open class VUITestBase : VApplicationTestBase() {
  fun setupRoutes() {
    MockVaadin.setup(routes!!)
    UI.getCurrent().pushConfiguration.pushMode = PushMode.DISABLED
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

open class GaliteVUITestBase: VUITestBase(), TestingLifecycleHook {
  protected fun login() {
    // Fill to username and password fields then click to the login button
    _get<VInputText> { id = "user_name" }._value = testUser
    _get<PasswordField> { id = "user_password" }._value = testPassword
    _get<VInputButton> { id = "login_button" }._click()
  }

  @Before
  fun createRoutes() {
    setupRoutes()
  }

  protected fun HasMenuItems._clickItemWithCaptionAsynch(caption: String, action: () -> Unit) {
    _clickItemWithCaption(caption)
    Thread.sleep(500)
    action()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setupVaadin() {
      discoverRooterClass(GaliteApplication::class.java)
    }
  }
}
