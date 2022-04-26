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

package org.kopi.galite.util.base

import java.util.Timer
import java.util.TimerTask

/**
 * This class defines several utilities methods used in source code
 */
open class Utils {
  companion object {
    /**
     * Returns a string representation of an integer, padding it
     * with leading zeroes to the specified length.
     */
    fun formatInteger(value: Int, length: Int): String = buildString {
      val valueLength = ("" + value).length
      for (i in length - valueLength downTo 1) {
        append("0")
      }
      append(value)
    }

    /**
     * Check if an assertion is valid
     *
     * @param            expression              The expression to verify
     * @exception        RuntimeException        the entire token reference
     */
    @Deprecated("Use the verify with the error message",
                ReplaceWith("verify(expression = expression, errorMessage = errorMessage)"))
    fun verify(expression: Boolean) {
      if (!expression) {
        throw InconsistencyException()
      }
    }

    /**
     * Check if an assertion is valid
     *
     * @param            expression              The expression to verify
     * @param            errorMessage            The error message to show when [expression] is not valid
     * @exception        RuntimeException        the entire token reference
     */
    fun verify(expression: Boolean, errorMessage: String) {
      if (!expression) {
        throw InconsistencyException(errorMessage)
      }
    }

    /**
     * Splits a string like:
     * "java/lang/System/out"
     * into two strings:
     * "java/lang/System" and "out"
     */
    fun splitQualifiedName(name: String, separator: Char): Array<String> =
            arrayOf(name.substringBeforeLast(separator, ""),
                    name.substringAfterLast(separator))

    /**
     * Splits a string like:
     * "java/lang/System/out"
     * into two strings:
     * "java/lang/System" and "out"
     */
    fun splitQualifiedName(name: String): Array<String> {
      return splitQualifiedName(name, '/')
    }

    /**
     * Returns a substring of this string.
     *
     * Provides a more robust implementation of java.lang.String.substring,
     * handling gracefully the following cases :
     * - the specified string is null
     * - a specified index is beyond the limits of the input string
     */
    fun substring(baseString: String?, beginIndex: Int, endIndex: Int): String =
            baseString?.substring(beginIndex.coerceAtMost(baseString.length),
                                  endIndex.coerceAtMost(baseString.length)).orEmpty()

    /**
     * Creates a typed array from a list.
     *
     * @param        list                the list containing the elements
     */
    fun toArray(list: List<*>): Array<*> = list.toTypedArray()

    /**
     * Executes a task after some delay.
     *
     * @param delay   the delay.
     * @param task    the task to execute.
     */
    fun doAfter(delay: Long, task: () -> Unit) {
      Timer().schedule(
        object : TimerTask() {
          override fun run() {
            task()
          }
        },
        delay
      )
    }
  }
}
