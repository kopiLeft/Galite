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
  fun code(init: CodeDomain<T>.() -> Unit): CodeDomain<T> {
    val codeDomain = CodeDomain<T>(this::class.java.simpleName)
    codeDomain.init()
    return codeDomain
  }

  /**
   * Allows to define the possible codes that the domain can take
   *
   * @param init
   */
  fun list(init: ListDomain<T>.() -> Unit): ListDomain<T> {
    val listDomain = ListDomain<T>(this::class.java.simpleName)
    listDomain.init()
    return listDomain
  }

  /**
   * returns list of code values that can this field get.
   */
  fun getValues(): MutableMap<String, *> {
    return if (isCodeField()) {
      (type as CodeDomain<T>).codes
    } else if (isListField()) {
      (type as ListDomain<T>).list
    } else {
      throw Exception("Unsupported domain type")
    }
  }

  /**
   * returns list of code values that can this field get.
   */
  open fun applyConvertUpper(value: String): String? {
    return if (isListField()) {
      (type as ListDomain<T>).applyConvertUpper(value)
    } else {
      throw UnsupportedOperationException("Unsupported operation on current domain type")
    }
  }

  /**
   * The type of this domain.
   */
  abstract val type: Domain<T>

  /**
   * Override it if you want to define a constraint that the domain values ​​must verify.
   */

  open val check: ((value: T) -> Boolean)? = null

  fun isCodeField(): Boolean = type is CodeDomain<T>

  fun isListField(): Boolean = type is ListDomain<T>
}
