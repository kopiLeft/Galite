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
package org.kopi.galite.visual.ui.vaadin.field

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Fixed numbers validator.
 */
class DecimalValidator(
        private val maxScale: Int,
        private val fraction: Boolean,
        private val width: Int,
        private val minval: Double?,
        private val maxval: Double?,
        maxLength: Int
) : AllowAllValidator(maxLength) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun validate(c: Char): Boolean = c in '0'..'9' || c == '.' || c == '-' || c == ' ' || c == ',' || c == '/'

  override fun checkType(field: InputTextField<*>, text: String) {
    if ("" == text) {
    } else {
      val v = try {
        scanBigDecimal(text)
      } catch (e: NumberFormatException) {
        throw CheckTypeException(field, "00006")
      }
      if (v != null) {
        //!!! FIXME : we should get here the current scale of the field and
        //            and not the max scale. The DecimalField should be modified
        //            so that the current scale is sent to the client side when it changes.
        //            This scale value should be updated in validator instance of the field
        //            and then used in this test.
        if (v.scale() > maxScale) {
          throw CheckTypeException(field, "00011", maxScale)
        }
        if (minval != null && v.compareTo(BigDecimal(minval)) == -1) {
          throw CheckTypeException(field, "00012", minval)
        }
        if (maxval != null && v.compareTo(BigDecimal(maxval)) == 1) {
          throw CheckTypeException(field, "00009", maxval)
        }
        if (toText(v.setScale(maxScale)).length > width) {
          throw CheckTypeException(field, "00010")
        }
      }
      //!!! FIXME : here too the current scale of the field should be used.
      field.value = toText(v!!.setScale(maxScale))
    }
  }

  /**
   * Returns the
   * @param value
   * @return
   */
  private fun toString(value: BigDecimal): String =
          buildString {
            val str = value.toString()
            var pos = 0
            var dot: Int

            // has minus sign ?
            if (str[0] == '-') {
              append('-')
              pos = 1
            }

            // get number of digits in front of the dot
            if (str.indexOf('.').also { dot = it } == -1) {
              if (str.indexOf(' ').also { dot = it } == -1) {
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
                append(".")
                append(str.substring(pos, pos + 3))
                pos += 3
              } while (dot - pos > 0)
            }
            if (str.length > pos) {
              append(",")
              append(str.substring(pos + 1))
            }
          }

  /**
   * Returns the string representation in human-readable format.
   * @return The string representation in human-readable format.
   */
  fun toText(v: BigDecimal): String =
          if (!fraction) {
            toString(v)
          } else {
            toFraction(toString(v))
          }

  /**
   * Returns the fraction representation of the given string.
   * @param str The string to calculate its fraction.
   * @return The calculated fraction.
   */
  private fun toFraction(str: String): String {
    var dot: Int
    if (str.indexOf(',').also { dot = it } == -1) {
      return str
    }
    val precomma = str.substring(0, dot)
    val fract = Integer.valueOf(str.substring(dot + 1, str.length)).toInt()
    return if (fract * 64 % 1000000 != 0) {
      str
    } else if (fract == 0) {
      precomma
    } else {
      var num: Int
      var den = 64
      num = fract * den / 1000000
      while (num % 2 == 0) {
        num /= 2
        den /= 2
      }
      when (precomma) {
        "0" -> "$num/$den"
        "-0" -> "-$num/$den"
        else -> "$precomma $num/$den"
      }
    }
  }

  companion object {
    /**
     * Parses the string argument as a big decimal number in human-readable format.
     * @param str The string to be scanned.
     */
    private fun scanBigDecimal(str: String): BigDecimal? {
      var negative = false
      var state = 0
      var scale = 0
      var value: Long = 0
      var num: Long = 0
      var den: Long = 0
      if (str == "") {
        return null
      }
      for (i in str.indices) {
        // skip dots
        if (str[i] == '.') {
          continue
        }
        when (state) {
          0 -> when {
            str[i] == ' ' -> {
              state = 0
            }
            str[i] == '+' -> {
              state = 1
            }
            str[i] == '-' -> {
              negative = true
              state = 1
            }
            str[i] == ',' -> {
              state = 3
            }
            Character.isDigit(str[i]) -> {
              value = Character.digit(str[i], 10).toLong()
              state = 2
            }
            else -> {
              throw NumberFormatException()
            }
          }
          1 -> when {
            str[i] == ' ' -> {
              state = 1
            }
            str[i] == ',' -> {
              state = 3
            }
            Character.isDigit(str[i]) -> {
              value = Character.digit(str[i], 10).toLong()
              state = 2
            }
            else -> {
              throw NumberFormatException()
            }
          }
          2 -> when {
            str[i] == ',' -> {
              state = 3
            }
            str[i] == ' ' -> {
              state = 4
            }
            str[i] == '/' -> {
              num = value
              value = 0
              state = 6
            }
            Character.isDigit(str[i]) -> {
              value = 10 * value + Character.digit(str[i], 10)
              state = 2
            }
            else -> {
              throw NumberFormatException()
            }
          }
          3 -> if (Character.isDigit(str[i])) {
            value = 10 * value + Character.digit(str[i], 10)
            scale += 1
            state = 3
          } else {
            throw NumberFormatException()
          }
          4 -> when {
            str[i] == ' ' -> {
              state = 4
            }
            Character.isDigit(str[i]) -> {
              num = Character.digit(str[i], 10).toLong()
              state = 5
            }
            else -> {
              throw NumberFormatException()
            }
          }
          5 -> when {
            str[i] == '/' -> {
              state = 6
            }
            Character.isDigit(str[i]) -> {
              num = 10 * num + Character.digit(str[i], 10)
              state = 5
            }
            else -> {
              throw NumberFormatException()
            }
          }
          6 -> when {
            str[i] == '0' -> {
              state = 6
            }
            Character.isDigit(str[i]) -> {
              den = Character.digit(str[i], 10).toLong()
              state = 7
            }
            else -> {
              throw NumberFormatException()
            }
          }
          7 -> if (Character.isDigit(str[i])) {
            den = 10 * den + Character.digit(str[i], 10)
            state = 7
          } else {
            throw NumberFormatException()
          }
          else -> throw RuntimeException()
        }
      }
      when (state) {
        0 -> return null
        2 -> {
        }
        3 ->       // remove trailing zeroes after comma
          while (scale > 0 && value % 10 == 0L) {
            value /= 10
            scale -= 1
          }
        7 -> {
          if (num > den || num % 2 == 0L || den > 64) {
            throw NumberFormatException()
          }
          when (den.toInt()) {
            2 -> {
              value = 10 * value + 5 * num
              scale = 1
            }
            4 -> {
              value = 100 * value + 25 * num
              scale = 2
            }
            8 -> {
              value = 1000 * value + 125 * num
              scale = 3
            }
            16 -> {
              value = 10000 * value + 625 * num
              scale = 4
            }
            32 -> {
              value = 100000 * value + 3125 * num
              scale = 5
            }
            64 -> {
              value = 1000000 * value + 15625 * num
              scale = 6
            }
            else -> throw NumberFormatException()
          }
        }
        else -> throw NumberFormatException()
      }
      return if (value == 0L) {
        BigDecimal.ZERO
      } else {
        if (negative) {
          value = -value
        }
        BigDecimal(BigInteger.valueOf(value), scale)
      }
    }
  }
}
