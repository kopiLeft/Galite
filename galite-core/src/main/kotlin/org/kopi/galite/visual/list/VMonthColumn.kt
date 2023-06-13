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

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.kopi.galite.type.Month
import org.kopi.galite.visual.domain.TableInitializer

/**
 * Represents a list column.
 */
class VMonthColumn(title: String,
                   column: ExpressionWithColumnType<*>?,
                   table: TableInitializer?,
                   sortAscending:
                   Boolean)
     : VListColumn(title,
                   column,
                   table,
                   VConstants.ALG_LEFT,
                   7,
                   sortAscending) {
  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------
  override fun getDataType(): KClass<*> = Month::class
}
