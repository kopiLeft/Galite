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
package org.kopi.galite.ui.vaadin.visual

import org.kopi.galite.ui.vaadin.menu.ModuleItem
import org.kopi.galite.visual.VMenuTree

/**
 * A module menu implementation that uses the menu tree
 * model. This will not display a menu tree but an horizontal
 * menu with vertical sub menus drops. Creates the module menu
 * from a menu tree model.
 *
 * @param model The menu tree model.
 */
class DUserMenu(model: VMenuTree) : DMenu(model) {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun getRootModuleItem(): ModuleItem = ModuleItem()

  override val type: Int
    get() = VMenuTree.USER_MENU
}
