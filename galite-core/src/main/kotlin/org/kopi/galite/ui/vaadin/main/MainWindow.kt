/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.ui.vaadin.main

import java.util.Locale

import kotlin.collections.MutableList

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.kopi.galite.ui.vaadin.menu.ModuleList

/**
 * Main application window composed of a header and content.
 * The content.
 * This main window will have a full size to fit with the browser
 * screen size.
 *
 * @param locale The application locale.
 * @param logo The application logo
 * @param href The logo link.
 */
class MainWindow(locale: Locale,
                 val logo: String,
                 val href: String) : Focusable<MainWindow>, VerticalLayout() {
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  /**
   * Adds a menu to this main window.
   * @param moduleList The module menu to be added
   */
  fun addMenu(moduleList: ModuleList) {
    if (menus == null) {
      menus = ArrayList()
    }
    menus!!.add(moduleList)
    add(moduleList)
  }

  /**
   * Adds a window to this main window.
   * @param window The window to be added.
   */
  fun addWindow(window: Component) {
    windows.add(window)
    add(window)
  }

  /**
   * Removes the given window.
   * @param window The window to be removed.
   */
  fun removeWindow(window: Component) {
    if (equals(window.parent)) {
      windows.remove(window)
      remove(window)
      /*if (window is PopupWindow) { TODO
        (window as PopupWindow).fireOnClose() // fire close event
      }*/
    }
  }

  /**
   * The connected user name.
   */
  var connectedUser: String? = null

  operator fun iterator(): Iterator<Component> {
    val components: MutableList<Component>
    components = ArrayList<Component>()
    components.addAll(menus!!)
    components.addAll(windows)
    return components.iterator()
  }

  /**
   * Adds a main window listener.
   * @param l the listener to be registered.
   */
  fun addMainWindowListener(l: MainWindowListener) {
    listeners.add(l)
  }

  /**
   * Removes a main window listener.
   * @param l the listener to be removed.
   */
  fun RemoveMainWindowListener(l: MainWindowListener) {
    listeners.add(l)
  }

  /**
   * Fires on support action.
   */
  protected fun fireOnSupport() {
    for (l in listeners) {
      l.onSupport()
    }
  }

  /**
   * Fires on help action.
   */
  protected fun fireOnHelp() {
    for (l in listeners) {
      l.onHelp()
    }
  }

  /**
   * Fires on admin action.
   */
  protected fun fireOnAdmin() {
    for (l in listeners) {
      l.onAdmin()
    }
  }

  /**
   * Fires on logout action.
   */
  protected fun fireOnLogout() {
    for (l in listeners) {
      l.onLogout()
    }
  }

  /**
   * Fires on user action.
   */
  protected fun fireOnUser() {
    for (l in listeners) {
      l.onUser()
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var menus: MutableList<ModuleList>? = null
  private var windows = mutableListOf<Component>()
  private val listeners = mutableListOf<MainWindowListener>()
}
