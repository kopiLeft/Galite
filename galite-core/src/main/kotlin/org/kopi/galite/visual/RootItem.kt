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

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

/**
 * A root item must provide its ID and name. The root tree node
 * of this item should be provided for further uses.
 *
 * @param id The root item ID.
 * @param name The root item name.
 */
class RootItem(id: Int, name: String) {

  private val rootItem: Item = Item(id,
                                    0,
                                    name,
                                    null,
                                    null,
                                    false,
                                    false,
                                    null,
                                    name)

  /**
   * The root node
   */
  var root: TreeNode? = null
    private set

  init {
    rootItem.level = 0
  }

  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------
  /**
   * Creates the item tree nodes for this root item.
   * @param items The accessible items for the connected user.
   */
  fun createTree(items: Array<Item>) {
    this.root = createTree(items, rootItem)
  }

  /**
   * Creates the item tree for the given root item.
   * @param items The accessible items.
   * @param root The root item.
   * @return The local root tree node.
   */
  protected fun createTree(items: Array<Item>, root: Item): DefaultMutableTreeNode? {
    var self: DefaultMutableTreeNode? = null
    var childsCount = 0

    items.forEach {
      if (it.parent == root.id) {
        childsCount++
        it.level = root.level + 1
        val node: DefaultMutableTreeNode? = createTree(items, it)

        if (node != null) {
          if (self == null) {
            self = DefaultMutableTreeNode(root)
          }
          self!!.add(node)
        }
      }
    }
    return if (childsCount == 0) {
      DefaultMutableTreeNode(root)
    } else {
      self
    }
  }

  /**
   * Returns true if this root item does not contain any item.
   */
  val isEmpty: Boolean get() = this.root == null
}
