/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
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

import org.jetbrains.exposed.sql.statements.api.ExposedBlob

class Color(var value: ExposedBlob) : Type0<ExposedBlob> {

  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean {
    return other is Color && value == other.value
  }

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  override fun toString(locale: Locale): String {
    return value.hexString()
  }

  /**
   * Represents the value in sql
   */
  override fun toSql(): ExposedBlob = value

  /**
   * Generete the HashCode of the value of field.
   */
  override fun hashCode(): Int {
    return value.hashCode()
  }
}
