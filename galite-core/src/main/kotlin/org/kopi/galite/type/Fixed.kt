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

import java.util.Locale
import java.math.BigInteger
import java.math.BigDecimal
import java.math.MathContext


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
  /**
   * add
   */
  fun add(f: NotNullFixed): NotNullFixed {
    TODO()
  }

  /**
   * divide
   */
  fun divide(f: NotNullFixed): NotNullFixed {
    TODO()
  }

  /**
   * multiply
   */
  fun multiply(f: NotNullFixed): NotNullFixed {
    TODO()
  }

  /**
   * subtract
   */
  fun subtract(f: NotNullFixed): NotNullFixed {
    if (value!!.compareTo(BigDecimal.ZERO) == 0) {
      return NotNullFixed((f as Fixed).value!!.negate())
    } else if ((f as Fixed).value!!.compareTo(BigDecimal.ZERO) == 0) {
      return NotNullFixed(value!!)
    }
    return NotNullFixed(value!!.subtract((f as Fixed).value, MATH_CONTEXT))
  }

  /**
   * Unary minus
   */
  fun negate(): NotNullFixed {
    return NotNullFixed(value!!.negate())
  }
  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * setScale
   */
  fun setScale(v: Int): NotNullFixed {
    return NotNullFixed(value!!.setScale(v, BigDecimal.ROUND_HALF_UP))
  }

  /**
   * setScale
   */
  fun setScale(v: Int, d: Int): NotNullFixed {
    return NotNullFixed(value!!.setScale(v, d))
  }

  /**
   * getScale
   */
  val scale: Int
    get() = value!!.scale()

  /**
   * Comparisons
   */
  operator fun compareTo(other: Fixed): Int {
    return value!!.compareTo(other.value)
  }

  override operator fun compareTo(other: Any?): Int {
    return compareTo(other as Fixed)
  }
  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean {
    return other is Fixed &&
            value!!.equals(other.value)
  }

  /**
   * Format the object depending on the current language
   */
  override fun toString(): String {
    return toString(Locale.GERMAN) // !!!
  }

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  fun toString(locale: Locale?): String {
    val str: String = value.toString()
    val buf = StringBuffer()
    var pos = 0
    var dot: Int

    // has minus sign ?
    if (str[0] == '-') {
      buf.append('-')
      pos = 1
    }

    // get number of digits in front of the dot
    if (str.indexOf('.').also { dot = it } == -1) {
      if (str.indexOf(' ').also { dot = it } == -1) {        // FRACTION DOT IS SPACE
        dot = str.length
      }
    }
    if (dot - pos <= 3) {
      buf.append(str.substring(pos, dot))
      pos = dot
    } else {
      when ((dot - pos) % 3) {
        1 -> {
          buf.append(str.substring(pos, pos + 1))
          pos += 1
        }
        2 -> {
          buf.append(str.substring(pos, pos + 2))
          pos += 2
        }
        0 -> {
          buf.append(str.substring(pos, pos + 3))
          pos += 3
        }
      }
      do {
        buf.append(".").append(str.substring(pos, pos + 3))
        pos += 3
      } while (dot - pos > 0)
    }
    if (str.length > pos) {
      buf.append(",").append(str.substring(pos + 1))
    }
    return buf.toString()
  }

  /**
   * Represents the value in sql
   */
  fun toSql(): String {
    return value.toString()
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF NUMBER
  // ----------------------------------------------------------------------
  /**
   * Returns the fixed as a byte
   */
  override fun toByte(): Byte {
    return value!!.toByte()
  }

  /**
   * Returns the fixed as a char
   */
  override fun toChar(): Char {
    return value!!.toChar()
  }

  /**
   * Returns the fixed as a double
   */
  override fun toDouble(): Double {
    return value!!.toDouble()
  }

  /**
   * Returns the fixed as a float
   */
  override fun toFloat(): Float {
    return value!!.toFloat()
  }

  /**
   * Returns the fixed as a int
   */
  override fun toInt(): Int {
    return value!!.toInt()
  }

  /**
   * Returns the fixed as a long
   */
  override fun toLong(): Long {
    return value!!.toLong()
  }

  /**
   * Returns the fixed as a short
   */
  override fun toShort(): Short {
    return value!!.toShort()
  }

  private var value: BigDecimal? = null
  var maxScale = -1   // the max scale.

  companion object {
    /**
     * Parse the String arguments and return the corresponding value
     */
    fun valueOf(value: String): NotNullFixed {
      return NotNullFixed(value)
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    private val MATH_CONTEXT: MathContext = TODO()
    private val DIV_CONTEXT: Any = TODO()

    // ----------------------------------------------------------------------
    // CONSTANTS
    // ----------------------------------------------------------------------
    val DEFAULT: NotNullFixed = NotNullFixed(0.0)
  }

  /*package*/
  init {
    value = b
  }
}
