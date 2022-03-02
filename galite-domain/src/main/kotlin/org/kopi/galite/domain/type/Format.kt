/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.domain.type

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Format date on
 */
fun LocalDate.format(): String {
  return format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}

/**
 * Format the localtime value.
 */
fun LocalTime.format(): String {
  return format(DateTimeFormatter.ofPattern("HH:mm"))
}

fun BigDecimal.format(): String {
  val str = this.toString()
  var pos = 0
  var dot: Int

  return buildString {
    // has minus sign ?
    if (str[0] == '-') {
      append('-')
      pos = 1
    }

    // get number of digits in front of the dot
    if (str.indexOf('.').also { dot = it } == -1) {
      if (str.indexOf(' ').also { dot = it } == -1) {        // FRACTION DOT IS SPACE
        dot = str.length
      }
    }
    if (dot - pos <= 3) {
      append(str.substring(pos, dot))
      pos = dot
    } else {
      when ((dot - pos) % 3) {
        1 -> {
          append(str.substring(pos, pos + 1))
          pos += 1
        }
        2 -> {
          append(str.substring(pos, pos + 2))
          pos += 2
        }
        0 -> {
          append(str.substring(pos, pos + 3))
          pos += 3
        }
      }
      do {
        append(".").append(str.substring(pos, pos + 3))
        pos += 3
      } while (dot - pos > 0)
    }
    if (str.length > pos) {
      append(",").append(str.substring(pos + 1))
    }
  }
}
