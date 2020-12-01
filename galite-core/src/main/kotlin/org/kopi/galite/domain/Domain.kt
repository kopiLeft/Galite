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

import kotlin.reflect.KClass

/**
 * A domain is a data type with predefined list of allowed values.
 *
 * @param width             the width in char of this field
 * @param height            the height in char of this field
 * @param visibleHeight     the visible height in char of this field.
 */
open class Domain<T : Comparable<T>>(val width: Int? = null, val height: Int? = null, val visibleHeight: Int? = null) {
  /**
   * The type of this domain.
   */
  open val type: Domain<T>? = null

  /**
   * Determines the column data type
   */
  var kClass: KClass<T>? = null

  /**
   * Allows to define the possible codes that the domain can take
   *
   * @param init used to initialize the code domain
   */
  fun code(init: CodeDomain<T>.() -> Unit): CodeDomain<T> {
    val codeDomain = CodeDomain<T>(this::class.java.simpleName)
    codeDomain.init()
    return codeDomain
  }

  /**
   * Allows to define the possible codes that the domain can take
   *
   * @param init used to initialize the list domain
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
    return if (isCodeDomain()) {
      (type as CodeDomain<T>).codes
    } else if (isListDomain()) {
      (type as ListDomain<T>).list
    } else {
      throw Exception("Unsupported domain type")
    }
  }

  /**
   * Converts domain value to uppercase.
   *
   * @param value domain's value.
   */
  open fun applyConvertUpper(value: String): String {
    if (!isListDomain()) {
      throw UnsupportedOperationException("ConvertUpper is an unsupported " +
              "operation on current domain type")
    }

    return (type as ListDomain<T>).applyConvertUpper(value)
  }

  /**
   * returns true if this domain is a code domain, false otherwise
   */
  private fun isCodeDomain(): Boolean = type is CodeDomain<T>

  /**
   * returns true if this domain is a list domain, false otherwise
   */
  private fun isListDomain(): Boolean = type is ListDomain<T>
}
