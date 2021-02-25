/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: SingleComponentBlockLayout.java 35180 2017-07-14 16:31:39Z hacheni $
 */
package org.kopi.galite.ui.vaadin.block

import com.vaadin.flow.component.Component

/**
 * A block layout that contains a single component inside.
 */
class SingleComponentBlockLayout : SimpleBlockLayout(1, 1) {
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
    var x = x
    var y = y
    var width = width
    if (x > 0) {
      x = 0
    }
    if (y > 0) {
      y = 0
    }
    if (width > 1 || width < 0) {
      width = 1
    }
    super.addComponent(component, x, y, width, height, alignRight, useAll)
  }

  override fun setBlockAlignment(ori: Component, targets: IntArray, isChart: Boolean) {
    // not supported feature
  }
}
