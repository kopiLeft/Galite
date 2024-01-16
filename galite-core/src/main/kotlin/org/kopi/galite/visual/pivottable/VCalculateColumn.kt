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

package org.kopi.galite.visual.pivottable

abstract class VCalculateColumn {
  /**
   * Initialisation of the calculation
   */
  fun init() {}

  /**
   * Evaluates nodes
   */
  open fun evalNode(row: VPivotTableRow, column: Int): Any? = null

  /**
   * Evaluates leafs
   */
  open fun evalLeaf(row: VPivotTableRow, column: Int): Any? = null

  fun calculate(tree: VPivotTableRow, column: Int) {
    if (tree.level > 1) {
      val childCount: Int = tree.childCount

      for (i in 0 until childCount) {
        calculate(tree.getChildAt(i) as VPivotTableRow, column)
      }
    } else {
      val childCount: Int = tree.childCount

      for (i in 0 until childCount) {
        val evaluatedObject = evalLeaf(tree.getChildAt(i) as VPivotTableRow, column)

        if (evaluatedObject != null) {
          (tree.getChildAt(i) as VPivotTableRow).setValueAt(column, evaluatedObject)
        }
      }
    }
    tree.setValueAt(column, evalNode(tree, column))
  }
}
