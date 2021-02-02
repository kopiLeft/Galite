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

package org.kopi.galite.type

import java.util.Locale

/**
 * This class is the super-class for types
 */
abstract class Type<U, V> : Comparable<U> {
  /**
   * Compares two objects
   */
  abstract override fun equals(other: Any?): Boolean

  /**
   * Format the object depending on the current language
   */
  override fun toString(): String = toString(Locale.getDefault())

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  abstract fun toString(locale: Locale): String

  /**
   * Represents the value in sql
   */
  abstract fun toSql(): Any
}
