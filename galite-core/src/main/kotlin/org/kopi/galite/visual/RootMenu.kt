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

import org.kopi.galite.l10n.LocalizationManager
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

class RootMenu(id: Int, name: String?) {
  fun createTree(modules: Array<Module>, isSuperUser: Boolean) {
    TODO()
  }
  protected fun createTree(modules: Array<Module>,
                           root: Module?,
                           force: Boolean,
                           isSuperUser: Boolean): DefaultMutableTreeNode? {
    TODO()
  }
  fun localize(manager: LocalizationManager?) {
    TODO()
  }
  fun getId(): Int {
    TODO()
  }
  fun getRoot(): TreeNode? {
    TODO()
  }
  fun isEmpty(): Boolean {
    TODO()
  }
  companion object {
    const val ROOT_MENU_LOCALIZATION_RESOURCE = "resource/org/kopi/galite/RootMenu"
  }
}
