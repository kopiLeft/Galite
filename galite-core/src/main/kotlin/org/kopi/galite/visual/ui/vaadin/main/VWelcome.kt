/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.galite.visual.ui.vaadin.menu.ModuleList

import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * The welcome text component.
 * Contains also the logout button to disconnect from application.
 */
class VWelcome : HorizontalLayout() {
  // menus
  private lateinit var userMenu: ModuleList
  private lateinit var adminMenu: ModuleList
  private lateinit var bookmarksMenu: ModuleList
  private lateinit var workspaceContextMenu: ModuleList

  init {
    setId("welcome")
  }

  /**
   * Sets the user menu.
   * @param menu The menu component.
   */
  fun setUserMenu(menu: ModuleList) {
    userMenu = menu
    menu.setId("user_menu")
    menu.rootMenuItem!!.setIcon(IronIcons.ACCOUNT_BOX.create())
    addComponentAsFirst(menu)
  }

  /**
   * Sets the connected user.
   * @param username The user name.
   */
  fun setConnectedUser(username: String) {
    userMenu.rootMenuItem!!.setCaption(username)
  }

  /**
   * Sets the admin menu.
   * @param menu The menu component.
   */
  fun setAdminMenu(menu: ModuleList) {
    adminMenu = menu
    menu.setId("admin_menu")
    val icon  = VaadinIcon.COG.create()
    icon.setSize("21.5px")
    menu.rootMenuItem!!.setIcon(icon)
    addComponentAtIndex(1, menu)
  }

  /**
   * Sets the bookmarks menu.
   * @param menu The menu component.
   */
  fun setBookmarksMenu(menu: ModuleList) {
    bookmarksMenu = menu
    menu.setId("bookmarks_menu")
    menu.rootMenuItem!!.setIcon(IronIcons.STAR.create())
    addComponentAtIndex(2, menu)
  }

  /**
   * Sets the workspace context menu.
   * @param menu The menu component.
   */
  fun setWorkspaceContextItemMenu(menu: ModuleList) {
    workspaceContextMenu = menu
    menu.setId("wrkcontext_menu")
    menu.rootMenuItem!!.setIcon(IronIcons.ROOM.create())
    addComponentAtIndex(3, menu)
  }
}
