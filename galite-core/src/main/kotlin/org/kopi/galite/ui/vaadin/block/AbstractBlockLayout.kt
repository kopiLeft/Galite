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
package org.kopi.galite.ui.vaadin.block

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.formlayout.FormLayout

/**
 * An abstract implementation for the block layout.
 *
 * @param col The number of columns.
 * @param line The number of lines.
 */
abstract class AbstractBlockLayout protected constructor(val col: Int, val line: Int) : FormLayout(), BlockLayout {

  protected var components: Array<Array<Component?>>? = null
  protected var aligns: Array<Array<ComponentConstraint?>>? = null

  /**
   * Creates a new AbstractBlockLayout instance.
   * This is a special hidden for use in multiple block layout.
   */
  constructor() : this(1, 1)

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Initialize layout size
   * @param columns The number of column.
   * @param rows The number of lines.
   */
  open fun initSize(columns: Int, rows: Int) {
    components = Array(columns) { arrayOfNulls(rows) }
    aligns = Array(columns) { arrayOfNulls(rows) }
  }

  /**
   * Sets the widget in the given layout cell.
   * @param widget The widget to be set.
   * @param row The cell row.
   * @param column The Cell column.
   * @param colSpan The column span width
   * @param rowSpan The row span width.
   */
  open fun setComponent(widget: Component?, column: Int, row: Int, colSpan: Int, rowSpan: Int) {
    // TODO
  }

  open fun addAlignedComponent(widget: Component?, constraint: ComponentConstraint?) {
    TODO()
  }

  override fun layoutAlignedComponents() {
    TODO("Not yet implemented")
  }
}