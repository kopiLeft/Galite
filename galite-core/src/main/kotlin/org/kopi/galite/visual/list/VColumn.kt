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

import java.io.Serializable

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/**
 * Represents a column
 *
 * @param pos       position of associated table
 * @param name      column name
 * @param key       whether the column is a key
 * @param nullable  true if column is nullable
 */
class VColumn(val pos: Int,
              val name: String,
              val key: Boolean,
              val nullable: Boolean,
              val column: Column<*>) : Serializable {

  /**
   * Returns the position of the table in the array of tables
   * of the field's block
   */
  @Deprecated("use getTable()")
  fun _getTable(): Int = pos

  /**
   * Returns the table in the array of tables of the field's block
   */
  fun getTable(): Table {
    return column.table
  }

  /**
   * Returns the qualified name of the column (i.e. with correlation)
   */
  fun getQualifiedName(): String = "T$pos.$name"
}
