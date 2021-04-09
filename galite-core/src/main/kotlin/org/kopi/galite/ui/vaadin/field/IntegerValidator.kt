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
package org.kopi.galite.ui.vaadin.field

/**
 * An integer validator.
 *
 * @param minval The minimum accepted value.
 * @param maxval The maximum accepted value.
 */
class IntegerValidator(private val minval: Int?,
                       private val maxval: Int?,
                       maxLength: Int)
  : AllowAllValidator(maxLength) {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new integer validator instance.
   * @param minval The minimum accepted value.
   * @param maxval The maximum accepted value.
   */
  constructor(minval: Double?, maxval: Double?, maxLength: Int) : this(minval?.toInt(), maxval?.toInt(), maxLength)

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun validate(c: Char): Boolean {
    return Character.isDigit(c) || c == '.' || c == '-'
  }

  override fun checkType(field: InputTextField<*>, text: String) {
    if ("" == text) {
      field.value = null
    } else {
      val v = try {
        text.toInt()
      } catch (e: NumberFormatException) {
        throw CheckTypeException(field, "00006")
      }
      if (minval != null && v < minval) {
        throw CheckTypeException(field, "00012", minval)
      }
      if (maxval != null && v > maxval) {
        throw CheckTypeException(field, "00009", maxval)
      }
      field.value = text
    }
  }
}
