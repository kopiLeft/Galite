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

/**
 * Validator for string fields.
 */
class StringValidator(
        private val width: Int,
        private val height: Int,
        private val fixedNewLine: Boolean,
        private val convertType: TextField.ConvertType,
        maxLength: Int
) : AllowAllValidator(maxLength) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun checkType(field: InputTextField<*>, text: String) {
    var text: String? = text
    if (text == null || "" == text) {
      field.value = null
    } else {
      when (convertType) {
        TextField.ConvertType.NONE -> {
        }
        TextField.ConvertType.UPPER -> text = text.toUpperCase()
        TextField.ConvertType.LOWER -> text = text.toLowerCase()
        TextField.ConvertType.NAME -> text = convertName(text)
        else -> throw RuntimeException()
      }
      if (!checkText(text)) {
        throw CheckTypeException(field, "00013")
      }
      field.value = text
    }
  }

  /**
   * Returns the convert type of this validator.
   * @return The convert type of this validator.
   */
  fun getConvertType(): TextField.ConvertType {
    return convertType
  }

  /**
   * Convert the first letter in each word in the source text into upper case.
   *
   * @param     source          the source text.
   */
  private fun convertName(source: String): String {
    val chars = source.toLowerCase().toCharArray()
    var found = false
    for (i in chars.indices) {
      if (!found && Character.isLetter(chars[i])) {
        chars[i] = Character.toUpperCase(chars[i])
        found = true
      } else if (isWhitespace(chars[i])) {
        found = false
      }
    }
    return String(chars)
  }

  /**
   * Checks the given text against field constraints.
   * @param text The text to be checked.
   * @return `true` if the text is valid.
   */
  private fun checkText(text: String?): Boolean {
    var end = 0
    end = textToModel(text, width, Int.MAX_VALUE, fixedNewLine).length
    return end <= width * height
  }

  companion object {
    /**
     * Replaces new-lines by blanks
     *
     * @param     source  the source text with carriage return
     * @param     col     the width of the text
     * @param     fixed   is it a fixed text ?
     */
    fun textToModel(source: String?, col: Int, lin: Int, fixed: Boolean): String {
      val target = StringBuffer()
      val length = source!!.length
      var start = 0
      var lines = 0
      while (start < length && lines < lin) {
        val index = source.indexOf('\n', start)
        if (index == -1) {
          target.append(source.substring(start, length))
          start = length
        } else {
          target.append(source.substring(start, index))
          if (fixed) {
            for (i in index - start until col) {
              target.append(' ')
            }
          } else {
            var i = (index - start) % col
            while (i != 0 && i < col) {
              target.append(' ')
              i++
            }
          }
          start = index + 1
          lines++
        }
      }
      return target.toString()
    }

    /**
     * Checks if the given character is a white space.
     * The implementation is picked from java implementation
     * since GWT does not contains the implementation of [Character.isWhitespace]
     * @param c The concerned character.
     * @return `true` if the character is whitespace.
     */
    fun isWhitespace(c: Char): Boolean {
      return c == ' ' || c == '\u00A0' // SPACE_SEPARATOR
              || c == '\u2007' // LINE_SEPARATOR
              || c == '\u202F' // PARAGRAPH_SEPARATOR
              || c == '\u0009' // HORIZONTAL TABULATION.
              || c == '\n' // LINE FEED.
              || c == '\u000B' // VERTICAL TABULATION.
              || c == '\u000C' // FORM FEED.
              || c == '\r' // CARRIAGE RETURN.
              || c == '\u001C' // FILE SEPARATOR.
              || c == '\u001D' // GROUP SEPARATOR.
              || c == '\u001E' // RECORD SEPARATOR.
              || c == '\u001F' // UNIT SEPARATOR.
    }
  }
}
