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

import java.util.Date

/**
 * Week validator.
 */
class WeekValidator(maxLength: Int) : AllowAllValidator(maxLength) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun validate(c: Char): Boolean = c in '0'..'9' || c == '.' || c == '/'

  override fun checkType(field: TextField, text: String) {
    if ("" == text) {
      field.value = null
    } else {
      parseWeek(field, text)
    }
  }

  /**
   * Parses the given string entry as a week.
   * @param field The concerned input zone.
   * @param s The text to be parsed
   * @throws CheckTypeException When the text is not a valid week.
   */
  private fun parseWeek(field: TextField, s: String) {
    var week = 0
    var year = -1
    var bp = 0
    var state: Int
    val buffer = s + '\u0000'
    state = 1
    while (state > 0) {
      when (state) {
        1 -> if (buffer[bp] in '0'..'9') {
          week = buffer[bp] - '0'
          state = 2
        } else {
          state = -1
        }
        2 -> if (buffer[bp] in '0'..'9') {
          week = 10 * week + (buffer[bp] - '0')
          state = 3
        } else if (buffer[bp] == '.' || buffer[bp] == '/') {
          state = 4
        } else if (buffer[bp] == '\u0000') {
          state = 0
        } else {
          state = -1
        }
        3 -> state = if (buffer[bp] == '.' || buffer[bp] == '/') {
          4
        } else if (buffer[bp] == '\u0000') {
          0
        } else {
          -1
        }
        4 -> if (buffer[bp] in '0'..'9') {
          year = buffer[bp] - '0'
          state = 5
        } else if (buffer[bp] == '\u0000') {
          state = 0
        } else {
          state = -1
        }
        5 -> if (buffer[bp] in '0'..'9') {
          year = 10 * year + (buffer[bp] - '0')
          state = 6
        } else {
          state = -1
        }
        6 -> if (buffer[bp] in '0'..'9') {
          year = 10 * year + (buffer[bp] - '0')
          state = 7
        } else if (buffer[bp] == '\u0000') {
          state = 0
        } else {
          state = -1
        }
        7 -> if (buffer[bp] in '0'..'9') {
          year = 10 * year + (buffer[bp] - '0')
          state = 8
        } else {
          state = -1
        }
        8 -> state = if (buffer[bp] == '\u0000') {
          0
        } else {
          -1
        }
        else -> throw CheckTypeException(field, "00008")
      }
      bp += 1
    }
    if (state == -1) {
      throw CheckTypeException(field, "00008")
    }
    when {
      year == -1 -> {
        val now = Date()
        year = now.year + 1900
      }
      year < 50 -> {
        year += 2000
      }
      year < 100 -> {
        year += 1900
      }
    }
    field.value = toString(year, week)
  }

  /**
   * Converts the given week to its string representation.
   *
   * @param year The week year.
   * @param week The week number.
   * @return The string representation of the week.
   */
  private fun toString(year: Int, week: Int): String = (if (week < 10) "0$week" else "" + week) + "." + year
}
