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

/**
 * This class represents the time types
 */
open class Time : Type<Time> {
  constructor(hours: Int, minutes: Int, seconds: Int = 0) {
    scalar = (hours * 3600 + minutes * 60 + seconds) % (3600 * 24)
  }

  constructor(time: java.sql.Time) {
    var hours: Int
    var minutes: Int
    var seconds: Int

    synchronized(calendar) {
      calendar.time = time
      hours = calendar[Calendar.HOUR_OF_DAY]
      minutes = calendar[Calendar.MINUTE]
      seconds = calendar[Calendar.SECOND]
    }
    scalar = (hours * 3600 + minutes * 60 + seconds) % (3600 * 24)
  }

  constructor(string: String) : this(java.sql.Time.valueOf(string))

  constructor(calendar: Calendar?) {
    if (calendar != null) {
      val hours: Int = calendar[Calendar.HOUR_OF_DAY]
      val minutes: Int = calendar[Calendar.MINUTE]
      val seconds: Int = calendar[Calendar.SECOND]

      scalar = (hours * 3600 + minutes * 60 + seconds) % (3600 * 24)
    }
  }

  /**
   * Formats the time according to the given format and locale
   *
   * @param     format  the format. see SimpleDateFormat
   * @param     locale  the locale to use
   */
  @JvmOverloads
  fun format(format: String, locale: Locale = Locale.getDefault()): String {
    val cal = GregorianCalendar()

    cal[Calendar.HOUR_OF_DAY] = hours
    cal[Calendar.MINUTE] = minutes
    cal[Calendar.SECOND] = seconds
    return SimpleDateFormat(format).format(cal.time)
  }

  /**
   * create an instance of Calendar to represent time.
   */
  fun toCalendar(): GregorianCalendar {
    val calendar = GregorianCalendar()

    calendar.clear()
    calendar[Calendar.HOUR_OF_DAY] = hours
    calendar[Calendar.MINUTE] = minutes
    calendar[Calendar.SECOND] = seconds
    return calendar
  }

  /**
   * Constructs a time from a scalar
   */
  internal constructor(scalar: Int) {
    this.scalar = scalar % (3600 * 24)
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------
  fun add(seconds: Int): Time {
    return Time(scalar + seconds)
  }

  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Compares to another time.
   *
   * @param    other    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  override operator fun compareTo(other: Time): Int {
    val v1 = scalar
    val v2 = other.scalar

    return if (v1 < v2) -1 else if (v1 > v2) 1 else 0
  }

  /**
   * Returns the hour represented by this object.
   */
  val hours: Int
    get() = scalar / 3600

  /**
   * Returns the minutes past the hour represented by this object.
   */
  val minutes: Int
    get() = scalar / 60 % 60

  /**
   * Returns the seconds past the minute represented by this object.
   */
  val seconds: Int
    get() = scalar % 60

  // !!! TO BE REMOVED
  fun getSqlTime(): java.sql.Time {
    synchronized(calendar) {
      calendar[Calendar.HOUR_OF_DAY] = scalar / 3600
      calendar[Calendar.MINUTE] = scalar / 60 % 60
      calendar[Calendar.SECOND] = scalar % 60
      return java.sql.Time(calendar.time.time)
    }
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean = other is Time && other.scalar == scalar

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  override fun toString(locale: Locale): String {
    val hours = scalar / 3600
    val minutes = scalar / 60 % 60

    return buildString {
      append(hours / 10)
      append(hours % 10)
      append(':')
      append(minutes / 10)
      append(minutes % 10)
    }
  }

  /**
   * Represents the value in sql
   * TODO: Time is not supported yet
   */
  override fun toSql(): String {
    val hours = scalar / 3600
    val minutes = scalar / 60 % 60
    val seconds = scalar % 60

    return buildString {
      append(hours / 10)
      append(hours % 10)
      append(':')
      append(minutes / 10)
      append(minutes % 10)
      append(':')
      append(seconds / 10)
      append(seconds % 10)
    }
  }

  override fun hashCode(): Int {
    return scalar
  }

  /**
   * Sets the base value for this object
   */
  internal var scalar = 0

  companion object {
    /**
     * Current time
     */
    fun now(): Time {
      val now = Calendar.getInstance()
      return Time(now[Calendar.HOUR_OF_DAY],
                  now[Calendar.MINUTE],
                  now[Calendar.SECOND])
    }

    /**
     * Parse the string to build the corresponding time using the
     * default Locale
     *
     * @param     input   the time parse
     * @param     format  the format of the date
     */
    fun parse(input: String, format: String): Time = parse(input, format, Locale.getDefault())

    /**
     * Parse the string to build the corresponding time using the
     * default Locale
     *
     * @param     input   the time parse
     * @param     format  the format of the date
     * @param     locale  the Locale to use
     */
    fun parse(input: String, format: String, locale: Locale): Time {
      val cal = GregorianCalendar()

      try {
        cal.time = SimpleDateFormat(format, locale).parse(input)
      } catch (e: ParseException) {
        throw IllegalArgumentException()
      }
      return Time(cal[Calendar.HOUR_OF_DAY],
                  cal[Calendar.MINUTE],
                  cal[Calendar.SECOND])
    }

    private val calendar = GregorianCalendar()

    // --------------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------------
    val DEFAULT = Time(0, 0, 0)
  }
}
