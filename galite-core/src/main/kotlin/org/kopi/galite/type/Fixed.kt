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
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.kopi.galite.type

import java.math.BigDecimal
import java.math.BigInteger

/**
 * This class represents the fixed type
 */
open class Fixed(b: BigDecimal?) : Number(), Comparable<Any?> {
  internal constructor(b: BigInteger?) : this(BigDecimal(b)) {}
  internal constructor(b: BigInteger?, l: Int) : this(BigDecimal(b)) {}
  internal constructor(value: Long, scale: Int) : this(BigDecimal.valueOf(value, scale)) {}
  internal constructor(d: Double) : this(BigDecimal(d)) {}
  internal constructor(s: String?) : this(BigDecimal(s)) {}
  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------
  fun add(f: NotNullFixed): NotNullFixed {
    TODO()
  }
  fun divide(f: NotNullFixed): NotNullFixed {
    TODO()
  }
  fun multiply(f: NotNullFixed): NotNullFixed {
    TODO()
  }
  fun subtract(f: NotNullFixed): NotNullFixed {
    TODO()
  }
  fun negate(): NotNullFixed {
    TODO("Not yet implemented")
  }
  fun setScale(v: Int): NotNullFixed {
    TODO("Not yet implemented")
  }
  fun setScale(v: Int, d: Int): NotNullFixed {
    TODO("Not yet implemented")
  }
  fun getScale(): Int = TODO("Not yet implemented")

  operator fun compareTo(other: Fixed): Int {
    TODO("Not yet implemented")
  }

  override operator fun compareTo(other: Any?): Int {
    TODO("Not yet implemented")
  }
  override fun equals(other: Any?): Boolean {
    TODO("Not yet implemented")
  }

  override fun toByte(): Byte {
    TODO("Not yet implemented")
  }

  override fun toChar(): Char {
    TODO("Not yet implemented")
  }

  override fun toDouble(): Double {
    TODO("Not yet implemented")
  }

  override fun toFloat(): Float {
    TODO("Not yet implemented")
  }

  override fun toInt(): Int {
    TODO("Not yet implemented")
  }

  override fun toLong(): Long {
    TODO("Not yet implemented")
  }

  override fun toShort(): Short {
    TODO("Not yet implemented")
  }

  private var value: BigDecimal? = null

  /*package*/
  init {
    value = b
  }
}
