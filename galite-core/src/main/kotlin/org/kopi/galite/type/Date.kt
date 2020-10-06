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

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * This class represents date types
 */
open class Date : Type {

  internal constructor(year: Int, month: Int, day: Int) {
    scalar = gregorianToJulian(year, month, day)
  }

  internal constructor(date: java.sql.Date) {
    synchronized(calendar) {
      calendar.time = date
      scalar = gregorianToJulian(calendar[Calendar.YEAR],
              calendar[Calendar.MONTH] + 1,
              calendar[Calendar.DAY_OF_MONTH])
    }
  }

  internal constructor(calendar: Calendar?) {
    if (calendar != null) {
      scalar = gregorianToJulian(calendar[Calendar.YEAR],
              calendar[Calendar.MONTH] + 1,
              calendar[Calendar.DAY_OF_MONTH])
    }
  }

  /**
   * Parses a date of format 'yyyy.MM.dd' or 'yyyy-MM-dd'
   */
  internal constructor(image: String) {
    val pattern: Pattern = Pattern.compile("(\\d\\d\\d\\d)[-.]{1}(\\d\\d?)[-.]{1}(\\d\\d?)")
    val matcher: Matcher = pattern.matcher(image)

    if (matcher.matches()) {
      val res = Pattern.compile("[-.]").split(image)
      scalar = gregorianToJulian(res[0].toInt(), res[1].toInt(), res[2].toInt())
    } else {
      throw IllegalArgumentException("invalid date string $image")
    }
  }

  /**
   * Formats the date according to the given format and locale
   *
   * @param     format  the format. see SimpleDateFormat
   * @param     locale  the locale to use
   */
  open fun format(format: String, locale: Locale = Locale.getDefault()): String {
    val cal = GregorianCalendar()

    cal[Calendar.YEAR] = year
    cal[Calendar.MONTH] = month - 1
    cal[Calendar.DAY_OF_MONTH] = day
    return SimpleDateFormat(format, locale).format(cal.time)
  }

  /**
   * create an instance of calendar to represent date.
   */
  fun toCalendar(): GregorianCalendar {
    val calendar = GregorianCalendar()

    calendar.clear()
    calendar[Calendar.YEAR] = year
    calendar[Calendar.MONTH] = month - 1
    calendar[Calendar.DAY_OF_MONTH] = day
    return calendar
  }
  // ----------------------------------------------------------------------
  // COMPILER METHODS - DO NOT USE OUTSIDE OF THE LIBRARY
  // ----------------------------------------------------------------------
  /**
   * Constructs a Date from a scalar
   * DO NOT USE OUTSIDE OF THE LIBRARY
   */
  internal constructor(scalar: Int) {
    this.scalar = scalar
  }
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * Returns the year of the month (by example 1999 or may be 2000 one year after)
   */
  val year: Int
    get() = julianToGregorian(scalar)[0]

  /**
   * Returns the month number (starts at 1, ends at 12)
   */
  val month: Int
    get() = julianToGregorian(scalar)[1]

  /**
   * Returns the day number (starts at 1, ends at 31)
   */
  val day: Int
    get() = julianToGregorian(scalar)[2]

  /**
   * Returns the day number (starts at 1, ends at 7)
   */
   fun getweekday() : Int {
      synchronized(calendar) {
        val gregorian = julianToGregorian(scalar)

        calendar[Calendar.YEAR] = gregorian[0]
        calendar[Calendar.MONTH] = gregorian[1] - 1
        calendar[Calendar.DAY_OF_MONTH] = gregorian[2]
        return calendar[Calendar.DAY_OF_WEEK]
      }
    }
  // ----------------------------------------------------------------------
  // ARITHMETIC OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Returns a Date with the specified number of days added to this Date.
   */
  fun add(days: Int): NotNullDate {
    return NotNullDate(scalar + days)
  }

  /**
   * Returns the number of days between two dates.
   */
  fun subtract(other: Date?): Int? {
    return if (other == null) null else subtract(other as NotNullDate)
  }

  /**
   * Returns the number of days between two dates.
   */
  fun subtract(other: NotNullDate): Int {
    return scalar - (other as Date).scalar
  }
  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean {
    return other is Date && scalar == other.scalar
  }

