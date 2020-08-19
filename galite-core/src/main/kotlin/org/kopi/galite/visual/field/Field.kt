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

package org.kopi.galite.visual.field

import org.kopi.galite.domain.Domain
import org.kopi.galite.visual.exceptions.InvalidValueException

/**
 * A field represents a visual component that can hold values
 *
 * The type of the value and the range of possible values can be specified by the [domain]
 *
 * @param domain the field's domain
 */
class Field<T : Comparable<T>>(val domain: Domain<T>? = null) {

  /** Field's label */
  var label: String = ""

  /** Field's help that describes the expected value of an input field */
  var help: String = ""

  /**
   * Checks if the value passed to the field doesn't exceed the length of the field's domain
   *
   * @param value passed value
   * @return true if the domain is not defined or the value's length doesn't exceed the domain size,
   * and returns false otherwise.
   */
  fun checkLength(value: T): Boolean = when {
    domain == null -> true
    domain.length == null -> true
    else -> value.toString().length <= domain.length
  }

  /**
   * Checks if the value passed to the field respects the [domain.check] constraint
   *
   * @param value passed value
   * @return  true if the domain is not defined or if the values if verified by the domain's constraint
   * @throws InvalidValueException otherwise
   */
  fun checkValue(value: T): Boolean = when {
    domain == null -> true
    domain.check == null -> true
    domain.check!!.invoke(value) -> true
    else -> throw InvalidValueException(value, label)
  }

  /**
   * Applies a transformation on the value.
   *
   * @param value passed value
   * @return value after transformation
   */
  fun applyTransformation(value: T): Any? = when {
    domain == null -> value
    domain.transformation == null -> value
    domain.transformation == Transformation.TransfomationType.CONVERT_UPPER -> (value as String).toUpperCase()
    else -> null
  }

  /**
   * returns list of code values that can this field get.
   */
  fun getCodes(): MutableMap<String, T>? {
    return domain?.domainCode?.codes
  }
}
