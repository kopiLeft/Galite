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

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.common.VContent
import org.kopi.galite.ui.vaadin.common.VHeader
import org.kopi.galite.ui.vaadin.common.VMain
import org.kopi.galite.ui.vaadin.menu.ModuleList
import org.kopi.galite.ui.vaadin.visual.DAdminMenu
import org.kopi.galite.ui.vaadin.visual.DBookmarkMenu
import org.kopi.galite.ui.vaadin.visual.DMainMenu
import org.kopi.galite.ui.vaadin.visual.DMenu
import org.kopi.galite.ui.vaadin.visual.DUserMenu

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.contextmenu.MenuItem

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
class MainWindow(locale: Locale, val logo: String, val href: String) : AppLayout(), HasStyle, Focusable<MainWindow> {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private val listeners = mutableListOf<MainWindowListener>()
  private var menus: MutableList<ModuleList>? = null
  private val header = VHeader()
  private val windowsLink = VWindows()
  private val welcome = VWelcome()
  private val content = VContent()
  private val container = VWindowContainer()
  private val locale: String? = null
  private var windowsList = mutableListOf<Component>()
  private val windows = mutableMapOf<Component, MenuItem>()
  private val windowsMenu = VWindowsDisplay()
  private var currentWindow: Component? = null
  private val originalWindowTitle: String? = null

  init {
    val main = VMain()

    className = Styles.MAIN_WINDOW
    setHref(href)
    setLogo(logo)
    setTarget("_blank")

    content.setContent(container)
    addToNavbar(header)
    header.setWelcome(welcome)
    welcome.add(windowsLink)
    main.setContent(content)
    main.setSizeFull()
    content.width = "100%"
    content.height = "100%"
    setContent(main)
    Shortcuts.addShortcutListener(this, this::goToPreviousPage, Key.PAGE_UP, KeyModifier.of("Alt"))
    Shortcuts.addShortcutListener(this, this::goToNextPage, Key.PAGE_DOWN, KeyModifier.of("Alt"))
    instance = this
  }

  companion object {
    private var instance: MainWindow? = null
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  /**
   * Adds a menu to this main window.
   * @param moduleList The module menu to be added
   */
  fun addMenu(moduleList: DMenu) {
    if (menus == null) {
      menus = ArrayList()
    }
    menus!!.add(moduleList)

    when (moduleList) {
      is DMainMenu -> setMainMenu(moduleList)
      is DUserMenu -> setUserMenu(moduleList)
      is DAdminMenu -> setAdminMenu(moduleList)
      is DBookmarkMenu -> setBookmarksMenu(moduleList)
    }
  }

  /**
   * Sets the main menu component.
   * @param moduleList The module list widget.
   */
  fun setMainMenu(moduleList: ModuleList) {
    header.setMainMenu(moduleList)
  }

  /**
   * Sets the user menu attached to this main window.
   * @param moduleList The user menu.
   */
  fun setUserMenu(moduleList: ModuleList) {
    welcome.setUserMenu(moduleList)
  }

  /**
   * Sets the admin menu attached to this main window.
   * @param moduleList The admin menu.
   */
  fun setAdminMenu(moduleList: ModuleList) {
    welcome.setAdminMenu(moduleList)
  }

  /**
   * Sets the bookmarks menu attached to this main window.
   * @param menu The favorites menu.
   */
  fun setBookmarksMenu(menu: ModuleList) {
    welcome.setBookmarksMenu(menu)
  }

  /**
   * Adds a window to this main window.
   * @param window The window to be added.
   */
  fun addWindow(window: Component) {
    windowsList.add(window)
    content.add(window)
  }

  /**
   * Removes the given window.
   * @param window The window to be removed.
   */
  fun removeWindow(window: Component) {
    if (equals(window.parent)) {
      windowsList.remove(window)
      remove(window)
      /*if (window is PopupWindow) { TODO
        (window as PopupWindow).fireOnClose() // fire close event
      }*/
    }
  }

  /**
   * The connected user name.
   */
  var connectedUser: String = ""
    set(value) {
      field = value

      welcome.setConnectedUser(value)
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


  /**
   * Sets the href for the anchor element.
   * @param href the href
   */
  fun setHref(href: String?) {
    header.setHref(href)
  }

  /**
   * Sets the target frame.
   * @param target The target frame.
   */
  fun setTarget(target: String) {
    header.setTarget(target)
  }

  /**
   * Sets the company logo image.
   * @param url The image URL.
   * @param alt The alternate text.
   */
  fun setImage(url: String?, alt: String?) {
    header.setImage(url!!, alt)
  }

  /**
   * Sets the company logo image.
   *
   * @param logo The logo image URL.
   * @param alt  The alternate text.
   */
  fun setLogo(logo: String?, alt: String? = null) {
    if (logo != null) {
      header.setImage(logo, alt)
    }
  }

  /**
   * Shows the next window
   */
  fun goToNextPage(event: ShortcutEvent) {
    gotoWindow(true)
  }

  /**
   * Shows the previous window
   */
  fun goToPreviousPage(event: ShortcutEvent) {
    gotoWindow(false)
  }

  /**
   * Shows the next or previous window according to a flag
   * @param next Should we goto the next window ?
   * Otherwise, it is the previous window that must be shown.
   */
  protected fun gotoWindow(next: Boolean) {
    // TODO
  }
}
