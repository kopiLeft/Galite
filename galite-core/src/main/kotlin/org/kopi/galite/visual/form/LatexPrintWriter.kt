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

package org.kopi.galite.visual.form

import java.io.PrintWriter
import java.io.Writer

/**
 * This class implements a special printwriter that map source line number 2 it
 *
 * Construct a new LatexPrintWriter. This object translate special chars into latex syntax
 *
 * @param writer the writer to filter
 */
class LatexPrintWriter(writer: Writer) : PrintWriter(writer) {
  /**
   * Prints a string and terminates the current line by writing the line separator string.
   * @param src The string to print.
   */
  fun uncheckedPrintln(src: String) {
    super.print(src)
    super.println()
  }

  /**
   * Prints a string.
   * @param src The string to print.
   */
  fun uncheckedPrint(src: String) {
    super.print(src)
  }

  /**
   * Prints an item. it adds the 'item' keyword to [src] and writes the line separator to the end.
   * @param src The string to print.
   */
  fun printItem(src: String?) {
    super.print("\\item[")
    print(src)
    super.print("]")
    super.println()
  }

  /**
   * Prints an item. it adds the 'item' keyword to [src] and writes the line separator to the end.
   * @param src The string to print.
   */
  override fun println(src: String) {
    print(src)
    super.println()
  }

  /**
   * prints the string. It converts the string inorder to escape special characters.
   * @param src The string to print.
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
