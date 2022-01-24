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

package org.kopi.galite.visual.dsl.field

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.LocalizationWriter

/**
 * A field represents a visual component that can hold values
 *
 * The type of the value and the range of possible values can be specified by the [domain]
 *
 * @param domain the field's domain
 */
abstract class Field<T>(open val domain: Domain<T>) {
  /** Field's label (text on the left) */
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
   * Generates localization for this field
   *
   * @param writer The localization writer.
   */
  abstract fun genLocalization(writer: LocalizationWriter)
}
