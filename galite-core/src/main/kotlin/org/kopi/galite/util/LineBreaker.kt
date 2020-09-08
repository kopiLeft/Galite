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

package org.kopi.galite.util

import java.text.BreakIterator

import org.kopi.galite.util.base.Utils

class LineBreaker : Utils() {
  companion object {
    /**
     * Replaces new-lines by blanks
     *
     * @param        source        the source text with carriage return
     * @param        col        the width of the text
     * @param        lin        number of lines
     */
    fun textToModel(source: String, col: Int, lin: Int): String {
      return textToModel(source, col, lin, false)
    }

    /**
     * Replaces new-lines by blanks
     *
     * @param        source        the source text with carriage return
     * @param        col        the width of the text
     * @param        fixed        is it a fixed text ?
     */
    fun textToModel(source: String,
                    col: Int,
                    lin: Int,
                    fixed: Boolean): String = buildString {
      val length = source.length
      var start = 0
      var lines = 0
      while (start < length && lines < lin) {
        val index = source.indexOf('\n', start)
        if (index == -1) {
          append(source.substring(start, length))
          start = length
        } else {
          append(source.substring(start, index))
          if (fixed) {
            for (i in index - start until col) {
              append(' ')
            }
          } else {
            var i = (index - start) % col
            while (i != 0 && i < col) {
              append(' ')
              i++
            }
          }
          start = index + 1
          lines++
        }
      }
    }

    /**
     * Replaces blanks by new-lines
     *
     * @param        source                the source text with white space
     * @param        col                the width of the text area
     */
    fun modelToText(source: String?, col: Int): String? = buildString {
      return if (source != null) {
        val length = source.length
        for (start in 0 until length step col) {
          val line = source.substring(start, Math.min(start + col, length))
          var last = -1
          var i = line.length - 1
          while (last == -1 && i >= 0) {
            if (!line[i].isWhitespace()) {

              last = i
            }
            --i
          }
          if (start != 0) {
            append('\n')
          }
          if (last != -1) {
            append(line.substring(0, last + 1))
          }
        }
        toString()
      } else {
        ""
      }
    }

    fun addBreakForWidth(sourceText: String?, width: Int): String {
      val source = sourceText.orEmpty()
      val boundary = BreakIterator.getLineInstance()
      boundary.setText(source)
      var start = boundary.first()
      var length = 0

      return buildString(source.length) {
        var end = boundary.next()
        while (end != BreakIterator.DONE) {
          length += end - start
          if (length > width) {
            length = end - start
            append("\n")
          }
          append(source.substring(start, end))
          if (source.substring(start, end).endsWith("\n")) {
            length = 0
          }
          start = end
          end = boundary.next()
        }
      }
    }

    /**
     * Splits specified string into an array of strings where each element
     * represents a single line fitting the specified width (except if a
     * single word is longer than the specified width.
     */
    fun splitForWidth(source: String?, width: Int): Array<String?> {
      return addBreakForWidth(source, width).split("\n".toRegex()).toTypedArray()
    }
  }
}
