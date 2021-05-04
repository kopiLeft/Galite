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

import kotlin.math.max

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.common.VTable

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component

/**
 * An abstract implementation for the block layout.
 *
 * @param col The number of columns.
 * @param line The number of lines.
 */
abstract class AbstractBlockLayout protected constructor(val col: Int,
                                                         val line: Int)
  : VTable(line, max(1, col / 2)), BlockLayout {

  /**
   * The number of columns
   */
  var columns = 1

  /**
   * The number of rows.
   */
  var rows = 1

  /**
   * The children constrains
   */
  var constrains: MutableMap<Component?, ComponentConstraint?> = mutableMapOf()

  protected var components: Array<Array<Component?>>? = null
  protected var aligns: Array<Array<ComponentConstraint?>>? = null

  /**
   * Creates a new AbstractBlockLayout instance.
   * This is a special hidden for use in multiple block layout.
   */
  constructor() : this(1, 1)

  init {
    className = Styles.BLOCK_LAYOUT
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  override fun onAttach(attachEvent: AttachEvent?) {
    layout() // FIXME!!
  }

  /**
   * Initialize the size of the layout
   */
  protected abstract fun initSize()

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
   * Sets the component in the given layout cell.
   * @param formItem The component to be set.
   * @param row The cell row.
   * @param column The Cell column.
   * @param colSpan The column span width
   * @param rowSpan The row span width.
   */
  open fun setComponent(formItem: Component, column: Int, row: Int, colSpan: Int, rowSpan: Int) {
    add(row, column, formItem.element)
    if (colSpan > 1) {
      setColSpan(row, column, colSpan.toString())
    }
    if (rowSpan > 1) {
      setRowSpan(row, column, rowSpan.toString())
    }
  }

  open fun addAlignedComponent(widget: Component?, constraint: ComponentConstraint?) {
    TODO()
  }

  override fun layoutAlignedComponents() {
    TODO("Not yet implemented")
  }
}
