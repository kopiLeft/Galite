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

package org.kopi.galite.visual.pivotTable

import java.io.Serializable

import org.kopi.galite.visual.MessageCode

class MPivotTable : Serializable {

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  // Columns contains all columns defined by the user
  var columns = mutableListOf<VPivotTableColumn?>()    // array of column definitions


  // Baserows contains data give by the request of the user
  internal var userRows: ArrayList<VPivotTableRow>? = ArrayList(500)
  /**
   * Add a row to the list of rows defined by the user
   */
  fun addLine(line: Array<Any?>) {
    userRows!!.add(VPivotTableRow(line))
  }

  /**
   * Build the base row + initialisation
   */
  internal fun build() {
    // build accessible columns
    columns.sortBy { it?.ident }
    if (userRows!!.size == 0) {
      throw VNoRowException(MessageCode.getMessage("VIS-00015"))
    }
  }
}