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

import kotlin.streams.toList

import org.junit.Before
import org.junit.BeforeClass
import org.kopi.galite.testing.waitAndRunUIQueue

import com.github.mvysny.kaributesting.v10.MockVaadin
import com.github.mvysny.kaributesting.v10.Routes
import com.github.mvysny.kaributesting.v10.TestingLifecycleHook
import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10.testingLifecycleHook
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.MenuItemBase
import com.vaadin.flow.shared.communication.PushMode

/**
 * The high level class for all classes containing UI tests
 */
open class VUITestBase : VApplicationTestBase() {
  fun setupRoutes() {
    MockVaadin.setup(routes!!)
    UI.getCurrent().pushConfiguration.pushMode = PushMode.MANUAL
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

  init {
    testingLifecycleHook = this
  }

  /**
   * Logins to the application
   */
  protected fun login() {
    org.kopi.galite.testing.login(testUser, testPassword)
  }

  @Before
  fun createRoutes() {
    setupRoutes()
  }

  protected fun ClickNotifier<*>._clickAndWait(duration: Long = 500) {
    _click()
    waitAndRunUIQueue(duration)
  }

  // TODO: Remove this method when using Karibu-Testing 1.3.1
  override fun getAllChildren(component: Component): List<Component> {
    return if (component is MenuItemBase<*, *, *>) {
      (component.children.toList() + component.subMenu.items).distinct()
    } else {
      super.getAllChildren(component)
    }
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setupVaadin() {
      discoverRooterClass(GaliteApplication::class.java)
    }
  }
}
