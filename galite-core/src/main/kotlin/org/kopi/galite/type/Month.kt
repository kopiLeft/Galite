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
import java.util.Locale
import java.util.GregorianCalendar
import java.util.Calendar

/**
 * This class represents month types
 */
open class Month internal constructor(year: Int, month: Int) : Type() {
  /**
   * Constructs a Month from a Date
   */
  internal constructor(date: Date) : this(date.year, date.month)

  /**
   * Formats the month according to the given format using the default
   * Locale
   *
   * @param     format  the format.
   * @param     locale  the Locale to use.
   * @see            SimpleDateFormat
   */
  fun format(format: String, locale: Locale = Locale.getDefault()): String {
    val cal = GregorianCalendar()

    cal[Calendar.YEAR] = getYear()
    cal[Calendar.MONTH] = getMonth() - 1
    cal[Calendar.DAY_OF_MONTH] = 1
    return SimpleDateFormat(format, locale).format(cal.time)
  }

  /**
   * Clones this object.
   */
  fun copy(): NotNullMonth = NotNullMonth(scalar / 12, scalar % 12 + 1)

  // ----------------------------------------------------------------------
  // IN PLACE OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Adds the specified number of months to this month.
   */
  fun addTo(months: Int) {
    scalar += months
  }

  // ----------------------------------------------------------------------
  // DEFAULT OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Returns a Month with the specified number of months added to this Month.
   */
  fun add(months: Int): NotNullMonth {
    return NotNullMonth((scalar + months) / 12, (scalar + months) % 12 + 1)
  }

  /**
   * subtract
   * @returns the number of month between two Months
   */
  fun subtract(other: Month?): Int? = other?.let { subtract(other as? NotNullMonth) }

  /**
   * subtract
   * @returns the number of month between two Months
   */
  fun subtract(other: NotNullMonth): Int = scalar - (other as Month).scalar

  // ----------------------------------------------------------------------
  // OTHER OPERATIONS
  // ----------------------------------------------------------------------
  /**
   * Compares to another month.
   *
   * @param    other    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  operator fun compareTo(other: Month): Int {
    val v1 = scalar
    val v2 = other.scalar

    return if (v1 < v2) -1 else if (v1 > v2) 1 else 0
  }

  override operator fun compareTo(other: Any?): Int = compareTo(other as? Month)

  /**
   * Returns the year of the month (by example 1999 or may be 2000 on year after)
   */
  open fun getYear(): Int = scalar / 12

  /**
   * Returns the month number (starts at 1, ends at 12)
   */
  open fun getMonth(): Int = scalar % 12 + 1 // month to start at 1

  /**
   * Returns the first day of this month.
   */
  open fun getFirstDay(): NotNullDate = NotNullDate(scalar / 12, scalar % 12 + 1, 1)

  /**
   * Returns the last day of this month.
   */
  open fun getLastDay(): NotNullDate =
          // this is the first day of the next month - 1 day.
          NotNullDate((scalar + 1) / 12, (scalar + 1) % 12 + 1, 1).add(-1)

  /**
   * Transforms this month in a date (the first day of the month)
   */
  @Deprecated("")
  open fun getDate(): NotNullDate = getFirstDay()

  // ----------------------------------------------------------------------
  // TYPE IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Compares two objects
   */
  override fun equals(other: Any?): Boolean = other is Month? && scalar == other?.scalar

  /**
   * Format the object depending on the current language
   * @param    locale    the current language
   */
  override fun toString(locale: Locale): String {
    val year = scalar / 12
    val month = scalar % 12 + 1

    return buildString {
      append(month / 10)
      append(month % 10)
      append('.')
      append(year)
    }
  }

  /**
   * Represents the value in sql
   */
  override fun toSql(): String {
    val year = scalar / 12
    val month = scalar % 12 + 1

    return "{fn MONTH($year, $month)}"
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var scalar: Int = year * 12 + month - 1

  companion object {
    /**
     * Current month
     */
    fun now(): NotNullMonth {
      val now = Calendar.getInstance()
      return NotNullMonth(now[Calendar.YEAR], now[Calendar.MONTH] + 1)
    }

    /**
     * Parse the string to build the corresponding month using the
     * default locale
     *
     * @param     input   the date to parse
     * @param     format  the format of the date
     */
    fun parse(input: String, format: String): NotNullMonth = parse(input, format, Locale.getDefault())


    /**
     * Parse the string to build the corresponding month using the given
     * Locale
     *
     * @param     input   the date to parse
     * @param     format  the format of the date
     * @param     locale  the Locale to use
     */
    fun parse(input: String, format: String, locale: Locale): NotNullMonth {
      val cal = GregorianCalendar()

      try {
        cal.time = SimpleDateFormat(format, locale).parse(input)
      } catch (e: ParseException) {
        throw IllegalArgumentException()
      }
      return NotNullMonth(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1)
    }

    // --------------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------------
    val DEFAULT: NotNullMonth = NotNullMonth(1900, 1)
  }
}
