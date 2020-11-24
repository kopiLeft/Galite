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

package org.kopi.galite.field

import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.domain.Domain
import org.kopi.galite.domain.ListDomain
import org.kopi.galite.exceptions.InvalidValueException

/**
 * A field represents a visual component that can hold values
 *
 * The type of the value and the range of possible values can be specified by the [domain]
 *
 * @param domain the field's domain
 */
abstract class Field<T : Comparable<T>>(open val domain: Domain<T>) {
  /** Field's label */
  var label: String? = null

  /** Field's help that describes the expected value of an input field */
  var help: String? = null

  /** true if the field is hidden, false otherwise */
  open var hidden: Boolean? = false

  /**
   * Checks if the value passed to the field doesn't exceed the length of the field's domain
   *
   * @param value passed value
   * @return true if the domain is not defined or the value's length doesn't exceed the domain size,
   * and returns false otherwise.
   */
  fun checkLength(value: T): Boolean = when (domain.width) {
    null -> true
    else -> value.toString().length <= domain.width!!
  }

  /**
   * Checks if the value passed to the field respects the check constraint
   *
   * @param value passed value
   * @return  true if the domain is not defined or if the values if verified by
   * the domain's constraint
   * @throws InvalidValueException otherwise
   */
  fun checkValue(value: T): Boolean = when {
    domain.type is ListDomain && (domain.type as ListDomain).checkValue(value) -> true
    domain.type !is ListDomain -> throw UnsupportedOperationException("Check not supported " +
            "by this domain type")
    else -> throw InvalidValueException(value, label)
  }

  /**
   * returns list of values that can this field get.
   */
  fun getValues(): MutableMap<String, *> {
    return domain.getValues()
  }

  /**
   * Generates localization for this field
   *
   * @param The localization writer.
   */
  abstract fun genLocalization(writer: LocalizationWriter)
}
