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

import java.time.Instant

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Column
import org.kopi.galite.type.format
import org.kopi.galite.visual.domain.TableInitializer

/**
 * Represents a list column.
 */
class VTimestampColumn(title: String,
                       column: Column<*>?,
                       table: TableInitializer?,
                       sortAscending: Boolean)
          : VListColumn(title,
                        column,
                        table,
                        VConstants.ALG_LEFT,
                        5,
                        sortAscending) {

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  /**
   * Returns a representation of value
   */
  override fun formatObject(value: Any?): Any {
    val stringValue = (value as? Instant)?.format() ?: VConstants.EMPTY_TEXT

    // this is work around to display the timestamp in yyyy-MM-dd hh:mm:ss format
    // The proper way is to change the method Timestamp#toString(Locale) but this
    // will affect the SQL representation of the timestamp value.
    return stringValue.substring(0, 19.coerceAtMost(stringValue.length))
  }

  override fun getDataType(): KClass<*> = Instant::class
}
