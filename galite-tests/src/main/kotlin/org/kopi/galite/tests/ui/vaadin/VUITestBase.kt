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

package org.kopi.galite.tests.ui.vaadin

import java.time.temporal.Temporal

import org.junit.Before
import org.junit.BeforeClass
import org.kopi.galite.testing.waitAndRunUIQueue
import org.kopi.galite.type.format

import com.github.mvysny.kaributesting.v10.*
import com.vaadin.flow.component.ClickNotifier
import com.vaadin.flow.component.UI
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

open class GaliteVUITestBase: VUITestBase(), TestingLifecycleHook by TestingLifecycleHookVaadin23_1(TestingLifecycleHook.default) {

  init {
    testingLifecycleHook = this
  }

  /**
   * Logins to the application
   */
  protected fun login() {
    org.kopi.galite.testing.login(testUser, testPassword)
  }

  /**
   * Runs the UI Queue Automatically
   *   --> MockVaadin.clientRoundtrip() runs all submitted tasks and blocks until all the tasks have been processed.
   * see : https://github.com/mvysny/karibu-testing/tree/master/karibu-testing-v10#testing-asynchronous-application
   */
  override fun awaitBeforeLookup() {
    MockVaadin.clientRoundtrip()
  }

  @Before
  fun createRoutes() {
    setupRoutes()
  }

  protected fun ClickNotifier<*>._clickAndWait(duration: Long = 500) {
    _click()
    waitAndRunUIQueue(duration)
  }

  fun Temporal?.defaultFormat(): String? = this?.let { format("yyyy-MM-dd HH:mm:ss") }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setupVaadin() {
      discoverRooterClass(GaliteApplication::class.java)
    }
  }
}
