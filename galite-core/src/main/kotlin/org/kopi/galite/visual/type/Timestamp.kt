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

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant

/**
 * This class represents kopi timestamp types
 */
class Timestamp(val sqlTimestamp: java.sql.Timestamp) : Type<Timestamp, Instant>() {

  constructor(string: String) : this(java.sql.Timestamp.valueOf(string))

  constructor(millis: Long) : this(java.sql.Timestamp(millis))

  constructor(calendar: Calendar) : this(java.sql.Timestamp(calendar.timeInMillis))

  /**
   * Formats the timestamp according to the given format and locale
   *
   * @param     format  the format. see SimpleDateFormat
   * @param     locale  the locale to use
   */
  fun format(format: String, locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(format, locale).format(sqlTimestamp)
  }

  /**
   * create an instance of calendar to represent timestamp.
   */
  fun toCalendar(): GregorianCalendar {
    val calendar = GregorianCalendar()

    calendar.clear()
    calendar.timeInMillis = sqlTimestamp.time
    return calendar
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------
  fun add(millis: Long): Timestamp {
    return Timestamp(sqlTimestamp.time + millis)
  }
  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Compares to another time.
   *
   * @param        other        the second operand of the comparison
   * @return        -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  override operator fun compareTo(other: Timestamp): Int {
    return sqlTimestamp.compareTo(other.sqlTimestamp)
  }

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean {
    return other is Timestamp &&
            other.sqlTimestamp.equals(sqlTimestamp)
  }

  /**
   * Format the object depending on the current language
   * @param        locale        the current language
   */
  override fun toString(locale: Locale): String {
    val tmp = StringBuffer(normal.format(sqlTimestamp))
    val nanos = sqlTimestamp.nanos
    when {
      nanos >= 100 -> {
        tmp.append(nanos)
      }
      nanos >= 10 -> {
        tmp.append("0$nanos")
      }
      else -> {
        tmp.append("00$nanos")
      }
    }
    return tmp.toString()
  }

  /**
   * Represents the value in sql
   */
  override fun toSql(): Instant {
    return sqlTimestamp.toInstant()
  }

  override fun hashCode(): Int {
    return sqlTimestamp.hashCode()
  }

  companion object {
    /**
     * now's timestamp
     */
    fun now(): Timestamp {
      return Timestamp(System.currentTimeMillis())
    }

    /**
     * Parse the string to build the corresponding timestamp using the
     * default Locale
     *
     * @param     input   the timestamp to parse
     * @param     format  the format of the timestamp
     */
    fun parse(input: String, format: String): Timestamp {
      return parse(input, format, Locale.getDefault())
    }

    /**
     * Parse the string to build the corresponding timestamp
     *
     * @param     input   the timestamp to parse
     * @param     format  the format of the timestamp
     * @param     locale  the Locale to use
     */
    fun parse(
            input: String,
            format: String,
            locale: Locale,
    ): Timestamp {
      val cal = GregorianCalendar()
      try {
        cal.time = SimpleDateFormat(format, locale).parse(input)
      } catch (e: ParseException) {
        e.printStackTrace()
        throw IllegalArgumentException(e.message)
      }
      return Timestamp(cal.timeInMillis)
    }

    // --------------------------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------------------------
    val DEFAULT = Timestamp(0)
    private val normal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  }
}
