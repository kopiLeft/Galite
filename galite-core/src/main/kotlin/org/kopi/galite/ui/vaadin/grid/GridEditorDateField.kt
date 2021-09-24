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
package org.kopi.galite.ui.vaadin.grid

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Server side implementation of an editor date field.
 * Date fields are fixed width fields with 10 characters inside.
 */
class GridEditorDateField: GridEditorTextField(10) {
  override fun check(text: String): Boolean {
    for (element in text) {
      if (!(element in '0'..'9' || element == '.' || element == '/')) {
        return false
      }
    }

    return true
  }

  override fun validate() {
    parseDate(value.orEmpty())
  }

  protected fun isNumeric(): Boolean {
    return true
  }

  /**
   * Parses the given date input.
   * @param f The input field.
   * @param s The date text.
   */
  private fun parseDate(s: String) {
    var day = 0
    var month = 0
    var year = -2
    val tokens = s.split("#|\\.|/".toRegex()).toTypedArray()
    if (tokens.isEmpty()) {
      throw InvalidEditorFieldException(this, "00003")
    }
    day = stringToInt(tokens[0])
    if (tokens.size >= 2) {
      month = stringToInt(tokens[1])
    }
    if (tokens.size >= 3) {
      year = stringToInt(tokens[2])
    }
    if (tokens.size > 3 || day == -1 || month == -1 || year == -1) {
      throw InvalidEditorFieldException(this, "00003")
    }
    if (month == 0) {
      val now = Date()
      month = now.month + 1
      year = now.year + 1900
    } else if (year == -2) {
      val now = Date()
      year = now.year + 1900
    } else if (year < 50) {
      year += 2000
    } else if (year < 100) {
      year += 1900
    } else if (year < 1000) {
      // less than 4 digits cause an error in database while paring the
      // sql statement
      throw InvalidEditorFieldException(this, "00003")
    }
    if (!isDate(day, month, year)) {
      throw InvalidEditorFieldException(this, "00003")
    }
    value = format(year, month, day)
  }

  /**
   *
   * @param in
   * @return
   */
  private fun stringToInt(`in`: String): Int {
    return try {
      Integer.valueOf(`in`).toInt()
    } catch (e: Exception) {
      -1
    }
  }

  /**
   * Checks if the given year, month and day is a valid date
   * @param d The day
   * @param m The month
   * @param y The year
   * @return `true` if the given parameters corresponds to a valid date.
   */
  private fun isDate(d: Int, m: Int, y: Int): Boolean {
    return if (y < 1 || m < 1 || m > 12 || d < 1) {
      false
    } else {
      when (m) {
        2 -> d <= (if (isLeapYear(y)) 29 else 28)
        4, 6, 9, 11 -> d <= 30
        else -> d <= 31
      }
    }
  }

  /**
   * Checks if the given date is a leap year.
   * @param year The year
   * @return `true` if the year is leap.
   */
  private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
  }

  /**
   * Formats the given date to the standard pattern (dd.MM.yyyy)
   * @param year The date year.
   * @param month The date month.
   * @param day The date day.
   * @return The formatted date.
   */
  private fun format(year: Int, month: Int, day: Int): String {
    return SimpleDateFormat("dd.MM.yyyy")
      .format(Date(year - 1900, month - 1, day))
  }
}
