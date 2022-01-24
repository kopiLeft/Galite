/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.type

import java.util.Locale

import org.jetbrains.exposed.sql.statements.api.ExposedBlob

/**
 * This class represents image types
 */
class Image(val width: Int, val height: Int, var value: ExposedBlob) : Type0<ExposedBlob> {

  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean {
    return other is Image &&
            width == other.width &&
            height == other.height &&
            value == other.value
  }

  /**
   * Format the object depending on the current language
   */
  override fun toString(): String {
    return toString(Locale.GERMAN) // !!!
  }

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  override fun toString(locale: Locale): String {
    val strBuilder = StringBuilder()
    return strBuilder.append(width)
                     .append(',')
                     .append(height)
                     .append(',')
                     .append(value).toString()
  }

  /**
   * Represents the value in sql
   */
  override fun toSql(): ExposedBlob = value

  companion object {

    /**
     * Comment for `serialVersionUID`
     */
    private const val serialVersionUID = 1L
  }
}
