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

abstract class VReportColumn {

  val ident: String? = null
  var label: String? = null
  val help: String? = null
  val options = 0
  val align = 0

  /** reference to grouped column or -1*/
  val groups = 0

  /**sum function or -1*/
  val function: VCalculateColumn? = null
  val format: VCellFormat? = null
  val visible = false
  val folded = false
  var addedAtRuntime = false
  var userDefinedLabel = false
  lateinit var styles: Array<ColumnStyle>
  protected var width = 0
  protected var height = 0

  /**
   * Compare two objects of type Any
   *
   * @param        object1        the first operand of the comparison
   * @param        object2        the second operand of the comparison
   * @return        -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  abstract fun compareTo(object1: Any?, object2: Any?): Int

  /**
   *Returns True if column is hidden
   */
  fun isHidden(): Boolean {
    return (options and Constants.CLO_HIDDEN) > 0
  }
}
