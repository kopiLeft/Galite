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

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Query

import org.kopi.galite.exceptions.InvalidValueException

/**
 * Represents a list domain.
 *
 * The possible values are obtained from the result of a database query.
 *
 * It allows optionally to define a constraint that makes restrictions
 * on the set of allowed values.
 */
class ListDomain<T : Comparable<T>>(private val name: String): Domain<T>() {
  var query: Query? = null

  /**
   * Sets a mapping between the values that the domain can take
   * and a corresponding text to be displayed in a field.
   *
   * @param text the text
   * @param value the value
   */
  operator fun <V: Comparable<V>> set(text: String, value: Column<V>) {
    if(text in list.keys) {
      throw RuntimeException("$text already exists in domain $name")
    }
    list[text] = value
  }

  /**
   * Mapping of all values that a domain can take
   */
  val list: MutableMap<String, Any> = mutableMapOf()

  /**
   * Transforms values in capital letters.
   */
  fun Domain<String>.convertUpper() {
    convertUpper = true
  }

  /**
   * Applies a transformation on the value.
   *
   * @param value passed value
   * @return value after transformation
   */
  override fun applyConvertUpper(value: String): String = when {
    convertUpper -> value.toUpperCase()
    else -> value
  }

  /**
   * Checks if the value passed to the field respects the [domain.check] constraint
   *
   * @param value passed value
   * @return  true if the domain is not defined or if the values if verified by the domain's constraint
   * @throws InvalidValueException otherwise
   */
  fun checkValue(value: T): Boolean = when {
    check == null || check!!.invoke(value) -> true
    else -> false
  }

  /**
   * Override it if you want to define a constraint that the domain values ​​must verify.
   */
  var check: ((value: T) -> Boolean)? = null

  override val type = this
  private var convertUpper = false
}
