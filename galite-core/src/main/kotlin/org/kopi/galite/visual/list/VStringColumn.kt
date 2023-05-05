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

import org.jetbrains.exposed.sql.ExpressionWithColumnType

import org.kopi.galite.visual.domain.TableInitializer

class VStringColumn(title: String,
                    column: ExpressionWithColumnType<*>?,
                    table: TableInitializer?,
                    align: Int,
                    width: Int,
                    sortAscending: Boolean) :
        VListColumn(title,
                    column,
                    table,
                    align,
                    width,
                    sortAscending) {
  /**
   * Returns a string representation of value
   */
  override fun formatObject(value: Any?): Any {
    if (value == null) {
      return VConstants.EMPTY_TEXT
    }
    var str = value.toString()
    val width: Int = width

    if (str.length > width) {
      str = str.substring(0, width) + "..." + str.substring(width)
    }

    return str
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------
  override fun getDataType(): KClass<*> = String::class
}
