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
package org.kopi.galite.ui.vaadin.field

import java.text.SimpleDateFormat
import java.util.Date

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
      field.value = toTimestamp(Date())
    }
  }

  /**
   * Converts the given time stamp to its string representation.
   * @param date The date to be converted.
   * @return The string representation of the equivalent time stamp.
   */
  private fun toTimestamp(date: Date): String =
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
}
