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

import java.io.Serializable

import org.jetbrains.exposed.sql.Column

/**
 * Represents a column
 *
 * @param pos position of associated table
 * @param name column name
 * @param key column key of table
 * @param nullable true if column is nullable
 */
class VColumn(val pos: Int, val name: String, val key: Boolean, val nullable: Boolean, val column: Column<Any>? = null) : Serializable {

  /**
   * Returns the position of the table in the array of tables
   * of the field's block
   */
  fun getTable(): Int = pos

  /**
   * Returns the qualified name of the column (i.e. with correlation)
   */
  fun getQualifiedName(): String = "T$pos.$name"

  /**
   * Returns wether ther column is a key
   */
  fun isNullable(): Boolean {
   TODO()
  }
}
