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
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.domain.TableInitializer
import kotlin.reflect.KClass

class VTextColumn(title: String, column: Column<*>?, table: TableInitializer?, align: Int, sortAscending: Boolean)
  : VListColumn(title, column, table, align, Message.getMessage("text-type").length, sortAscending) {
  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------

  /**
   * Returns a string representation of value
   */
  override fun formatObject(value: Any?): Any {
    return if (value == null) VConstants.EMPTY_TEXT else Message.getMessage("text-type")
  }

  override fun getDataType(): KClass<*> {
    return String::class
  }
}
