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

/**
 * This class represents date types
 */
class NotNullDate : Date {
  constructor(year: Int, month: Int, day: Int) : super(year, month, day)
  constructor(date: java.sql.Date) : super(date)
  constructor(image: String) : super(image)
  constructor(calendar: Calendar) : super(calendar)

  /**
   * Constructs a Date from a scalar representation.
   * DO NOT USE OUTSIDE OF THE LIBRARY
   */
  constructor(scalar: Int) : super(scalar)

  companion object {
    fun castToNotNull(value: Date): NotNullDate {
      return value as NotNullDate
    }
  }
}
