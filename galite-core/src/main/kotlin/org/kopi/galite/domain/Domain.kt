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
open class Domain<T: Comparable<T>>(val length: Int? = null) {

    /**
     * Override it if you want to define a constraint that the domain values ​​must meet.
     */
    open val check: ((value: T) -> Boolean)? = null

    /**
     * Allows to define list of possible values that the domain can take
     */
    open val code: (() -> Any)? = null

    /**
     * Override it if you want to apply transformation on values.
     *
     * You can use [Transformation.convertUpper] to apply convert to uppercase transformation
     */
    open val transformation: Transformation.TransfomationType? = null

    /**
     * Maps a name with a value
     *
     * @param name the name
     * @param value the value
     */
    operator fun set(name: String, value: T) {
        codes[name] = value
    }

    val codes = mutableMapOf<String, T>()
}
