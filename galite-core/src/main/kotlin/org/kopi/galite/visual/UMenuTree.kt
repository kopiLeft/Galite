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

package org.kopi.galite.visual

import org.kopi.galite.base.UComponent

/**
 * `UMenuTree` is the top-level interface that represents the application
 * menu tree. The visible component is built from a [VMenuTree] model.
 */
interface UMenuTree : UWindow {
  /**
   * Returns The Tree instance
   *
   * @return a [UTree]
   */
  fun getTree(): UTree?

  /**
   * Returns The favorites panel
   *
   * @return a [UBookmarkPanel]
   */
  fun getBookmark(): UBookmarkPanel?

  /**
   * This method launches the selected form in the menu tree and throws
   * a VException
   * If the menu tree is launched as a super user, the form will not be launched
   * but its accessibility will change.
   *
   */
  fun launchSelectedForm()

  /**
   * Adds the selected module in the menu tree to favorites
   *
   */
  fun addSelectedElement()

  /**
   * Sets the menu tree menu. This allows enabling and disabling menu tree actors
   * according to the selected tree node.
   *
   */
  fun setMenu()

  /**
   * Removes the selected module in the menu tree from favorites
   *
   */
  fun removeSelectedElement()

  /**
   * Move the focus from the activated frame to favorites frame.
   */
  fun gotoShortcuts()

  /**
   * Shows the application information
   * @param message The message to be shown as application information
   */
  fun showApplicationInformation(message: String)

  /**
   * `UTree` is the top-level interface representing a visible tree
   * component.
   */
  interface UTree : UComponent {
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
     * Returns the first row of the currently selected rows.
     * @return an integer that identifies the first row of currently selected rows
     */
    val selectionRow: Int

    /**
     * Returns true if the node identified by the path is currently expanded,
     *
     * @param path  the path specifying the node to check
     * @return false if any of the nodes in the node's path are collapsed,
     * true if all nodes in the path are expanded
     */
    fun isExpanded(path: Any?): Boolean

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
  }

  /**
   * `UBookmarkPanel` is the top-level interface representing a bookmark
   * panel or window.
   */
  interface UBookmarkPanel : UComponent {
    /**
     * Makes the `UBookmarkPanel` visible. If the panel and/or its owner
     * are not yet displayable, both are made displayable.  The
     * `UBookmarkPanel` will be validated prior to being made visible.
     * @see UComponent.setVisible
     */
    fun show()

    /**
     * If this `UBookmarkPanel` is visible, brings this panel to the front and may make
     * it the focused Window.
     */
    fun toFront()
  }
}
