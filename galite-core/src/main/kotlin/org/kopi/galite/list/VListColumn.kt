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

import org.kopi.galite.l10n.ListLocalizer

open abstract class VListColumn(private var title: String, private val column: String, private val align: Int, private val width: Int,
                                private val isSortAscending: Boolean) : VConstants, ObjectFormatter {

  /**
   * Returns the column title
   */
  open fun getTitle(): String {
    return title
  }

  /**
   * Returns the column's database column name
   */
  open fun getColumn(): String {
    return column
  }

  /**
   * Returns the column alignment
   */
  override fun getAlign(): Int {
    return align;
  }

  /**
   * Returns the column width in characters
   */
  open fun getWidth(): Int {
    return width
  }

  /**
   * Returns the column width in characters
   */
  open fun isSortAscending(): Boolean {
    return isSortAscending
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
  abstract fun getDataType(): Class<*>
  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localize this object.
   *
   * @param     loc
   */
  fun localize(loc: ListLocalizer) {
    title = loc.getColumnTitle(column)
  }

}
