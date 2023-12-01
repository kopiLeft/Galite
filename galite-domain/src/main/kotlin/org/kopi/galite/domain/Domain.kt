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

package org.kopi.galite.domain

import java.math.BigDecimal
import java.util.regex.Pattern

import kotlin.reflect.KClass

/**
 * A domain is a data type with predefined list of allowed values.
 */
open class Domain<T> {

  /**
   * the height in char of this field
   */
  val height: Int? = null

  /**
   * the visible height in char of this field.
   */
  val visibleHeight: Int? = null

  /**
   * True if the field is a richtext editor.
   */
  protected var styled: Boolean = false

  /**
   * TODO: doc?
   */
  protected var fixed: Fixed = Fixed.UNDEFINED

  /**
   * The conversion method.
   */
  protected var convert: Convert = Convert.NONE

  /**
   * Sets the minimum value of a number domain.
   */
  var <U> Domain<U>.min : U? where U : Comparable<U>?, U : Number?
    get() = min
    set(value) {
      min = value
    }

  /**
   * Sets the maximum value of a number domain.
   */
  var <U> Domain<U>.max : U? where U : Comparable<U>?, U : Number?
    get() = max
    set(value) {
      max = value
    }

  /**
   * Sets the minimum value of a number domain.
   */
  var Domain<String>.min : Int?
    get() = minWidth
    set(value) {
      minWidth = value
    }

  /**
   * Sets the maximum value of a number domain.
   */
  var Domain<String>.max : Int?
    get() = maxWidth
    set(value) {
      maxWidth = value
    }

  /**
   * Sets the minimum value of a number domain.
   */
  var Domain<BigDecimal>.precisionExt : Int?
    get() = precision
    set(value) {
      precision = value
    }

  /**
   * Sets the maximum value of a number domain.
   */
  public var Domain<BigDecimal>.scaleExt : Int?
    get() = scale
    set(value) {
      scale = value
    }

  /**
   * Defines a [constraint] that the field value should verify. Otherwise an error [message] is displayed to the user.
   *
   * @param message the error message to display.
   * @param constraint the constraint that the field value should verify.
   */
  fun check(message: String? = null, constraint: (value: T?) -> Boolean) {
    this.constraint = Constraint(message, constraint)
  }

  /**
   * A pattern that the value should match.
   */
  var pattern: Pattern? = null

  // INTERNALS
  internal var kClass: KClass<*>? = null // Determines the field data type
  internal val ident: String = this::class.java.simpleName
  private var min : T? = null // the minimum value that cannot exceed
  private var max : T? = null // the maximum value that cannot exceed
  var minWidth : Int? = null // the minimum width in char of this field
  var maxWidth : Int? = null // the maximum width in char of this field
  var precision : Int? = null // the minimum width in char of this field
  var scale : Int? = null // the maximum width in char of this field
  private var constraint: Constraint<T>? = null

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------
  fun hasSize(): Boolean =
          when (kClass) {
            BigDecimal::class, Int::class, Long::class, String::class -> true
            else -> false
          }
}
