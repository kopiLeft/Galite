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
 * Represents a code domain.
 */
class CodeDomain<T : Comparable<T>>(private val name: String, kClass: KClass<*>) : Domain<T>(kClass = kClass) {
  /**
   * Mapping of all values that a domain can take
   */
  val codes: MutableMap<String, T> = mutableMapOf()

  override val type = this

  /**
   * Sets a mapping between the values that the domain can take
   * and a corresponding text to be displayed in a field.
   *
   * @param text the text
   * @param value the value
   */
  operator fun set(text: String, value: T) {
    if (text in codes.keys) {
      throw RuntimeException("$text already exists in domain $name")
    }
    codes[text] = value
  }
}
