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

package org.kopi.galite.domain

import org.kopi.galite.visual.field.Transformation

/**
 * A domain is used to specify the type of values that a [Field] can hold. It allow to specify
 * the set of values that a [Field] can hold. You can also do some checks on these values.
 *
 * @param length the maximum length of the value that can be passed
 */
abstract class Domain<T : Comparable<T>>(val length: Int? = null) {

  /**
   * Allows to define the possible codes that the domain can take
   *
   * @param init
   */
  fun code(init: DomainCode<T>.() -> Unit): DomainType {
    domainCode.init()
    return DomainType.CODE
  }

  /**
   * Override it if you want to define a constraint that the domain values ​​must verify.
   */
  open val check: ((value: T) -> Boolean)? = null

  /**
   * Allows to define the values that the domain can take.
   */
  abstract val values: DomainType

  /**
   * Override it if you want to apply transformation on values.
   *
   * You can use [Transformation.convertUpper] to apply convert to uppercase transformation
   */
  open val transformation: Transformation.TransfomationType? = null

  /**
   * Codes that this domain can take
   */
  var domainCode = DomainCode<T>()
}

/**
 * Defines the domain types
 */
enum class DomainType {
  LIST,
  CODE
}
