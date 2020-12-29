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

import org.kopi.galite.visual.UItemTree
import org.kopi.galite.visual.VItemTree

/**
 * The `DItemTree` is the vaadin implementation of the
 * [UItemTree].
 *
 *
 * The implementation is based on [DWindow]
 *
 * TODO Externalize favorites handling.
 */
class DItemTree(model: VItemTree) : DWindow(model), UItemTree {
  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------
  /**
   * Sets item selection state.
   */
  override fun setSelectedItem() {
    TODO()
  }

  override fun setDefaultItem() {
    TODO()
  }

  override fun addItem() {
    TODO()
  }

  override fun removeSelectedItem() {
    TODO()
  }

  override fun localiseSelectedItem() {
    TODO("Not yet implemented")
  }

  override fun editSelectedItem() {
    TODO("Not yet implemented")
  }

  override fun setTree() {
    TODO("Not yet implemented")
  }

  override fun run() {
    TODO("Not yet implemented")
  }

  override fun getModel(): VItemTree {
    return super.getModel() as VItemTree
  }

  override fun getTree(): UItemTree.UTreeComponent {
    TODO("Not yet implemented")
  }
}
