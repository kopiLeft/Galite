/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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

import org.kopi.galite.exceptions.InconsistencyException

class VFixnumColumn : VReportColumn {

  var formula: String? = null
  var maxScale = 0

  constructor(ident: String?,
              options: Int,
              align: Int,
              groups: Int,
              function: VCalculateColumn,
              digits: Int,
              maxScale: Int,
              format: VCellFormat?) : super(ident,
          options,
          align,
          groups,
          function,
          VFixnumField.computeWidth(digits, maxScale, null, null),
          1,
          format ?: VFixedFormat(maxScale, true) as VCellFormat)
}

/**
 * Compare two objects.
 *
 * @param        o1        the first operand of the comparison
 * @param        o2        the second operand of the comparison
 * @return        -1 if the first operand is smaller than the second
 * 1 if the second operand if smaller than the first
 * 0 if the two operands are equal
 */
fun compareTo(o1: Any, o2: Any?): Int {
  return (o1 as NotNullFixed).compareTo(o2 as NotNullFixed?)
}

/**
 * Returns the width of cells in this column in characters
 */
fun getPrintedWidth(): Double {
  return getWidth() * 0.7
}

private class VFixedFormat     /*
     * sets all column values to the same scale
     */(private val maxScale: Int, private val exactScale: Boolean) : VCellFormat() {
  override fun format(value: Any?): String {
    return if (value == null) {
      ""
    } else (value as? Int)?.toString()
            ?: if (value is NotNullFixed) {
              if ((value as NotNullFixed).getScale() > maxScale || exactScale) (value as NotNullFixed).setScale(maxScale).toString() else value.toString()
            } else {
              throw InconsistencyException("bad type for $value")
            }
  }
}

fun formatColumn(exporter: PExport, index: Int) {
  exporter.formatFixedColumn(this, index)
}

/*
 * Sets display scale to maxScale
 * all values will be set to the same scale
 */
fun setDisplayScale(scale: Int) {
  setFormat(VFixedFormat(scale, true))
  maxScale = scale
}

/*
 * Sets maxScale
 * all values with scale superior than maxScale will have
 * maxScale as scale, and the other values will keep their scale.
 */
fun setMaxScale(scale: Int) {
  setFormat(VFixedFormat(scale, false))
  maxScale = scale
}
}
