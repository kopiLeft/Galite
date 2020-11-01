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

package org.kopi.galite.visual

//import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.MainWindowServerRpc
//import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.MainWindowState

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.icon.Icon
import java.io.Serializable
import java.util.*

/**
 * The main application window.
 */
class MainWindow(locale: Locale,
                 logo: Resource,
                 href: String) : Component() {
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  /**
   * Adds a menu to this main window.
   * @param moduleList The module menu to be added
   */
  fun addMenu(moduleList: ModuleList) {
    if (menus == null) {
      menus = ArrayList<ModuleList>()
    }
    menus!!.add(moduleList)

    addMenu(moduleList)

  }

  /**
   * Adds a window to this main window.
   * @param window The window to be added.
   */
  fun addWindow(window: Component) {
    if (windows == null) {
      windows = LinkedList<Component>()
    }
    windows!!.add(window)
    addWindow(window)

  }

  /**
   * Removes the given window.
   * @param window The window to be removed.
   */
  fun removeWindow(window: Component) {
    if (equals(window.parent)) {
      windows!!.remove(window)
      removeWindow(window)
      if (window is CloseListener) {
        (window as CloseListener).onClose() // fire close event

      }
    }
  }

  operator fun iterator(): Iterator<Component> {
    val components: MutableList<Component>
    components = ArrayList<Component>()
    components.addAll(menus!!)
    components.addAll(windows!!)
    return components.iterator()
  }

  fun replaceComponent(oldComponent: Component?, newComponent: Component?) {
    // cannot replace component.
  }

  val componentCount: Int
    get() = windows!!.size + if (menus != null) 1 else 0

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
      if (l != null) {
        l.onSupport()
      }
    }
  }

  /**
   * Fires on help action.
   */
  protected fun fireOnHelp() {
    for (l in listeners) {
      if (l != null) {
        l.onHelp()
      }
    }
  }

  /**
   * Fires on admin action.
   */
  protected fun fireOnAdmin() {
    for (l in listeners) {
      if (l != null) {
        l.onAdmin()
      }
    }
  }

  /**
   * Fires on logout action.
   */
  protected fun fireOnLogout() {
    for (l in listeners) {
      if (l != null) {
        l.onLogout()
      }
    }
  }

  /**
   * Fires on user action.
   */
  protected fun fireOnUser() {
    for (l in listeners) {
      if (l != null) {
        l.onUser()
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var menus: MutableList<ModuleList>? = null
  private var windows: LinkedList<Component>?
  private val listeners: MutableList<MainWindowListener>

  /**
   * Creates the main window server component.
   * @param locale The application locale.
   * @param logo The application logo
   * @param href The logo link.
   */
  init {
    com.vaadin.flow.component.icon.Icon(logo) //= logo
    listeners = ArrayList<MainWindowListener>()
    windows = LinkedList<Component>()
  }
}