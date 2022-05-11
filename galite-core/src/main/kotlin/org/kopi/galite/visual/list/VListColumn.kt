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

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.QueryAlias
import org.jetbrains.exposed.sql.Table
import org.kopi.galite.visual.domain.TableInitializer
import org.kopi.galite.visual.l10n.ListLocalizer

abstract class VListColumn(
  var title: String,
  private val internalColumn: Column<*>?,
  private val table: TableInitializer?,
  private val align: Int,
  val width: Int,
  val isSortAscending: Boolean,
) : VConstants, ObjectFormatter {

  /**
   * Returns the column alignment
   */
  override fun getAlign(): Int {
    return align
  }

  /**
   * Returns a representation of value
   */
  override fun formatObject(value: Any?): Any? {
    return value?.toString() ?: VConstants.EMPTY_TEXT
  }

  /**
   * Returns the data type provided by this list.
   * @return The data type provided by this list.
   */
  abstract fun getDataType(): KClass<*>

  val column: Column<*>? get() = internalColumn?.let { table?.let { tableInit -> tableInit().resolveColumn(it) } } ?: internalColumn

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localize this object.
   *
   * @param     loc
   */
  fun localize(loc: ListLocalizer) {
    title = loc.getColumnTitle(column!!.name)
  }
}

/**
 * Finds and returns the column in this [ColumnSet] corresponding to the [column] from the original table.
 *
 * @param column The column in the original table.
 */
fun ColumnSet.resolveColumn(column: Column<*>): Column<*> {
  return when (this) {
    is Table -> {
      column
    }
    is QueryAlias -> {
      get(column)
    }
    is Alias<*> -> {
      get(column)
    }
    else -> {
      columns.single { it.name == column.name }
    }
  }
}
