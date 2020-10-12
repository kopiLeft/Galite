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

import java.util.Calendar

open class Date {

  constructor(year: Int, month: Int, day: Int) {
    TODO()
  }

  constructor(scalar: Int) {
    TODO()
  }

  fun compareTo(other: Date): Int {
    TODO()
  }

  constructor(date: java.sql.Date) {
    TODO()
  }

  constructor(image: String) {
    TODO()
  }

  // ----------------------------------------------------------------------
  // ARITHMETIC OPERATIONS
  // ----------------------------------------------------------------------
  open fun add(days: Int): NotNullDate {
    TODO()
  }

  /*package*/
  constructor(calendar: Calendar) {
    TODO()
  }

  /**
   * Returns the month number (starts at 1, ends at 12)
   */
  open fun getMonth(): Int {
    TODO()
  }

  /**
   * Returns the year of the month (by example 1999 or may be 2000 one year after)
   */
  open fun getYear(): Int {
    TODO()
  }

  /**
   * Returns the day number (starts at 1, ends at 31)
   */
  open fun getDay(): Int {
    TODO()
  }
  fun format(s: String): Any = TODO()

  companion object {

    open fun now(): NotNullDate {
      TODO()
    }
  }
}
