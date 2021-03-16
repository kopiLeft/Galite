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
package org.kopi.galite.ui.vaadin.field

/**
 * Time validator.
 */
class TimeValidator(maxLength: Int) : AllowAllValidator(maxLength) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun validate(c: Char): Boolean = c in '0'..'9' || c == ':'

  override fun checkType(field: TextField, text: String) {
    if ("" == text) {
      field.value = null
    } else {
      var hours = -1
      var minutes = 0
      val buffer = text + '\u0000'
      var bp = 0
      var state: Int
      state = 1
      while (state > 0) {
        when (state) {
          1 -> when {
            buffer[bp] in '0'..'9' -> {
              hours = buffer[bp] - '0'
              state = 2
            }
            buffer[bp] == '\u0000' -> {
              state = 0
            }
            else -> {
              state = -1
            }
          }
          2 -> when {
            buffer[bp] in '0'..'9' -> {
              hours = 10 * hours + (buffer[bp] - '0')
              state = 3
            }
            buffer[bp] == ':' -> {
              state = 4
            }
            buffer[bp] == '\u0000' -> {
              state = 0
            }
            else -> {
              state = -1
            }
          }
          3 -> state = when(buffer[bp]) {
            ':' -> {
              4
            }
            '\u0000' -> {
              0
            }
            else -> {
              -1
            }
          }
          4 -> when {
            buffer[bp] in '0'..'9' -> {
              minutes = buffer[bp] - '0'
              state = 5
            }
            buffer[bp] == '\u0000' -> {
              state = 0
            }
            else -> {
              state = -1
            }
          }
          5 -> if (buffer[bp] in '0'..'9') {
            minutes = 10 * minutes + (buffer[bp] - '0')
            state = 6
          } else {
            state = -1
          }
          6 -> state = if (buffer[bp] == '\u0000') {
            0
          } else {
            -1
          }
        }
        bp += 1
      }
      if (state == -1) {
        throw CheckTypeException(field, "00007")
      }
      if (hours == -1) {
        field.value = null
      } else {
        if (!isTime(hours, minutes)) {
          throw CheckTypeException(field, "00007")
        }
        field.value = toString(hours, minutes)
      }
    }
  }

  /**
   * Checks if the given time is valid.
   * @param h The hours.
   * @param m The minutes.
   * @return `true` if the given time is valid.
   */
  private fun isTime(h: Int, m: Int): Boolean = h in 0..23 && m >= 0 && m < 60

  /**
   * Returns the string representation of the given time.
   * @param hours The time hours.
   * @param minutes The time minutes.
   * @return The string representation of the given time.
   */
  private fun toString(hours: Int, minutes: Int): String =
          buildString {
            append(hours / 10)
            append(hours % 10)
            append(':')
            append(minutes / 10)
            append(minutes % 10)
          }
}
