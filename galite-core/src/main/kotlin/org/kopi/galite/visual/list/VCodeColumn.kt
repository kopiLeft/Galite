/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.list

import org.jetbrains.exposed.sql.Column
import org.kopi.galite.visual.domain.TableInitializer

abstract class VCodeColumn(
  title: String,
  column: Column<*>?,
  table: TableInitializer?,
  protected val names: Array<String>,
  sortAscending: Boolean
) : VListColumn(title, column, table, VConstants.ALG_LEFT, getMaxWidth(names), sortAscending) {
  /**
   * Returns a string representation of value
   */
  override fun formatObject(value: Any?): Any = when (value) {
    null -> VConstants.EMPTY_TEXT
    else -> names[getObjectIndex(value)]
  }

  /**
   * Returns the indexOf given object
   */
  protected abstract fun getObjectIndex(value: Any): Int

  companion object {
    private fun getMaxWidth(names: Array<String>): Int = names.maxByOrNull { it.length }?.length ?: 0
  }
}
