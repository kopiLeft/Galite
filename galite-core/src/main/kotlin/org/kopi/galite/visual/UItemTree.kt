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

import org.kopi.galite.base.UComponent

/**
 * `UItemTree` is the top-level interface that represents a tree.
 * The visible component is built from a [VTreeWindow] model.
 */
interface UItemTree : UWindow {
  /**
   * Returns The Tree instance
   * @return a [UTreeComponent]
   */
  fun getTree(): UTreeComponent

  /**
   * Change item selection state
   *
   */
  @Throws(VException::class)
  open fun setSelectedItem()

  /**
   * Set selected item as default element
   *
   */
  fun setDefaultItem()

  /**
   * Adds new item to the tree
   *
   */
  @Throws(VException::class)
  open fun addItem()

  /**
   * Sets the tree. This allows enabling and disabling item tree actors
   * according to the selected tree node.
   *
   */
  fun setTree()

  /**
   * Removes the selected item from the tree
   *
   */
  fun removeSelectedItem()

  /**
   * Edit the selected item
   *
   */
  fun editSelectedItem()

  /**
   * Localise the selected item
   *
   */
  fun localiseSelectedItem()

  /**
   * `UTreeComponent` is the top-level interface representing a visible tree
   * component.
   */
  interface UTreeComponent : UComponent {
    /**
     * Ensures that the node in the specified row is collapsed.
     *
     * @param row
     * an integer specifying a display row, where 0 is the first
     * row in the display
     */
    fun collapseRow(row: Int)

    /**
     * Ensures that the node in the specified row is expanded and
     * viewable.
     *
     * @param row  an integer specifying a display row, where 0 is the
     * first row in the display
     */
    fun expandRow(row: Int)

    /**
     * Ensures that the selected nodes are expanded and viewable.
     */
    fun expandTree()

    /**
     * Returns the first row of the currently selected rows.
     * @return an integer that identifies the first row of currently selected rows
     */
    fun getSelectionRow(): Int

    /**
     * Returns true if the node identified by the path is currently expanded,
     *
     * @param path  the path specifying the node to check
     * @return false if any of the nodes in the node's path are collapsed,
     * true if all nodes in the path are expanded
     */
    fun isExpanded(path: Any): Boolean

    /**
     * Returns true if the value identified by path is currently collapsed,
     * this will return false if any of the values in path are currently
     * not being displayed.
     *
     * @param path  the path to check
     * @return true if any of the nodes in the node's path are collapsed,
     * false if all nodes in the path are expanded
     */
    fun isCollapsed(path: Any?): Boolean

    /**
     * Returns the items of the tree as an array, returns null if there is no items
     * @return an array that contain the items of the tree
     */
    fun getItems(): Array<Item>

    /**
     * Returns the root item
     */
    fun getRootItem(): Item

    /**
     * Returns true if the item name done is unique in this tree
     */
    fun isUnique(name: String): Boolean
  }
}
