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

package org.galite.domain

open class Domain<T : Comparable<T>>(val size: Int? = null) : Comparable<Domain<T>> {
  open val check: ((value: T) -> Boolean)? = null
  open val list: Unit? = null
  open val convertUpper = false
  open val code: Unit? = null

  fun list(function: DomainList.() -> Unit) {
    TODO("Not yet implemented")
  }

  fun code(vararg data: Pair<String, T>) {
    TODO("Not yet implemented")
  }

  override fun compareTo(other: Domain<T>): Int {
    TODO("Not yet implemented")
  }
}