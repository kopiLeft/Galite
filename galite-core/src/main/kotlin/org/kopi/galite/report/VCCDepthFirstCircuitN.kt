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

package org.kopi.galite.report

abstract class VCCDepthFirstCircuitN : VCalculateColumn() {
  /**
   * Initialisation of the calculation
   */
  override fun init() {}

  /**
   * Evaluates nodes
   */
  override fun evalNode(row: VReportRow, column: Int): Any? = null

  /**
   * Evaluates leafs
   */
  open fun evalLeaf(row: VReportRow, column: Int): Any? = null

  /**
   * Add calculated data into the report row
   */
  override fun calculate(tree: VGroupRow, column: Int) {
    if (tree.level > 1) {
      val childCount: Int = tree.childCount

      for (i in 0 until childCount) {
        calculate(tree.getChildAt(i) as VGroupRow, column)
      }
    } else {
      val childCount: Int = tree.childCount

      for (i in 0 until childCount) {
        val evaluatedObject = evalLeaf(tree.getChildAt(i) as VBaseRow, column)

        if (evaluatedObject != null) {
          (tree.getChildAt(i) as VBaseRow).setValueAt(column, evaluatedObject)
        }
      }
    }
    tree.setValueAt(column, evalNode(tree, column))
  }
}
