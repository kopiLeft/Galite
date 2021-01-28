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

import org.kopi.galite.ui.vaadin.menu.ModuleItem
import org.kopi.galite.ui.vaadin.menu.ModuleList
import org.kopi.galite.ui.vaadin.menu.WelcomeListMenu

import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * The welcome text widget.
 * Contains also the logout button to disconnect from application.
 */
class VWelcome : HorizontalLayout() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  // menus
  private val userMenu = WelcomeListMenu()
  private val adminMenu = WelcomeListMenu()
  private val bookmarksMenu = WelcomeListMenu()
  private val workspaceContextMenu = WelcomeListMenu()

  // items
  private val userItem = ModuleItem(VaadinIcon.USER)
  private val adminItem = ModuleItem(VaadinIcon.COG)
  private val bookmarksItem = ModuleItem(VaadinIcon.STAR)
  private val workspaceContextItem = ModuleItem(VaadinIcon.MAP_MARKER)
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates the welcome text component.
   */
  init {
    setId("welcome")
    userMenu.addItem(userItem)
    adminMenu.addItem(adminItem)
    bookmarksMenu.addItem(bookmarksItem)
    workspaceContextMenu.addItem(workspaceContextItem)
    add(userMenu)
    add(adminMenu)
    add(bookmarksMenu)
    add(workspaceContextMenu)
    userMenu.setId("user_menu")
    adminMenu.setId("admin_menu")
    bookmarksMenu.setId("bookmarks_menu")
    workspaceContextMenu.setId("wrkcontext_menu")
  }

  /**
   * Sets the user menu.
   * @param menu The menu component.
   */
  fun setUserMenu(menu: ModuleList) {
    // TODO
  }

  /**
   * Sets the connected user.
   * @param username The user name.
   */
  fun setConnectedUser(username: String) {
    userItem.setCaption(username)
  }

  /**
   * Sets the admin menu.
   * @param menu The menu component.
   */
  fun setAdminMenu(menu: ModuleList) {
    // TODO
  }

  /**
   * Sets the bookmarks menu.
   * @param menu The menu component.
   */
  fun setBookmarksMenu(menu: ModuleList) {
    // TODO
  }
}
