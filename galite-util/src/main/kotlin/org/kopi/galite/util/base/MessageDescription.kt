/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util.base

import java.text.MessageFormat

/**
 * This class defines message descriptions (errors, warnings, notices, ...)
 *
 * The message format is a text message with placeholders for its arguments
 * of the form {0}, {1}, ... . Each placeholder will be replaced by the string
 * representation of the corresponding argument.
 */
class MessageDescription( val format: String, val reference: String?, val level: Int) {

   /**
   * Returns a string explaining the error.
   *
   * @param    parameters        the array of parameters
   */
  fun format(parameters: Array<Any?>): String {
    val prefix = when (level) {
      LVL_UNDEFINED -> ""      // no qualifier
      LVL_ERROR -> "error:"
      LVL_CAUTION -> "caution:"
      LVL_WARNING -> "warning:"
      LVL_NOTICE -> "notice:"
      LVL_INFO -> ""
      else ->       // unknown: mark as error
        "error:"
    } // the text for the severity level

    var body = try {
      MessageFormat.format(format, *parameters)
    } catch (e: RuntimeException) {
      // wrong number of parameters: give at least message text with placeholders
      format
    } // the formatted message

    val suffix = if (reference == null) "" else " [$reference]" // the reference

    return prefix + body + suffix
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  companion object {
    const val LVL_UNDEFINED: Int = -1
    const val LVL_ERROR: Int = 0
    const val LVL_CAUTION: Int = 1
    const val LVL_WARNING: Int = 2
    const val LVL_NOTICE: Int = 3
    const val LVL_INFO: Int = 4
  }
}