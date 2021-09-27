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
package org.kopi.galite.visual.ui.vaadin.block

import com.vaadin.flow.component.Component

/**
 * The chart block layout.
 *
 * @param col The number of columns.
 * @param line The number of lines.
 */
class ChartBlockLayout(col: Int, line: Int) : AbstractBlockLayout(col, line), BlockLayout {

  /**
   * Can the block scroll ? If yes, draw a scroll bar
   */
  var hasScroll = false

  override fun initSize() {
    TODO("Not yet implemented")
  }

  override fun add(component: Component?, constraints: ComponentConstraint) {
    if (aligns != null && components != null) {
      aligns!![constraints.x][constraints.y] = constraints
      components!![constraints.x][constraints.y] = component
    }
  }

  override fun addComponent(
          component: Component?, x: Int, y: Int, width: Int, height: Int, alignRight: Boolean,
          useAll: Boolean,
  ) {
    TODO("Not yet implemented")
  }

  override fun layout() {
    TODO("Not yet implemented")
  }

  override fun clear() {
    TODO("Not yet implemented")
  }

  override fun updateScroll(pageSize: Int, maxValue: Int, enable: Boolean, value: Int) {
    TODO("Not yet implemented")
  }
}