  /**
   * Compares to another date.
   *
   * @param    other    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  operator fun compareTo(other: Date): Int {
    val v1 = scalar
    val v2 = other.scalar

    return if (v1 < v2) -1 else if (v1 > v2) 1 else 0
  }

  override operator fun compareTo(other: Any?): Int {
    return compareTo(other as Date)
  }

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  override fun toString(locale: Locale): String {
    val gregorian = julianToGregorian(scalar)

    // !!! taoufik 20061010
    // LOCALIZATION NOT HANDLED
    return buildString {
      append(gregorian[2] / 10)
      append(gregorian[2] % 10)
      append('.')
      append(gregorian[1] / 10)
      append(gregorian[1] % 10)
      append('.')
      append(gregorian[0])
    }
  }

  /**
   * Represents the value in sql
   */
  override fun toSql(): String {
    val gregorian = julianToGregorian(scalar)

    return buildString {
      append("{d '")
      append(gregorian[0])
      append('-')
      append(gregorian[1] / 10)
      append(gregorian[1] % 10)
      append('-')
      append(gregorian[2] / 10)
      append(gregorian[2] % 10)
      append("'}")
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  var scalar = 0

  companion object {
    /**
     * Today's date
     */
    fun now(): NotNullDate {
      val now = Calendar.getInstance()

      return NotNullDate(now[Calendar.YEAR],
              now[Calendar.MONTH] + 1,
              now[Calendar.DAY_OF_MONTH])
    }

    /**
     * Parse the string to build the corresponding date using the
     * default Locale
     *
     * @param     input   the date to parse
     * @param     format  the format of the date
     */
    fun parse(input: String, format: String): NotNullDate {
      return parse(input, format, Locale.getDefault())
    }

    /**
     * Parse the string to build the corresponding date
     *
     * @param     input   the date to parse
     * @param     format  the format of the date
     * @param     locale  the Locale to use
     */
    fun parse(
      input: String,
      format: String,
      locale: Locale,
    ): NotNullDate {
      val cal = GregorianCalendar()

      try {
        cal.time = SimpleDateFormat(format, locale).parse(input)
      } catch (e: ParseException) {
        e.printStackTrace()
        throw IllegalArgumentException(e.message)
      }
      return NotNullDate(cal[Calendar.YEAR],
              cal[Calendar.MONTH] + 1,
              cal[Calendar.DAY_OF_MONTH])
    }

    // --------------------------------------------------------------------
    // JULIAN DATES
    // --------------------------------------------------------------------
    /*
   * NOTE:
   * Gregorian calendar dates are converted to the corresponding Julian
   * day number according to Algorithm 199 from
   * Communications of the ACM, Volume 6, No. 8, (Aug. 1963), p. 444.
   * Gregorian calendar started on Sep. 14, 1752.
   * The corresponding function are not valid before that.
   */
    /**
   * Returns the julian day number of the date specified by year, month, day
   */
    private fun gregorianToJulian(y: Int, m: Int, d: Int): Int {
      var y = y
      var m = m
      val c: Int

      if (m > 2) m -= 3 else {
        m += 9
        y -= 1
      }
      c = y / 100
      y %= 100
      return (146097 * c shr 2) + (1461 * y shr 2) + (153 * m + 2) / 5 + d + 1721119
    }

    /**
   * Returns the date specified by a julian day number as year, month, day.
   */
    private fun julianToGregorian(julian: Int): IntArray {
      var y: Int
      var m: Int
      var d: Int
      var j: Int = julian - 1721119

      y = ((j shl 2) - 1) / 146097
      j = (j shl 2) - 1 - 146097 * y
      d = j shr 2
      j = ((d shl 2) + 3) / 1461
      d = (d shl 2) + 3 - 1461 * j
      d = d + 4 shr 2
      m = (5 * d - 3) / 153
      d = 5 * d - 3 - 153 * m
      d = (d + 5) / 5
      y = 100 * y + j
      if (m < 10) m += 3 else {
        m -= 9
        y += 1
      }
      return intArrayOf(y, m, d)
    }

    private val calendar = GregorianCalendar()
    private val ORIGIN = NotNullDate(1970, 1, 1)
    val DEFAULT = NotNullDate(0)

    init {
      calendar.minimalDaysInFirstWeek = 4
    }
  }
}
