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
package org.kopi.galite.ui.vaadin.grid

import java.lang.Exception
import java.util.Date

/**
 * Server side implementation of a month editor field.
 * Month field are fixed width field. Its width is 7
 */
class GridEditorMonthField: GridEditorTextField(7) {

  override fun check(text: String): Boolean {
    for (element in text) {
      if (!(element in '0'..'9' || element == '.')) {
        return false
      }
    }
    return true
  }

  override fun validate() {
    val text: String = value
    if (text.indexOf(".") != -1 && text.indexOf(".") == text.lastIndexOf(".")) {
      // one "." and only one
      try {
        val month = text.substring(0, text.indexOf(".")).toInt()
        var year = text.substring(text.indexOf(".") + 1).toInt()
        if (year < 50) {
          year += 2000
        } else if (year < 100) {
          year += 1900
        }
        if (isMonth(month, year)) {
          value = toString(year, month)
        } else {
          throw InvalidEditorFieldException(this, "00005")
        }
      } catch (e: Exception) {
        throw InvalidEditorFieldException(this, "00005")
      }
    } else if (text.indexOf(".") == -1) {
      // just the month, complete
      try {
        val month = text.toInt()
        val year = Date().year + 1900
        if (isMonth(month, year)) {
          value = toString(year, month)
        } else {
          throw InvalidEditorFieldException(this, "00005")
        }
      } catch (e: Exception) {
        throw InvalidEditorFieldException(this, "00005")
      }
    } else {
      throw InvalidEditorFieldException(this, "00005")
    }
  }

  protected fun isNumeric(): Boolean {
    return true
  }

  /**
   * Returns the string representation of the given month.
   * @param year The month year.
   * @param month The month value.
   * @return The string representation of the given month.
   */
  fun toString(year: Int, month: Int): String =
          buildString {
            append(month / 10)
            append(month % 10)
            append('.')
            append(year)
          }

  /**
   * Checks if the given month is valid.
   * @param m The month.
   * @param y The year.
   * @return The `true` if the month is valid.
   */
  private fun isMonth(m: Int, y: Int): Boolean = if (y < 1 || m < 1 || m > 12) false else true
}
