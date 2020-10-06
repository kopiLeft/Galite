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
import java.util.GregorianCalendar
import java.util.Calendar

/**
 * This class represents the week types
 */
open class Week : Type {
  /**
   * Constructs a Week with a year and a week in this year
   * @param    year        the year
   * @param    week        the week of year (1 .. 53)
   */
  internal constructor(year: Int, week: Int) {
    scalar = year * 53 + week - 1
  }

  /**
   * Constructs a Week from a Date
   */
  internal constructor(date: Date) {
    scalar = iso8601(date.year, date.month, date.day)
  }

  /**
   * Clones this object.
   */
  fun copy(): NotNullWeek {
    return NotNullWeek(scalar / 53, scalar % 53 + 1)
  }

  // ----------------------------------------------------------------------
  // IN PLACE OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Adds the specified number of months to this month.
   */
  fun addTo(weeks: Int) {
    scalar += weeks
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Returns a Week with the specified number of weeks added to this Week.
   */
  fun add(weeks: Int): NotNullWeek {
    return NotNullWeek((scalar + weeks) / 53, (scalar + weeks) % 53 + 1)
  }

  /**
   * subtract
   * @returns the number of weeks between two Weeks
   */
  fun subtract(other: Week?): Int?  = other ?.let { subtract(other as NotNullWeek) }

  /**
   * subtract
   * @returns the number of weeks between two Weeks
   */
  fun subtract(other: NotNullWeek): Int {
    return scalar - (other as Week).scalar
  }

  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Compares to another week.
   *
   * @param    other    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  operator fun compareTo(other: Week): Int {
    val v1 = scalar
    val v2 = other.scalar
    return if (v1 < v2) -1 else if (v1 > v2) 1 else 0
  }

  override operator fun compareTo(other: Any?): Int {
    return compareTo(other as Week)
  }// week to start at 1

  /**
   * Returns the week number (starts at 1, ends at 53)
   */
  val week: Int
    get() = scalar % 53 + 1 // week to start at 1

  /**
   * Returns the year of the week (by example 1999 or may be 2000 on year after)
   */
  val year: Int
    get() = scalar / 53

  /**
   * Returns the date specified by this week and a day of week.
   * @param    weekday        the day of week (monday = 1, sunday = 7)
   */
  fun getDate(weekday: Int): NotNullDate {
    val year: Int
    val month: Int
    val day: Int

    synchronized(calendar) {
      calendar.clear()
      calendar[Calendar.YEAR] = scalar / 53
      calendar[Calendar.WEEK_OF_YEAR] = scalar % 53 + 1
      calendar[Calendar.DAY_OF_WEEK] = weekday % 7 + 1 // Calendar.MONDAY = 2
      year = calendar[Calendar.YEAR]
      month = calendar[Calendar.MONTH] + 1
      day = calendar[Calendar.DAY_OF_MONTH]
    }
    return NotNullDate(year, month, day)
  }

  /**
   * Returns the first day of this week.
   */
  open fun getFirstDay(): NotNullDate {
    return getDate(1)
  }

  /**
   * Returns the last day of this week.
   */
  open fun getLastDay(): NotNullDate {
    return getDate(7)
  }

  /**
   * Transforms this week into a date (the first day of the week)
   */
  @Deprecated("")
  open fun getDate(): NotNullDate {
    return getDate(1)
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean {
    return other is Week && scalar == other.scalar
  }

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  override fun toString(locale: Locale): String {
    val year = scalar / 53
    val week = scalar % 53 + 1
    return (if (week < 10) "0$week" else "" + week) + "." + year
  }

  /**
   * Represents the value in sql
   */
  override fun toSql(): String {
    val year = scalar / 53
    val week = scalar % 53 + 1
    return "{fn WEEK($year, $week)}"
  }

  companion object {
    /**
     * Current week
     */
    fun now(): NotNullWeek {
      val now = Calendar.getInstance()
      return NotNullWeek(now[Calendar.YEAR], now[Calendar.WEEK_OF_YEAR])
    }

    // --------------------------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------------------------
    /**
     * Returns a scalar representation of the Year/Week combination
     * for the specified date.
     * @param    year        the year
     * @param    month        the month (1 .. 12)
     * @param    day        the day of month ( 1 .. 31)
     * @return    year * 53 + (week - 1)
     */
    private fun iso8601(year: Int, month: Int, day: Int): Int {
      val leapYear = isLeapYear(year)
      // 3. Find if Y-1 is LeapYear
      val leapYear_m_1 = isLeapYear(year - 1)
      var dayOfYearNumber = day + DAYS_BEFORE_MONTH[month - 1]

      if (leapYear && month > 2) {
        dayOfYearNumber += 1
      }
      val yy = (year - 1) % 100
      val c = year - 1 - yy
      val g = yy + yy / 4
      val jan1Weekday = 1 + (c / 100 % 4 * 5 + g) % 7
      val h = dayOfYearNumber + (jan1Weekday - 1)
      val weekday = 1 + (h - 1) % 7
      var yearNumber: Int
      var weekNumber: Int

      if (dayOfYearNumber <= 8 - jan1Weekday && jan1Weekday > 4) {
        yearNumber = year - 1
        weekNumber = if (jan1Weekday == 5 || jan1Weekday == 6 && leapYear_m_1) 53 else 52
      } else {
        yearNumber = year
        weekNumber = -10000000
      }
      val i = if (leapYear) 366 else 365

      if (i - dayOfYearNumber < 4 - weekday) {
        yearNumber = year + 1
        weekNumber = 1
      }
      if (yearNumber == year) {
        val j = dayOfYearNumber + (7 - weekday) + (jan1Weekday - 1)

        weekNumber = j / 7
        if (jan1Weekday > 4) {
          weekNumber -= 1
        }
      }
      return yearNumber * 53 + weekNumber - 1
    }

    /**
     * Returns true iff the specified year is a leap year.
     */
    private fun isLeapYear(year: Int): Boolean {
      return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }

    // --------------------------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------------------------
    private val DAYS_BEFORE_MONTH = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    private var calendar: GregorianCalendar = GregorianCalendar()

    // --------------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------------
    val DEFAULT: NotNullWeek = NotNullWeek(0, 0)

    init {
      calendar.firstDayOfWeek = Calendar.MONDAY
      calendar.minimalDaysInFirstWeek = 4
    }
  }

  private var scalar: Int
}
