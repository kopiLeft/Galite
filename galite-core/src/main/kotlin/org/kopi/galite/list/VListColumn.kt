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

package org.kopi.galite.list

import kotlin.reflect.KClass

import org.kopi.galite.l10n.ListLocalizer

abstract class VListColumn(
  var title: String?,
  val column: String?,
  private val align: Int,
  val width: Int,
  val isSortAscending: Boolean
) : VConstants, ObjectFormatter {

  /**
   * Returns the column alignment
   */
  override fun getAlign(): Int {
    return align;
  }

  /**
   * Returns a representation of value
   */
  override fun formatObject(value: Any?): Any {
    return value?.toString() ?: VConstants.EMPTY_TEXT
  }

  /**
   * Returns the data type provided by this list.
   * @return The data type provided by this list.
   */
  abstract fun getDataType(): KClass<*>

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localize this object.
   *
   * @param     loc
   */
  fun localize(loc: ListLocalizer) {
    title = loc.getColumnTitle(column!!)
  }
}
