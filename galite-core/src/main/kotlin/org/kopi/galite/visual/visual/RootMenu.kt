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

package org.kopi.galite.visual.visual

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

import org.kopi.galite.visual.l10n.LocalizationManager

/**
 * A root menu must provide its ID and name. The root tree node
 * of this menu should be provided for further uses.
 *
 * @param id The root menu ID.
 * @param name The root menu name.
 */
class RootMenu(id: Int, name: String) {

  private val rootModule: Module

  /**
   * return the menu tree
   */
  var root: TreeNode? = null
    private set

  init {
    rootModule = Module(id,
                        0,
                        name,
                        ROOT_MENU_LOCALIZATION_RESOURCE,
                        null,
                        Module.ACS_PARENT, Int.MAX_VALUE,
                        null)
  }

  /**
   * Creates the module tree nodes for this root menu.
   * @param modules The accessible modules for the connected user.
   * @param isSuperUser Is the connected user is a super user ?
   */
  fun createTree(modules: Array<Module>, isSuperUser: Boolean) {
    this.root = createTree(modules, rootModule, false, isSuperUser)
  }

  /**
   * Creates the module tree for the given root module.
   * @param modules The accessible modules.
   * @param root The root module.
   * @param force Should we force module accessibility ?
   * @param isSuperUser Is the connected user is a super user ?
   * @return The local root tree node.
   */
  internal fun createTree(modules: Array<Module>,
                          root: Module,
                          force: Boolean,
                          isSuperUser: Boolean): DefaultMutableTreeNode? {
    var force = force

    if (root.accessibility == Module.ACS_TRUE || isSuperUser) {
      force = true
    }
    return if (root.objectName != null) {
      if (force) DefaultMutableTreeNode(root) else null
    } else {
      var self: DefaultMutableTreeNode? = null

      modules.forEach {
        if (it.parent == root.id) {
          val node: DefaultMutableTreeNode? = createTree(modules, it, force, isSuperUser)

          if (node != null) {
            if (self == null) {
              self = DefaultMutableTreeNode(root)
            }
            self!!.add(node)
          }
        }
      }
      self
    }
  }

  /**
   * Localizes this root menu.
   * @param manager The localization manager.
   */
  fun localize(manager: LocalizationManager?) {
    if (manager != null) {
      rootModule.localize(manager)
    }
  }

  /**
   * return the identifier of the menu
   */
  fun getId(): Int {
    return rootModule.id
  }

  /**
   * Returns true if this root menu does not contain any module.
   * @return True if this root menu does not contain any module.
   */
  fun isEmpty(): Boolean {
    return root == null
  }

  companion object {
    const val ROOT_MENU_LOCALIZATION_RESOURCE = "org/kopi/galite/visual/RootMenu"
  }
}
