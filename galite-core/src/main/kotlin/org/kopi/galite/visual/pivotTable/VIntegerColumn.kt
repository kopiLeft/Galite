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

/**
 * Represents a report column description
 *
 * @param     ident           The column ident
 * @param     options         The column options as bitmap
 * @param     align           The column alignment
 * @param     groups          The index of the column grouped by this one or -1
 * @param     function        An (optional) summation function
 */
class VIntegerColumn(ident: String?,
                     options: Int,
                     align: Int,
                     groups: Int,
                     function: VCalculateColumn?,
                     width: Int,
                     format: VCellFormat?)
     : VReportColumn(ident,
                     options,
                     align,
                     groups,
                     function,
                     width,
                     1,
                     format ?: VIntegerFormat()) {

  /**
   * Compare two objects.
   *
   * @param    object1    the first operand of the comparison
   * @param    object2    the second operand of the comparison
   * @return              -1 if the first operand is smaller than the second
   *           1 if the second operand if smaller than the first
   *           0 if the two operands are equal
   */
  override fun compareTo(object1: Any, object2: Any): Int {
    val v1 = (object1 as Int)
    val v2 = (object2 as Int)

    return if (v1 < v2) -1 else if (v1 > v2) 1 else 0
  }

  /**
   * Returns the width of cells in this column in characters
   */
  override fun getPrintedWidth(): Double = width * 0.7

  private class VIntegerFormat : VCellFormat() {
    override fun format(value: Any?): String {
      // don't do substring when value.length() > columnWidth
      return value?.toString() ?: ""
    }
  }
}
