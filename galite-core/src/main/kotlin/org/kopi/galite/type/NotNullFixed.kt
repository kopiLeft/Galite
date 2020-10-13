/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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
 * License along with this library; if not, write timport java.math.BigIntegero the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.kopi.galite.type

import java.math.BigInteger
import java.math.BigDecimal

/**
 * This class represents the not null fixed type
 */
class NotNullFixed : Fixed {

  constructor (b: BigDecimal) : super(b) {}
  constructor(b: BigInteger) : super(b) {}
  constructor(b: BigInteger, l: Int) : super(b) {}
  constructor(value: Long, scale: Int) : super(value, scale) {}
  constructor(d: Double) : super(d) {}
  constructor(s: String) : super(s) {}

  /**
   * Checks whether this object is equal to the specified object.
   */
  override fun equals(other: Any?): Boolean = (other is NotNullFixed && super.equals(other))

  companion object {
    fun castToNotNull(value: Fixed?): NotNullFixed = value as NotNullFixed
  }
}