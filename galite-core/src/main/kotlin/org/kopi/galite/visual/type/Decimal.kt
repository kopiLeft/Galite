/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.type

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.util.Locale

/**
 * This class represents the decimal type
 */
class Decimal(var value: BigDecimal) : Number(), Comparable<Decimal>, Type0<BigDecimal> {

  /**
   * The Max scale
   */
  var maxScale = -1

  constructor(b: BigInteger) : this(BigDecimal(b))

  constructor(value: Long, scale: Int) : this(BigDecimal.valueOf(value, scale))

  constructor(d: Double) : this(BigDecimal(d))

  constructor(s: String) : this(BigDecimal(s))
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
  operator fun div(f: Decimal): Decimal = Decimal(value.divide(f.value, DIV_CONTEXT).plus(MATH_CONTEXT))

  /**
   * plusAssign (f1 /= f2) operator
   */
  operator fun divAssign(f: Decimal) {
    value = value.divide(f.value, DIV_CONTEXT).plus(MATH_CONTEXT)
  }

  /**
   * multiply (f1 * f2) operator
   */
  operator fun times(f: Decimal): Decimal = Decimal(value.multiply(f.value, MATH_CONTEXT))

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
  operator fun rem(f: Decimal): Decimal = Decimal(value.remainder(f.value, MATH_CONTEXT))

  /**
   * remAssign (f1 %= f2) operator
   */
  operator fun remAssign(f: Decimal) {
    value = value.remainder(f.value, MATH_CONTEXT)
  }

  infix fun shr(count: Int): Decimal {
    return setScale(count, BigDecimal.ROUND_FLOOR)
  }

  infix fun ushr(count: Int): Decimal {
    return setScale(count, BigDecimal.ROUND_HALF_UP)
  }

  infix fun shl(count: Int): Decimal {
    return setScale(count, BigDecimal.ROUND_UP)
  }

  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * setScale
   */
  fun setScale(v: Int): Decimal = Decimal(value.setScale(v, RoundingMode.HALF_UP))

  /**
   * setScale
   */
  fun setScale(v: Int, d: Int): Decimal = Decimal(value.setScale(v, d))

  /**
   * getScale
   */
  val scale: Int
    get() = value.scale()

  /**
   * Returns the decimal as a double
   */
  override fun toDouble(): Double = value.toDouble()

  // ----------------------------------------------------------------------
  // COMPARISONS
  // ----------------------------------------------------------------------

  override operator fun compareTo(other: Decimal): Int = value.compareTo(other.value)

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean =
          other is Decimal &&
                  value == other.value

  /**
   * Format the object depending on the current language
   */
  override fun toString(): String = toString(Locale.GERMAN) // !!!

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  override fun toString(locale: Locale): String {
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
  override fun toSql(): BigDecimal = value

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF NUMBER
  // ----------------------------------------------------------------------
  override fun toInt(): Int = value.toInt()

  override fun toLong(): Long = value.toLong()

  override fun toShort(): Short = value.toShort()

  override fun toFloat(): Float = value.toFloat()

  override fun toByte(): Byte = value.toByte()

  override fun toChar(): Char = value.toChar()

  override fun hashCode(): Int {
    value.hashCode()
    var result = value.hashCode()
    result = 31 * result + maxScale
    return result
  }

  companion object {
    /**
     * Parse the String arguments and return the corresponding value
     */
    fun valueOf(value: String): Decimal = Decimal(value)

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
