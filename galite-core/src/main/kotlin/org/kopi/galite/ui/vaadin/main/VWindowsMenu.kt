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

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.contextmenu.SubMenu
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.menubar.MenuBar

/**
 * The already opened windows menu.
 * The menu aims to show the opened windows by the user.
 * From this menu, the user can switch to another window.
 */
class VWindowsMenu(vWindowsMenuItem: VWindowsMenuItem) : MenuBar() {

  fun add(menu: String) {
    subMenu.addItem(menu) { selected.text = menu }
  }

  fun add(menu: VWindowsMenuItem) {
    subMenu.addItem(menu.text) { selected.text = menu.text }
  }
 //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Returns the number of items.
   * @return The number of items.
   */
  val numItems: Int
    get() = items.size// The index of the currently selected item can only be
  // obtained if the menu is showing.


  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    // Make sure that CSS styles specified for the default Menu classes
    // do not affect this menu
   element.setAttribute("name", "k-windowsMenu")
  }
  private val subMenu : SubMenu = this.addItem(vWindowsMenuItem).subMenu
  private val selected = Text("")
  val message = Div(Text("Selected: "), selected)
}
