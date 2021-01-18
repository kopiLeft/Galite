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

import java.awt.Component

import org.kopi.galite.ui.vaadin.menu.ModuleItem
import org.kopi.galite.ui.vaadin.menu.ModuleList
import org.kopi.galite.ui.vaadin.menu.ModuleListMenu

import com.vaadin.flow.component.html.Div

/**
 * The welcome text widget.
 * Contains also the logout button to disconnect from application.
 */
class VWelcome : Div() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  // menues
  private var userMenu = ModuleListMenu(true)
  private var adminMenu = ModuleListMenu(true)
  private var bookmarksMenu = ModuleListMenu(true)
  private var workspaceContextMenu = ModuleListMenu(true)

  // items
  private var userItem: ModuleItem = ModuleItem()
  private var adminItem = ModuleItem()
  private var bookmarksItem = ModuleItem()
  private var workspaceContextItem = ModuleItem()
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates the welcome text widget.
   */
  init {
    setId("welcome")
    userMenu.addItem(userItem, false, true)
    adminMenu.addItem(adminItem, false, true)
    bookmarksMenu.addItem(bookmarksItem, false, true)
    workspaceContextMenu.addItem(workspaceContextItem, false, true)
    add(workspaceContextMenu)
    add(bookmarksMenu)
    add(adminMenu)
    add(userMenu)
    userMenu.autoOpen = false
    adminMenu.autoOpen = false
    bookmarksMenu.autoOpen = false
    workspaceContextMenu.autoOpen = false
    userMenu.setId("user_menu")
    adminMenu.setId("admin_menu")
    bookmarksMenu.setId("bookmarks_menu")
    workspaceContextMenu.setId("wrkcontext_menu")
  }

  /**
   * Sets the user menu.
   * @param menu The menu widget.
   */
  fun setUserMenu(menu: ModuleList) {
    userItem.root = false
    userItem.buildContent()
    userItem.setIcon("user")
    if (menu.menu != null) {
      userItem.subMenu = menu.menu
    }
  }

  /**
   * Sets the connected user.
   * @param username The user name.
   */
  fun setConnectedUser(username: String?) {
    userItem.caption = username
  }

  /**
   * Sets the admin menu.
   * @param menu The menu widget.
   */
  fun setAdminMenu(menu: ModuleList) {
    adminItem.root = false
    adminItem.buildContent()
    adminItem.setIcon("cog")
    if (menu.menu != null) {
      adminItem.subMenu = menu.menu
    }
  }

  /**
   * Sets the bookmarks menu.
   * @param menu The menu widget.
   */
  fun setBookmarksMenu(menu: ModuleList) {
    bookmarksItem.root = false
    bookmarksItem.buildContent()
    bookmarksItem.setIcon("star")
    if (menu.menu != null) {
      bookmarksItem.subMenu = menu.menu
    }
  }

  /**
   * Sets the workspace context menu.
   * @param menu The menu widget.
   */
  fun setWorkspaceContextItemMenu(menu: Component) {
    workspaceContextItem.root = false
    workspaceContextItem.buildContent()
    workspaceContextItem.setIcon("map-marker")
  }
}
