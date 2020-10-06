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

import org.kopi.galite.form.VFixnumField

//TODO
class VFixnumColumn(
        ident: String?,
        options: Int,
        align: Int,
        groups: Int,
        function: VCalculateColumn?,
        digits: Int,
        private var maxScale: Int,
        format: VCellFormat?,
) : VReportColumn(
        ident,
        options,
        align,
        groups,
        function,
        VFixnumField.computeWidth(digits, maxScale, null, null),
        1,
        format ?: VFixedFormat(maxScale, true)
  ) {

  private class VFixedFormat(private val maxScale: Int, private val exactScale: Boolean) : VCellFormat() {
    override fun format(value: Any?): String = TODO()
  }

  override fun compareTo(object1: Any, object2: Any): Int = TODO()

  fun getMaxScale(): Int = TODO()
}
