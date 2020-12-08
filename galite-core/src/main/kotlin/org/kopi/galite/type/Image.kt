/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
import java.util.Arrays

/**
 * This class represents image types
 */
class Image(val width: Int, val height: Int, var byteArray: ByteArray?) : Type() {

  override fun equals(other: Any?): Boolean = other is Image?
                                              && width == other?.width
                                              && height == other?.height
                                              && byteArray === other?.byteArray

  override fun toString(locale: Locale): String {
    val strBuilder = StringBuilder()
    return strBuilder.append(width)
                     .append(',')
                     .append(height)
                     .append(',')
                     .append(byteArray).toString()
  }

  override fun toSql(): String {
    TODO("Not yet implemented")
  }

  /**
   * Compares to another image.
   * TODO Do we need to compare two images?
   */
  operator fun compareTo(other: Image): Int {
    return Arrays.compare(byteArray, other.byteArray)
  }

  override fun compareTo(other: Any): Int = compareTo(other as Image)

}
