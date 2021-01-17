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

package org.kopi.galite.report

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.type.Decimal

class VFixnumCodeColumn(ident: String?,
                        type: String?,
                        source: String?,
                        options: Int,
                        align: Int,
                        groups: Int,
                        function: VCalculateColumn?,
                        width: Int,
                        format: VCellFormat?,
                        names: Array<String>,
                        // array of internal representations
                        private val codes: Array<Decimal>)
          : VCodeColumn(ident,
                        type,
                        source,
                        options,
                        align,
                        groups,
                        function,
                        width,
                        format,
                        names) {
  /**
   * Get the index of the value.
   */
  override fun getIndex(value: Any): Int {
    for (i in codes.indices) {
      if (value == codes[i]) {
        return i
      }
    }
    throw InconsistencyException(">>>>$value")
  }

  /**
   * Returns the width of cells in this column in characters
   */
  override fun getPrintedWidth(): Double = width * 0.7

  override fun formatColumn(exporter: PExport, index: Int) {
    exporter.formatFixedColumn(this, index)
  }

  /**
   * Compares two objects.
   *
   * @param    object1    the first operand of the comparison
   * @param    object2    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  override fun compareTo(object1: Any, object2: Any): Int = (object1 as Decimal)
          .compareTo(object2 as Decimal)

}
