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

import org.kopi.galite.util.base.InconsistencyException

import kotlin.reflect.KClass

open class VStringCodeColumn(
  title: String,
  column: String,
  names: Array<String>,
  private val codes: Array<String>,
  sortAscending: Boolean
) : VCodeColumn(title, column, names, sortAscending) {
  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------
  /**
   * Returns the indexOf given object
   */
  public override fun getObjectIndex(value: Any): Int = codes.indexOfFirst { it == value }
          .takeUnless { it == -1 } ?: throw InconsistencyException("bad code value $value")

  override fun getDataType(): KClass<*> {
    return String::class
  }
}
