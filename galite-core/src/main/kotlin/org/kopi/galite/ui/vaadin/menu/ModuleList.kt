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
package org.kopi.galite.ui.vaadin.menu

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div

/**
 * The Module list Component.
 */
open class ModuleList : Div(), HasComponents {

  private var isMain = false

  /**
   * The root menu of this module list.
   */
  var menu: ModuleListMenu = ModuleListMenu()
    set(value) {
      removeAll()
      field = value;
      add(field); // add it to container.
    }

  init {
    setId("module_list")
    setWidthFull()
    add(menu); // add it to container.
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Adds Item to this menu bar
   *
   * @param caption the module caption
   */
  fun addItem(caption: String, help: String?): MenuItem {
    val moduleItem = ModuleItem(help = help)
    moduleItem.setCaption(caption)
    return menu.addItem(moduleItem)
  }

  /**
   * Sets this module list to handle main menu
   * @param isMain Should handle main menu ?
   */
  fun setMain(isMain: Boolean) {
    this.isMain = isMain
  }
}
