/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.main

import java.util.Locale

import org.kopi.galite.visual.ui.vaadin.base.LocalizedProperties
import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.common.VContent
import org.kopi.galite.visual.ui.vaadin.common.VHeader
import org.kopi.galite.visual.ui.vaadin.common.VMain
import org.kopi.galite.visual.ui.vaadin.menu.ModuleList
import org.kopi.galite.visual.ui.vaadin.visual.DUserMenu
import org.kopi.galite.visual.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.ui.vaadin.window.Window

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout

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
@CssImport("./styles/galite/login.css")
class MainWindow(locale: Locale, val logo: String, val href: String, val application: VApplication)
  : VerticalLayout(), HasStyle, HasSize, Focusable<MainWindow> {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private val listeners = mutableListOf<MainWindowListener>()
  private val header = VHeader()
  private val windowsLink = VWindows()
  private val welcome = VWelcome()
  private val content = VContent()
  private val locale: String = locale.toString()
  internal var windowsList = mutableListOf<Component>()
  private val windows = mutableMapOf<Component, MenuItem>()
  private val windowsMenu = VWindowsDisplay()
  private val container = VWindowContainer(windowsMenu.menu)
  var currentWindow: Component? = null
  private var originalWindowTitle: String = ""

  init {
    val main = VMain()
    setWidthFull()

    className = Styles.MAIN_WINDOW
    setHref(href)
    setLogo(logo)
    setTarget("_blank")

    content.setContent(container)
    add(header)
    val welcomeContainer = Div()
    welcomeContainer.setId("welcome_container")
    val horizontalAlignContainer = Div()
    horizontalAlignContainer.setId("horizontal_align_container")
    horizontalAlignContainer.add(welcome)
    welcomeContainer.add(horizontalAlignContainer)
    header.setWelcome(welcomeContainer)

    windowsMenu.text = LocalizedProperties.getString(this.locale, "windowsText")
    welcome.add(windowsLink)
    main.setContent(content)
    main.setSizeFull()
    content.width = "100%"
    content.height = "100%"
    add(main)
    addLinksListeners()
    Shortcuts.addShortcutListener(this, this::goToPreviousPage, Key.PAGE_UP, KeyModifier.of("Alt"))
    Shortcuts.addShortcutListener(this, this::goToNextPage, Key.PAGE_DOWN, KeyModifier.of("Alt"))
    addBeforeUnloadListener()
    instance = this
  }

  companion object {
    lateinit var instance: MainWindow
    val locale: String get() = instance.locale
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  /**
   * Sets the main menu component.
   * @param moduleList The module list widget.
   */
  fun setMainMenu(moduleList: ModuleList) {
    header.setMainMenu(moduleList)
    header.expand(moduleList)
  }

  /**
   * Updates the title (caption) of the given window.
   * @param window The concerned window.
   * @param title The new window title.
   */
  fun updateWindowTitle(window: Component, title: String) {
    container.updateWindowTitle(window, title)
    windowsMenu.updateCaption(window, title)
    application.setPageTitle(title)
  }

  /**
   * Sets the user menu attached to this main window.
   * @param moduleList The user menu.
   */
  fun setUserMenu(moduleList: DUserMenu) {
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
   * Sets the workspace context menu.
   * @param menu The menu component.
   */
  fun setWorkspaceContextItemMenu(menu: ModuleList) {
    welcome.setWorkspaceContextItemMenu(menu)
  }

  /**
   * Adds a window to this main window.
   *
   * @param window The window to be added.
   * @param title The window title.
   */
  fun addWindow(window: Component, title: String) {
    val item = windowsMenu.addWindow(window, title)

    windowsList.add(window)
    container.addWindow(window, title)
    currentWindow = container.showWindow(window)
    // adding listener on the item to show the window in the container
    item.addClickListener {
      if (currentWindow != item.window) {
        currentWindow = container.showWindow(item.window)
        if (currentWindow is Window) {
          (currentWindow as Window).goBackToLastFocusedTextField()
          window.isVisible = true
        }
        windowsMenu.hideMenu()
      }
    }
  }

  /**
   * Removes the given window.
   * @param window The window to be removed.
   */
  fun removeWindow(window: Component) {
    windowsList.remove(window)
    currentWindow = container.removeWindow(window)
    windowsMenu.removeWindow(window)
    if (currentWindow is Window) {
      (currentWindow as Window).goBackToLastFocusedTextField()
    }
    if (windows.size <= 1) {
      windowsLink.isEnabled = false
    }
    if (currentWindow == null) {
      application.setPageTitle(originalWindowTitle)
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
   * Shows the opened windows menu.
   */
  fun showWindowsMenu() {
    windowsMenu.showMenu()
  }

  /**
   * Adds the global links listeners
   */
  fun addLinksListeners() {
    windowsLink.addClickListener {
      if (windowsLink.isEnabled) {
        windowsLink.showLabel()
        windowsLink.focus()
        showWindowsMenu()
      }
    }
  }

  /**
   * Adds a detatch listener to be excecuted only when a window is closed or navigated away
   */
  fun addWindowDetachListener(onDetach: () -> Unit) {
    addDetachListener {
      isWindowActuallyClosing { isClosing ->
        if (isClosing) {
          println("The window is closing or navigating away.")
          onDetach()
        } else {
          println("The window was refreshed.")
        }
      }
    }
  }

  /**
   * Determines if the window is actually closing (i.e., the user is closing a tab or
   * navigating away) as opposed to simply refreshing the page.
   *
   * This method uses a `localStorage` flag, `pageReload`, that is set or removed by the
   * `addBeforeUnloadListener` method. The logic works as follows:
   *
   * - If `pageReload` is found and set to `'true'`, it indicates that a page refresh
   *   occurred. In this case, the method returns `false` to indicate that the window
   *   is not actually closing.
   * - If `pageReload` is not found, it suggests that the window is closing (either
   *   a tab close or navigation away), so the method returns `true`.
   *
   * @param callback A lambda function to handle the result: `true` if the window is closing (not a refresh), `false` otherwise.
   */
  private fun isWindowActuallyClosing(callback: (Boolean) -> Unit) {
    UI.getCurrent().page.executeJs("return localStorage.getItem('pageReload') === 'true';").then {
      val isRefresh = it.asBoolean()

      callback(!isRefresh)
    }
  }

  /**
   * Adds a JavaScript listener for the `beforeunload` event to detect when a page is
   * refreshed or when a tab is being closed/navigated away from.
   *
   * This listener distinguishes between a page refresh (navigation type `1`) and other
   * events such as closing the browser tab or navigating away. It uses `localStorage`
   * to set a flag based on the detected action, which can be used server-side to infer
   * user actions when combined with a Vaadin `DetachListener`.
   *
   * - On page refresh: A flag `pageReload` is set in the browser's `localStorage`.
   * - On tab close or navigation away: The `pageReload` flag is removed from `localStorage`.
   *
   * Example Usage:
   * This function is useful for determining whether to perform cleanup operations (like
   * closing a database connection) only on tab close or navigation events but not on a refresh.
   */
  private fun addBeforeUnloadListener() {
    UI.getCurrent().page.executeJs(
      """
        let isUnloading = false;

        window.addEventListener('beforeunload', function(event) {
            isUnloading = true;
            // Save a flag to detect page reload
            if (performance.navigation.type === 1) {
                // Page refresh detected
                localStorage.setItem('pageReload', 'true');
            } else {
                // Tab close or navigation away
                localStorage.removeItem('pageReload');
            }
        });

        // Use 'unload' event as a fallback for closing/navigating away cases
        window.addEventListener('unload', function(event) {
            if (!isUnloading) {
                localStorage.removeItem('pageReload');
            }
        });
        """
    )
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
  fun removeMainWindowListener(l: MainWindowListener) {
    listeners.add(l)
  }

  /**
   * Fires on support action.
   */
  internal fun fireOnSupport() {
    for (l in listeners) {
      l.onSupport()
    }
  }

  /**
   * Fires on help action.
   */
  internal fun fireOnHelp() {
    for (l in listeners) {
      l.onHelp()
    }
  }

  /**
   * Fires on admin action.
   */
  internal fun fireOnAdmin() {
    for (l in listeners) {
      l.onAdmin()
    }
  }

  /**
   * Fires on logout action.
   */
  internal fun fireOnLogout() {
    for (l in listeners) {
      l.onLogout()
    }
  }

  /**
   * Fires on user action.
   */
  internal fun fireOnUser() {
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

  fun resetTitle() {
    application.setPageTitle(originalWindowTitle)
  }

  override fun onAttach(attachEvent: AttachEvent?) {
    originalWindowTitle = ui.get().internals.title.orEmpty()
  }

  /**
   * Shows the next or previous window according to a flag
   * @param next Should we goto the next window ?
   * Otherwise, it is the previous window that must be shown.
   */
  internal fun gotoWindow(next: Boolean) {
    if(container.isEmpty) {
      return
    }

    currentWindow = if (next) {
      container.showNextWindow()
    } else {
      container.showPreviousWindow()
    }
    if (currentWindow is Window) {
      (currentWindow as Window).goBackToLastFocusedTextField()
    }
  }
}
