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

import org.kopi.galite.l10n.ReportLocalizer

abstract class VReportColumn {
  /**
   * Compare two objects
   *
   * @param        object1        the first operand of the comparison
   * @param        object2        the second operand of the comparison
   * @return        -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  abstract fun compareTo(object1: Any?, object2: Any?): Int
  abstract fun isVisible(): Boolean
  abstract fun isFolded(): Any
  abstract fun formatColumn(pExport: PExport, index: Int)
  abstract fun getLabel(): String
  abstract fun format(valueAt: Any?): String?
  abstract fun formatWithLineBreaker(valueAt: Any?): String?
  abstract fun getAlign(): Int
  fun localize(parent: ReportLocalizer): Void = TODO()
  fun getIdent(): String = TODO()
  open fun isAddedAtRuntime(): Boolean = TODO()
}
