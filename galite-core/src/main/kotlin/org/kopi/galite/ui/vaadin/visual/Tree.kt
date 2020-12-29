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
package org.kopi.galite.ui.vaadin.visual

import javax.swing.tree.TreeNode

import org.kopi.galite.visual.Item
import org.kopi.galite.visual.UItemTree.UTreeComponent

/**
 * The vaadin implementation of an [UTreeComponent].
 */
class Tree(root: TreeNode,
           private val isNoEdit: Boolean,
           private val localised: Boolean)
  : UTreeComponent {
  override fun collapseRow(row: Int) {
    TODO("Not yet implemented")
  }

  override fun expandRow(row: Int) {
    TODO("Not yet implemented")
  }

  override fun expandTree() {
    TODO("Not yet implemented")
  }

  override fun getSelectionRow(): Int {
    TODO("Not yet implemented")
  }

  override fun isExpanded(path: Any): Boolean {
    TODO("Not yet implemented")
  }

  override fun isCollapsed(path: Any?): Boolean {
    TODO("Not yet implemented")
  }

  override fun getItems(): Array<Item> {
    TODO("Not yet implemented")
  }

  override fun getRootItem(): Item {
    TODO("Not yet implemented")
  }

  override fun isUnique(name: String): Boolean {
    TODO("Not yet implemented")
  }

  override fun isEnabled(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setEnabled(enabled: Boolean) {
    TODO("Not yet implemented")
  }

  override fun isVisible(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setVisible(visible: Boolean) {
    TODO("Not yet implemented")
  }
}
