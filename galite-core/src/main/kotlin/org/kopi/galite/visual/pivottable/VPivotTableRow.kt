/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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

import javax.swing.tree.DefaultMutableTreeNode

class VPivotTableRow(val data: Array<Any?>) : DefaultMutableTreeNode() {

  /**
   * Sets data row
   *
   * @param        column                the index of the column
   * @param        value                the value for the column
   */
  fun setValueAt(column: Int, value: Any?) {
    data[column] = value
  }

  /**
   * Return the object at column
   *
   * @param        column                the index of the column
   * @return        the object to be displayed
   */
  fun getValueAt(column: Int): Any? = data[column]

  companion object {
    private const val serialVersionUID = 0L
  }
}
