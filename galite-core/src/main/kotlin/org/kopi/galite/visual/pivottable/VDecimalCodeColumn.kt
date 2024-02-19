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

import java.math.BigDecimal

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.dsl.pivottable.Dimension

class VDecimalCodeColumn(
  ident: String?,
  position: Dimension.Position?,
  type: String?,
  source: String?,
  name: Array<String>,
  private val codes: Array<BigDecimal?>
) : VCodeColumn(ident, position, type, source, name) {

  /**
   * Compares two objects.
   *
   * @param    object1    the first operand of the comparison
   * @param    object2    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  override fun compareTo(object1: Any, object2: Any): Int = (object1 as BigDecimal).compareTo(object2 as BigDecimal)

  /**
   * Get the index of the value.
   */
  override fun getIndex(value: Any?): Int {
    for (i in codes.indices) {
      if (value == codes[i]) {
        return i
      }
    }
    throw InconsistencyException(">>>>$value")
  }
}
