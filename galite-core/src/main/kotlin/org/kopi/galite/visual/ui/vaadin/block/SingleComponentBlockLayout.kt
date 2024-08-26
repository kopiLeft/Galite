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
package org.kopi.galite.visual.ui.vaadin.block

import com.vaadin.flow.component.Component

/**
 * A block layout that contains a single component inside.
 *
 * @param block the black inside this layout.
 */
class SingleComponentBlockLayout(override val block: Block) : SimpleBlockLayout(1, 1, block) {

  init {
    className = "multiple"
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun addComponent(
          component: Component?,
          x: Int,
          y: Int,
          width: Int,
          height: Int,
          alignRight: Boolean,
          useAll: Boolean
  ) {
    // always put the component at the only available position
    var xPosition = x
    var yPosition = y
    var componentWidth = width
    if (xPosition > 0) {
      xPosition = 0
    }
    if (yPosition > 0) {
      yPosition = 0
    }
    if (componentWidth > 1 || componentWidth < 0) {
      componentWidth = 1
    }
    super.addComponent(component, xPosition, yPosition, componentWidth, height, alignRight, useAll)
  }

  override fun setBlockAlignment(original: Component, targetBlockName: String, targets: IntArray, isChart: Boolean) {
    // not supported feature
  }
}
