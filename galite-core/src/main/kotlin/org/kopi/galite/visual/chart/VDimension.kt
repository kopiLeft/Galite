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

package org.kopi.galite.visual.chart

/**
 * A chart dimension represented by its name and its key.
 *
 * @param ident The dimension identifier.
 * @param format The dimension format.
 */
abstract class VDimension protected constructor(ident: String, var format: VColumnFormat?) : VColumn(ident) {
  //---------------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------------
  /**
   * Formats the dimension value.
   *
   * @param value The value to be formatted.
   * @return The string representation of the object value.
   */
  protected abstract fun toString(value: Any?): String

  //---------------------------------------------------------------------
  // UTILS
  //---------------------------------------------------------------------
  /**
   * Returns the String representation of the dimension value.
   *
   * @param value The dimension value.
   * @return The formatted value.
   */
  fun format(value: Any?): String {
    return if (format != null) format!!.format(value) else toString(value)
  }
}
