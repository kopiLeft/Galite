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
import org.kopi.galite.form.VFixnumField
import org.kopi.galite.type.NotNullFixed

/**
 * Represents a report column description
 *
 * @param     ident           The column identifier
 * @param     options         The column options as bitmap
 * @param     align           The column alignment
 * @param     groups          The index of the column grouped by this one or -1
 * @param     function        An (optional) summation function
 * @param     maxScale        The maximum of scale
 * @param     format          The format of the cells
 */
class VFixnumColumn(ident: String?,
                    options: Int,
                    align: Int,
                    groups: Int,
                    function: VCalculateColumn?,
                    digits: Int,
                    maxScale: Int,
                    format: VCellFormat?)
    : VReportColumn(ident,
                    options,
                    align,
                    groups,
                    function,
                    VFixnumField.computeWidth(digits, maxScale, null, null),
                    1,
                    format ?: VFixedFormat(maxScale, true)) {
  /**
   * Compare two objects.
   *
   * @param    object1    the first operand of the comparison
   * @param    object2    the second operand of the comparison
   * @return              -1 if the first operand is smaller than the second
   *           1 if the second operand if smaller than the first
   *           0 if the two operands are equal
   */
  override fun compareTo(object1: Any, object2: Any): Int =
          (object1 as NotNullFixed).compareTo(object2 as NotNullFixed)

  /**
   * Returns the width of cells in this column in characters
   */
  override fun getPrintedWidth(): Double = width * 0.7

  /**
   * sets all column values to the same scale
   */
  private class VFixedFormat(private val maxScale: Int, private val exactScale: Boolean) : VCellFormat() {

    override fun format(value: Any?): String {
      return if (value == null) {
        ""
      } else (value as? Int)?.toString()
              ?: if (value is NotNullFixed) {
                if ((value).scale > maxScale || exactScale) (value).setScale(maxScale).toString() else value.toString()
              } else {
                throw InconsistencyException("bad type for $value")
              }
    }
  }

  override fun formatColumn(exporter: PExport, index: Int) {
    exporter.formatFixedColumn(this, index)
  }

  /**
   * Sets display scale to maxScale
   * all values will be set to the same scale
   */
  fun setDisplayScale(scale: Int) {
    format = VFixedFormat(scale, true)
    maxScale = scale
  }

  /**
   * Sets maxScale
   * all values with scale superior than maxScale will have
   * maxScale as scale, and the other values will keep their scale.
   */
  fun setMaxScale(scale: Int) {
    format = VFixedFormat(scale, false)
    maxScale = scale
  }

  var formula = null
  var maxScale = 0
    private set
}
