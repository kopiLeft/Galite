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
import java.math.MathContext
import java.math.RoundingMode
import java.util.Locale

/**
 * This class represents the decimal type
 */
class Decimal(var value: BigDecimal) : Number(), Comparable<BigDecimal> {
  internal constructor(b: BigInteger) : this(BigDecimal(b))

  internal constructor(value: Long, scale: Int) : this(BigDecimal.valueOf(value, scale))

  internal constructor(d: Double) : this(BigDecimal(d))

  internal constructor(s: String) : this(BigDecimal(s))
  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------

  /**
   * add (f1 + f2) operator
   */
  operator fun plus(f: Decimal): Decimal {
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      return f
    } else if (f.value.compareTo(BigDecimal.ZERO) == 0) {
      return Decimal(value)
    }
    return Decimal(value.add(f.value, MATH_CONTEXT))
  }

  /**
   * plusAssign (f1 += f2) operator
   */
  operator fun plusAssign(f: Decimal) {
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      value = f.value
    } else if (f.value.compareTo(BigDecimal.ZERO) != 0) {
      value.add(f.value, MATH_CONTEXT)
    }
  }

  /**
   * divide (f1 / f2) operator
   */
  operator fun div(f: Decimal): Decimal {
    return Decimal(value.divide(f.value, DIV_CONTEXT).plus(MATH_CONTEXT))
  }

  /**
   * plusAssign (f1 /= f2) operator
   */
  operator fun divAssign(f: Decimal) {
    value = value.divide(f.value, DIV_CONTEXT).plus(MATH_CONTEXT)
  }

  /**
   * multiply (f1 * f2) operator
   */
  operator fun times(f: Decimal): Decimal {
    return Decimal(value.multiply(f.value, MATH_CONTEXT))
  }

  /**
   * timesAssign (f1 *= f2) operator
   */
  operator fun timesAssign(f: Decimal) {
    value = value.multiply(f.value, MATH_CONTEXT)
  }

  /**
   * subtract (f1 - f2) operator
   */
  operator fun minus(f: Decimal): Decimal {
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      return Decimal(f.value.negate())
    } else if (f.value.compareTo(BigDecimal.ZERO) == 0) {
      return Decimal(value)
    }
    return Decimal(value.subtract(f.value, MATH_CONTEXT))
  }

  /**
   * minusAssign (f1 -= f2) operator
   */
  operator fun minusAssign(f: Decimal) {
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      value = f.value.negate()
    } else if (f.value.compareTo(BigDecimal.ZERO) != 0) {
      value = value.subtract(f.value, MATH_CONTEXT)
    }
  }

  /**
   * Unary minus (-f) operator
   */
  operator fun unaryMinus(): Decimal {
    return Decimal(value.negate())
  }

  /**
   * remainder (f1 % f2) operator
   */
  operator fun rem(f: Decimal): Decimal {
    return Decimal(value.remainder(f.value, MATH_CONTEXT))
  }

  /**
   * remAssign (f1 %= f2) operator
   */
  operator fun remAssign(f: Decimal) {
    value = value.remainder(f.value, MATH_CONTEXT)
  }
  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * setScale
   */
  fun setScale(v: Int): Decimal {
    return Decimal(value.setScale(v, RoundingMode.HALF_UP))
  }

  /**
   * setScale
   */
  fun setScale(v: Int, d: Int): Decimal {
    return Decimal(value.setScale(v, d))
  }

  /**
   * getScale
   */
  val scale: Int
    get() = value.scale()

  /**
   * Returns the decimal as a double
   */
  override fun toDouble(): Double {
    return value.toDouble()
  }

  // ----------------------------------------------------------------------
  // COMPARISONS
  // ----------------------------------------------------------------------

  operator fun compareTo(other: Decimal): Int {
    return value.compareTo(other.value)
  }

  override fun compareTo(other: BigDecimal): Int {
    return value.compareTo(other)
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean {
    return other is Decimal &&
            value == other.value
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
    val str = value.toString()
    var pos = 0
    var dot: Int

    return buildString {
      // has minus sign ?
      if (str[0] == '-') {
        append('-')
        pos = 1
      }

      // get number of digits in front of the dot
      if (str.indexOf('.').also { dot = it } == -1) {
        if (str.indexOf(' ').also { dot = it } == -1) {        // FRACTION DOT IS SPACE
          dot = str.length
        }
      }
      if (dot - pos <= 3) {
        append(str.substring(pos, dot))
        pos = dot
      } else {
        when ((dot - pos) % 3) {
          1 -> {
            append(str.substring(pos, pos + 1))
            pos += 1
          }
          2 -> {
            append(str.substring(pos, pos + 2))
            pos += 2
          }
          0 -> {
            append(str.substring(pos, pos + 3))
            pos += 3
          }
        }
        do {
          append(".").append(str.substring(pos, pos + 3))
          pos += 3
        } while (dot - pos > 0)
      }
      if (str.length > pos) {
        append(",").append(str.substring(pos + 1))
      }
    }
  }

  /**
   * Represents the value in sql
   */
  fun toSql(): String = value.toString()

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF NUMBER
  // ----------------------------------------------------------------------
  override fun toInt(): Int {
    return value.toInt()
  }

  override fun toLong(): Long {
    return value.toLong()
  }

  override fun toShort(): Short {
    return value.toShort()
  }

  override fun toFloat(): Float {
    return value.toFloat()
  }

  override fun toByte(): Byte {
    return value.toByte()
  }

  override fun toChar(): Char {
    return value.toChar()
  }

  /**
   * The Max scale
   */
  var maxScale = -1

  companion object {
    /**
     * Parse the String arguments and return the corresponding value
     */
    fun valueOf(value: String): Decimal {
      return Decimal(value)
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    private val MATH_CONTEXT: MathContext = MathContext(0, RoundingMode.HALF_UP)
    private val DIV_CONTEXT: MathContext = MathContext(30, RoundingMode.HALF_UP)

    // ----------------------------------------------------------------------
    // CONSTANTS
    // ----------------------------------------------------------------------
    val DEFAULT: Decimal = Decimal(0.0)

    /**
     * Comment for `serialVersionUID`
     */
    private const val serialVersionUID = 1L
  }
}

/**
 * Creates a Decimal type from a BigDecimal
 */
fun decimal(bigDecimal: BigDecimal): Decimal = Decimal(bigDecimal)

/**
 * Creates a Decimal type from a BigInteger
 */
fun decimal(bigInteger: BigInteger): Decimal = Decimal(bigInteger)

/**
 * Creates a Decimal type from a long value and scale
 */
fun decimal(value: Long, scale: Int): Decimal = Decimal(value, scale)

/**
 * Creates a Decimal type from a Double
 */
fun decimal(double: Double): Decimal = Decimal(double)

/**
 * Creates a Decimal type from a String
 */
fun decimal(string: String): Decimal = Decimal(string)
