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

/**
 * Constructs a report column description
 *
 * @param     ident           The column ident
 * @param     options         The column options as bitmap
 * @param     align           The column alignment
 * @param     groups          The index of the column grouped by this one or -1
 * @param     function        An (optional) summation function
 */
class VStringCodeColumn(ident: String,
                        type: String,
                        source: String,
                        options: Int,
                        align: Int,
                        groups: Int,
                        function: VCalculateColumn,
                        width: Int,
                        format: VCellFormat,
                        names: Array<String>,
                        private val codes: Array<String>)
          : VCodeColumn(ident, type, source, options, align, groups, function, width, format, names) {

  /**
   * Get the index of the value.
   */
  override fun getIndex(value: Any): Int {
    codes.forEachIndexed { index, code ->
      if (value == code) {
        return index
      }
    }
    throw InconsistencyException(">>>>$value")
  }

  /**
   * Returns the width of cells in this column in characters
   */
  override fun getPrintedWidth(): Double = width * 0.7

  override fun formatColumn(exporter: PExport, index: Int) {
    exporter.formatStringColumn(this, index)
  }

  /**
   * Compares two objects.
   *
   * @param        o1        the first operand of the comparison
   * @param        o2        the second operand of the comparison
   * @return        -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  override fun compareTo(o1: Any, o2: Any): Int {
    return (o1 as String).compareTo((o2 as String))
  }
}
