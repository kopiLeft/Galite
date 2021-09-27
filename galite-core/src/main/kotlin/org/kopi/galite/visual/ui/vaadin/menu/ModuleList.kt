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
package org.kopi.galite.visual.ui.vaadin.menu

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * The Module list Component.
 */
open class ModuleList: Composite<Component>(), HasEnabled {

  private var isMain = false

  /**
   * The root menu of this module list.
   */
  val menu: ModuleListMenu = ModuleListMenu()
  var rootMenuItem: ModuleItem? = null

  init {
    setId("module_list")

    if(getRootModuleItem() != null) {
      rootMenuItem = getRootModuleItem()
      rootMenuItem!!.rootItem = menu.addItem(rootMenuItem)
    }
  }

  override fun initContent(): Component {
    return HorizontalLayout(menu)
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
    return if(rootMenuItem == null) {
      menu.addItem(moduleItem)
    } else {
      rootMenuItem!!.rootItem!!.subMenu.addItem(moduleItem)
    }
  }

  open fun getRootModuleItem(): ModuleItem? = null

  /**
   * Sets this module list to handle main menu
   * @param isMain Should handle main menu ?
   */
  fun setMain(isMain: Boolean) {
    this.isMain = isMain
  }
}
