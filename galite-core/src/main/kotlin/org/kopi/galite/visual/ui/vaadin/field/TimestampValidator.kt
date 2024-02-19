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
package org.kopi.galite.visual.ui.vaadin.field

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import org.kopi.galite.type.format

/**
 * Time stamp validator
 */
class TimestampValidator(maxLength: Int) : AllowAllValidator(maxLength) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun checkType(field: InputTextField<*>, text: String) {
    if ("" == text) {
      field.value = null
    } else {
      field.value = parseTimestamp(text)?.format()
    }
  }

  /**
   * Converts the given time stamp to its string representation.
   * @param date The date to be converted.
   * @return The string representation of the equivalent time stamp.
   */
  private fun toTimestamp(date: java.util.Date): String =
          buildString {
            var nanos = (date.time % 1000 * 1000000).toInt()
            if (nanos < 0) {
              nanos += 1000000000
              date.time = (date.time / 1000 - 1) * 1000
            }
            append(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date))
            when {
              nanos >= 100 -> {
                append(nanos)
              }
              nanos >= 10 -> {
                append("0$nanos")
              }
              else -> {
                append("00$nanos")
              }
            }
          }


  companion object {

    /**
     * Parses the given timestamp input.
     * @param text The timestamp text.
     */
    fun parseTimestamp(text: String): LocalDateTime? {
      val timestamp = text.split("[ T]".toRegex(), 2)
      val date = parseDate(timestamp[0]) ?: return null
      val time = parseTime(timestamp[1]) ?: return null

      return LocalDateTime.of(date, time)
    }

    fun parseDate(date: String): LocalDate? {
      // Date check
      var month = 0
      var year = -2

      val tokens = date.split("[#./-]".toRegex())
      if (tokens.isEmpty()) {
        return null
      }

      val day = DateValidator.stringToInt(tokens[0])

      if (tokens.size >= 2) {
        month = DateValidator.stringToInt(tokens[1])
      }
      if (tokens.size >= 3) {
        year = DateValidator.stringToInt(tokens[2])
      }
      if (tokens.size > 3 || day == -1 || month == -1 || year == -1) {
        return null
      }
      when {
        month == 0 -> {
          val now = LocalDate.now()
          month = now.monthValue
          year = now.year
        }
        year == -2 -> {
          val now = LocalDate.now()
          year = now.year
        }
        year < 50 -> {
          year += 2000
        }
        year < 100 -> {
          year += 1900
        }
        year < 1000 -> {
          // less than 4 digits cause an error in database while paring the
          // sql statement
          return null
        }
      }
      if (!DateValidator.isDate(day, month, year)) {
        return retryParseDate(tokens)
      }

      return LocalDate.of(year, month, day)
    }

    private fun retryParseDate(tokens: List<String>): LocalDate? {
      var day = 0
      var month = 0
      var year = DateValidator.stringToInt(tokens[0])
      if (tokens.size >= 2) {
        month = DateValidator.stringToInt(tokens[1])
      }
      if (tokens.size >= 3) {
        day = DateValidator.stringToInt(tokens[2])
      }
      if (tokens.size > 3 || day == -1 || month == -1 || year == -1) {
        return null
      }
      when {
        month == 0 -> {
          val now = LocalDate.now()
          month = now.monthValue
          year = now.year
        }
        year == -2 -> {
          val now = LocalDate.now()
          year = now.year
        }
        year < 50 -> {
          year += 2000
        }
        year < 100 -> {
          year += 1900
        }
        year < 1000 -> {
          // less than 4 digits cause an error in database while paring the
          // sql statement
          return null
        }
      }
      if (!DateValidator.isDate(day, month, year)) {
        return null
      }

      return LocalDate.of(year, month, day)
    }

    fun parseTime(time: String): LocalTime? {
      var hours = -1
      var minutes = 0
      var seconds = 0
      val buffer = time + '\u0000'
      var bp = 0
      var state = 1

      while (state > 0) {
        when (state) {
          1 -> when {
            buffer[bp] in '0'..'9' -> {
              hours = buffer[bp] - '0'
              state = 2
            }
            buffer[bp] == '\u0000' -> {
              state = 0
            }
            else -> {
              state = -1
            }
          }
          2 -> when {
            buffer[bp] in '0'..'9' -> {
              hours = 10 * hours + (buffer[bp] - '0')
              state = 3
            }
            buffer[bp] == ':' -> {
              state = 4
            }
            buffer[bp] == '\u0000' -> {
              state = 0
            }
            else -> {
              state = -1
            }
          }
          3 -> state = when {
            buffer[bp] == ':' -> {
              4
            }
            buffer[bp] == '\u0000' -> {
              0
            }
            else -> {
              -1
            }
          }
          4 -> when {
            buffer[bp] in '0'..'9' -> {
              minutes = buffer[bp] - '0'
              state = 5
            }
            buffer[bp] == '\u0000' -> {
              state = 0
            }
            else -> {
              state = -1
            }
          }
          5 -> when {
            buffer[bp] in '0'..'9' -> {
              minutes = 10 * minutes + (buffer[bp] - '0')
              state = 6
            }
            buffer[bp] == ':' -> {
              state = 7
            }
            buffer[bp] == '\u0000' -> {
              state = 0
            }
            else -> {
              state = -1
            }
          }
          6 -> state = when {
            buffer[bp] == ':' -> {
              7
            }
            buffer[bp] == '\u0000' -> {
              0
            }
            else -> {
              -1
            }
          }
          7 -> when {
            buffer[bp] in '0'..'9' -> {
              seconds = buffer[bp] - '0'
              state = 8
            }
            buffer[bp] == '\u0000' -> {
              state = 0
            }
            else -> {
              state = -1
            }
          }
          8 ->
            when {
              buffer[bp] in '0'..'9' -> {
                seconds = 10 * seconds + (buffer[bp] - '0')
                state = 9
              }
              buffer[bp] == '\u0000' -> {
                state = 0
              }
              else -> {
                state = -1
              }
            }

          9 -> state = if (buffer[bp] == '\u0000') {
            0
          } else {
            -1
          }
        }
        bp += 1
      }
      if (state == -1) {
        return null
      }
      return if (hours == -1) {
        null
      } else {
        LocalTime.of(hours, minutes, seconds)
      }
    }
  }
}
