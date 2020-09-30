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

/**
 * This class represents the fixed type
 */
open class Fixed internal constructor(d: Double) : Number(), Comparable<Any?> {

  override fun compareTo(other: Any?): Int {
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
}
