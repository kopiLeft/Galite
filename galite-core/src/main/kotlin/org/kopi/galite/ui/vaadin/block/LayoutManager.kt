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

/**
 * A simple block layout manager. This class aims to correct component positions in case of
 * column and row span.
 */
class LayoutManager(private var layout: AbstractBlockLayout?) {

  private var handler: ConstraintsHandler? = ConstraintsHandler()

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the component in the given layout cell.
   * @param component The component to be set.
   * @param row The cell row.
   * @param column The Cell column.
   * @param colSpan The column span width
   * @param rowSpan The row span width.
   */
  fun setComponent(component: Component?, c: ComponentConstraint, colSpan: Int, rowSpan: Int) {
    val constraint = ConstraintWrapper(c, colSpan, rowSpan)
    constraint.component = component
    handler!!.add(constraint)
  }

  /**
   * Layout the container.
   */
  protected fun layout() {
    handler!!.handleColSpan()
    handler!!.layout(layout)
  }

  /**
   * Releases the content of this layout manager
   */
  fun release() {
    handler!!.release()
    handler = null
    layout = null
  }
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  /**
   * A component constraint wrapper
   */
  class ConstraintWrapper(val constraint: ComponentConstraint,
                          colSpan: Int,
                          rowSpan: Int) {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun equals(obj: Any?): Boolean {
      if (obj is ConstraintWrapper) {
        val w = obj
        return column == w.column && row == w.row && colSpan == w.colSpan && rowSpan == w.rowSpan
      }
      return false
    }

    override fun hashCode(): Int {
      return column + row + colSpan + rowSpan
    }

    var column = constraint.x
    var row = constraint.y
    var colSpan = colSpan
    var rowSpan = rowSpan
    var component: Component? = null
  }

  /**
   * A component constraints wrapper
   */
  class ConstraintsHandler {
    private var constraints: MutableList<ConstraintWrapper>? = mutableListOf()

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    /**
     * Adds a constraint to the buffer.
     * @param constraint The constraint to be buffered.
     */
    fun add(constraint: ConstraintWrapper) {
      constraints!!.add(constraint)
    }

    /**
     * Handles the column span problems
     */
    fun handleColSpan() {
      for (c in constraints!!) {
        // look for constraints that have column span
        if (c.colSpan > 1) {
          // correct the position of all components beside the spanned component.
          // aligns[x][y].x - (colSpan == 0 ? 1 : colSpan) + 1;
          val constraints = getBesideConstraints(c.row, c.column)
          for (constraint in constraints) {
            constraint.column = constraint.column - c.colSpan + 1
            constraint.constraint.x = constraint.column
          }
        }
      }
    }

    /**
     * Returns the constraints beside the given row and column.
     * @param row The constraint row.
     * @param col The starting column.
     * @return The list of the constraint beside the starting column.
     */
    protected fun getBesideConstraints(row: Int, col: Int): List<ConstraintWrapper> {
      val constraints: MutableList<ConstraintWrapper>
      constraints = ArrayList()
      for (c in this.constraints!!) {
        if (c.row == row && c.column > col) {
          constraints.add(c)
        }
      }
      return constraints
    }

    /**
     * Returns the constraints bottom the given row and column.
     * @param row The constraint row.
     * @param col The starting column.
     * @return The list of the constraint beside the starting column.
     */
    protected fun getBottomConstraints(row: Int, col: Int): List<ConstraintWrapper> {
      val constraints: MutableList<ConstraintWrapper>
      constraints = ArrayList()
      for (c in this.constraints!!) {
        if (c.row > row && c.row <= row + c.rowSpan && c.column > col) {
          constraints.add(c)
        }
      }
      return constraints
    }

    /**
     * Layouts the container.
     * @param container The container to be filled.
     */
    fun layout(container: AbstractBlockLayout?) {
      for (c in constraints!!) {
        container!!.setComponent(c.component, c.column, c.row, c.colSpan, c.rowSpan)
      }
    }

    /**
     * Releases the content of this constraint handler
     */
    fun release() {
      constraints!!.clear()
      constraints = null
    }
  }
}
