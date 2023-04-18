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

import java.math.BigDecimal

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Column
import org.kopi.galite.type.format
import org.kopi.galite.type.ushr
import org.kopi.galite.visual.domain.TableInitializer

/**
 * Represents a list column.
 */
class VDecimalColumn(title: String,
                     column: Column<*>?,
                     table: TableInitializer?,
                     align: Int,
                     width: Int,
                     scale: Int,
                     sortAscending: Boolean): VListColumn(title,
                                                           column,
                                                           table,
                                                           align,
                                                           width,
                                                           sortAscending) {

  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------

  private var scale = scale
  override fun getDataType(): KClass<*> = BigDecimal::class

  /**
   * Returns a string representation of value
   */
  override fun formatObject(value: Any?): Any = (value as? BigDecimal)?.ushr(scale)?.format() ?: VConstants.EMPTY_TEXT
}
