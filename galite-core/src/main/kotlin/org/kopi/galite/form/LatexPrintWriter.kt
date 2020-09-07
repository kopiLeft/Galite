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

package org.kopi.galite.form

import java.io.PrintWriter
import java.io.Writer

/**
 * This class implements a special printwriter that map source line number 2 it
 *
 * Construct a new LatexPrintWriter. This object translate special chars into latex syntax
 *
 * @param w the writer to filter
 */
class LatexPrintWriter(w: Writer?) : PrintWriter(w) {
  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  fun uncheckedPrintln(src: String?) {
    super.print(src)
    super.println()
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  fun uncheckedPrint(src: String?) {
    super.print(src)
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  fun printItem(src: String) {
    super.print("\\item[")
    print(src)
    super.print("]")
    super.println()
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  override fun println(src: String) {
    print(src)
    super.println()
  }

  /**
   * check that line number match, else print enough '\n'
   * @param p the buffered printwriter on which we write code
   */
  override fun print(src: String) {
    super.print(convert(src))
  }

  companion object {
    fun convert(src: String): String = buildString {
      src.forEach {
        when (it) {
          '&' -> append("\\&")
          '%' -> append("\\%")
          '(' -> append("$($")
          ')' -> append("$)$")
          '[' -> append("$[$")
          ']' -> append("$]$")
          '<' -> append("$<$")
          '>' -> append("$>$")
          '_' -> append("\\_")
          '#' -> append("\\#")
          'ü' -> append("\\\"{u}")
          'ä' -> append("\\\"{a}")
          'ö' -> append("\\\"{o}")
          'Ä' -> append("\\\"{A}")
          'Ö' -> append("\\\"{O}")
          'Ü' -> append("\\\"{U}")
          'ß' -> append("{\\ss}")
          else -> append(it)
        }
      }
    }
  }
}
