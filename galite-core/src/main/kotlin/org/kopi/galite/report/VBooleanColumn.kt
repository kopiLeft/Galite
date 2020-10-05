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

import org.kopi.galite.visual.VlibProperties

/**
 * Represents a report column description
 *
 * @param     ident           The column identifier
 * @param     options         The column options as bitmap
 * @param     align           The column alignment
 * @param     groups          The index of the column grouped by this one or -1
 * @param     function        An (optional) summation function
 */
class VBooleanColumn(ident: String?,
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
                     Math.max(VlibProperties.getString("true").length, VlibProperties.getString("false").length),
                     1,
                     format ?: VBooleanFormat()) {
  /**
   * Compare two objects.
   *
   * @param        o1        the first operand of the comparison
   * @param        o2        the second operand of the comparison
   * @return        -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  override fun compareTo(o1: Any, o2: Any): Int {
    return if (o1 == o2) 0 else if (true == o1) 1 else -1
  }

  private class VBooleanFormat : VCellFormat() {
    override fun format(value: Any?): String {
      return if (value == null) "" else if (true == value) trueRep else falseRep
    }
  }

  override fun formatColumn(exporter: PExport, index: Int) {
    exporter.formatBooleanColumn(this, index)
  }

  companion object {
    // --------------------------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------------------------
    private val trueRep: String = VlibProperties.getString("true")
    private val falseRep: String = VlibProperties.getString("false")
  }
}
