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

package org.kopi.galite.base

import java.text.ChoiceFormat
import java.text.Format
import java.text.MessageFormat
import java.text.ParsePosition
import java.util.ArrayList
import java.util.Arrays
import java.util.Locale

class ExtendedMessageFormat : MessageFormat {
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  constructor(pattern: String) : super(pattern)
  constructor(pattern: String, locale: Locale) : super(pattern, locale)

  /**
   * Formats an object to produce a string. This is equivalent to
   * <blockquote>
   * [format][.format]`(obj,
   * new StringBuffer(), new FieldPosition(0)).toString();`
  </blockquote> *
   *
   * @param obj    The object to format
   * @return       Formatted string.
   * @exception IllegalArgumentException if the Format cannot format the given
   * object
   */
  fun formatMessage(obj: Array<Any?>?): String = format(toNullRepresentation(obj))
          .toString()
          .replace("null".toRegex(), "")

  /**
   * Transforms the internal elements of the given array to their
   * null representation if the element is null.
   * @param obj The source object array.
   * @return The transformed object array or null if the source object
   * is already null.
   */
  protected fun toNullRepresentation(obj: Array<Any?>?): Array<Any?>? {
    val newObjects: Array<Any?>?
    if (obj == null) {
      newObjects = null
    } else {
      newObjects = arrayOfNulls(obj.size)

      // replace null by null representation object to allow
      // format execution since this behavior cannot be hacked
      for (i in newObjects.indices) {
        if (obj[i] == null) {
          newObjects[i] = NULL_REPRESENTATION
        } else {
          newObjects[i] = obj[i]
        }
      }
    }
    return newObjects
  }

  /*
 * (non-Javadoc)
 * @see java.text.MessageFormat#applyPattern(java.lang.String)
 */
  override fun applyPattern(pattern: String) {
    val descriptions: MutableList<FormatDescription>
    descriptions = ArrayList()
    val customPattern: StringBuilder = StringBuilder(pattern.length)
    val position: ParsePosition = ParsePosition(0)
    val patternChars: CharArray = pattern.toCharArray()
    var fmtCount: Int = 0
    while (position.index < pattern.length) {
      when (patternChars[position.index]) {
        QUOTE -> appendQuotedString(pattern, position, customPattern)
        START_FE -> {
          val index: Int
          val formatDescription: String?
          fmtCount++
          seekNonWs(pattern, position)
          val start: Int = position.index
          val argumentInfo: ArgumentInfo = readArgumentInfo(pattern, next(position))
          index = argumentInfo.index
          customPattern.append(START_FE).append(index)
          seekNonWs(pattern, position)
          if (patternChars[position.index] == START_FMT) {
            formatDescription = parseFormatDescription(pattern, next(position))
            customPattern.append(START_FMT).append(formatDescription)
          } else {
            formatDescription = null
          }
          descriptions.add(FormatDescription(formatDescription, argumentInfo))
          require(descriptions.size == fmtCount) { "The validated expression is false" }
          require(patternChars[position.index] == END_FE) { "Unreadable format element at position $start" }
          customPattern.append(patternChars[position.index])
          next(position)
        }
        else -> {
          customPattern.append(patternChars[position.index])
          next(position)
        }
      }
    }
    super.applyPattern(customPattern.toString())
    // use extended choice format to handle specific use
    useExtendedChoiceFormat(descriptions)
  }

  /**
   * Forces to use extended choice format to handle null test in a standard
   * choice format pattern.
   */
  private fun useExtendedChoiceFormat(formatDescriptions: List<FormatDescription>) {
    val newFormats = arrayOfNulls<Format>(formats.size)
    for (i in formats.indices) {
      if (formats[i] is ChoiceFormat) {
        var description: FormatDescription?
        var pattern: String = (formats[i] as ChoiceFormat).toPattern()
        description = getChoiceFormatDescription(pattern, formatDescriptions)
        // use extended choice format instead
        newFormats[i] = ExtendedChoiceFormat(pattern, description?.info?.hasNotNullMarker ?: false)
      } else {
        newFormats[i] = formats[i]
      }
    }
    formats = newFormats
  }

  /**
   * Read the argument index from the current format element.
   *
   * @param pattern pattern to parse
   * @param pos current parse position
   * @return argument index
   */
  private fun readArgumentInfo(pattern: String, pos: ParsePosition): ArgumentInfo {
    var error: Boolean
    var hasNotNullMarker: Boolean
    val start: Int = pos.index
    seekNonWs(pattern, pos)
    val result: StringBuilder = StringBuilder()
    error = false
    hasNotNullMarker = false
    while (!error && pos.index < pattern.length) {
      var c = pattern[pos.index]
      if (Character.isWhitespace(c)) {
        seekNonWs(pattern, pos)
        c = pattern[pos.index]
        if (c == NOT_NULL_MARKER) {
          hasNotNullMarker = true
          next(pos)
          continue
        }
        if (c != START_FMT && c != END_FE) {
          error = true
          next(pos)
          continue
        }
      }
      if (c == NOT_NULL_MARKER) {
        hasNotNullMarker = true
        next(pos)
        continue
      }
      if ((c == START_FMT || c == END_FE) && result.isNotEmpty()) {
        try {
          return ArgumentInfo(result.toString().toInt(), hasNotNullMarker)
        } catch (e: NumberFormatException) { // NOPMD
          // we've already ensured only digits, so unless something
          // outlandishly large was specified we should be okay.
        }
      }
      error = !Character.isDigit(c)
      result.append(c)
      next(pos)
    }
    require(!error) {
      ("Invalid format argument index at position " + start + ": "
              + pattern.substring(start, pos.index))
    }
    throw IllegalArgumentException("Unterminated format element at position $start")
  }

  /**
   * Parse the format component of a format element.
   *
   * @param pattern string to parse
   * @param pos current parse position
   * @return Format description String
   */
  private fun parseFormatDescription(pattern: String, pos: ParsePosition): String {
    val start: Int = pos.index
    seekNonWs(pattern, pos)
    val text: Int = pos.index
    var depth: Int = 1
    while (pos.index < pattern.length) {
      when (pattern[pos.index]) {
        START_FE -> {
          depth++
          next(pos)
        }
        END_FE -> {
          depth--
          if (depth == 0) {
            return pattern.substring(text, pos.index)
          }
          next(pos)
        }
        QUOTE -> getQuotedString(pattern, pos)
        else -> next(pos)
      }
    }
    throw IllegalArgumentException("Unterminated format element at position $start")
  }

  /**
   * Consume whitespace from the current parse position.
   * @param pattern String to read
   * @param pos current position
   */
  private fun seekNonWs(pattern: String, pos: ParsePosition) {
    var len = 0
    val buffer = pattern.toCharArray()
    do {
      len = isWsMatch(buffer, pos.index)
      pos.index = pos.index + len
    } while (len > 0 && pos.index < pattern.length)
  }

  /**
   * Convenience method to advance parse position by 1.
   * @param pos ParsePosition
   * @return `pos`
   */
  private fun next(pos: ParsePosition): ParsePosition {
    pos.index = pos.index + 1
    return pos
  }

  /**
   * Consume a quoted string, adding it to `appendTo` if
   * specified.
   * @param pattern pattern to parse
   * @param pos current parse position
   * @param appendTo optional StringBuilder to append
   * @return `appendTo`
   */
  private fun appendQuotedString(pattern: String,
                                 pos: ParsePosition,
                                 appendTo: StringBuilder?): StringBuilder? {
    assert(pattern.toCharArray()[pos.index] == QUOTE) { "Quoted string must start with quote character" }
    val lastHold: Int

    // handle quote character at the beginning of the string
    appendTo?.append(QUOTE)
    next(pos)
    val start: Int = pos.index
    val c: CharArray = pattern.toCharArray()
    lastHold = start
    for (i in pos.index until pattern.length) {
      when (c[pos.index]) {
        QUOTE -> {
          next(pos)
          return appendTo?.append(c, lastHold, pos.index - lastHold)
        }
        else -> next(pos)
      }
    }
    throw IllegalArgumentException("Unterminated quoted string at position $start")
  }

  /**
   * Consume quoted string only.
   * @param pattern pattern to parse
   * @param pos current parse position
   */
  private fun getQuotedString(pattern: String, pos: ParsePosition) {
    appendQuotedString(pattern, pos, null)
  }

  /**
   * Returns whether or not the given character matches with whitespace.
   * @param buffer  the text content to match against, do not change
   * @param pos  the starting position for the match, valid for buffer
   * @return the number of matching characters, zero for no match
   */
  private fun isWsMatch(buffer: CharArray, pos: Int): Int {
    val chars: CharArray = " \t\n\r\u000C".toCharArray()
    Arrays.sort(chars)
    return if (Arrays.binarySearch(chars, buffer[pos]) >= 0) 1 else 0
  }

  /**
   * Returns the format description of the given description.
   * @param descrption The searched format description.
   * @param descriptions The list of available format descriptions.
   * @return The format description if found or null if not.
   */
  private fun getChoiceFormatDescription(descrption: String, descriptions: List<FormatDescription>): FormatDescription? {
    for (fdescription in descriptions) {
      if (fdescription.description == null || !fdescription.description.contains("choice")) {
        continue
      }
      if (toChoicePattern(fdescription) == descrption) {
        return fdescription
      }
    }
    return null
  }

  /**
   * Converts the given choice format description to a choice pattern.
   * @param fdescription The choice format description.
   * @return The choice pattern.
   */
  private fun toChoicePattern(fdescription: FormatDescription): String {
    return (MessageFormat("{" + fdescription.info.index + "," + fdescription.description!!.replace("''".toRegex(), "'") + "}").formats[0] as ChoiceFormat).toPattern()
  }

  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------
  /*package*/
  internal class ArgumentInfo(internal val index: Int, internal val hasNotNullMarker: Boolean)

  /*package*/
  internal class FormatDescription(internal val description: String?, internal val info: ArgumentInfo)

  companion object {
    // ----------------------------------------------------------------------
    // IMPLEMENTATION
    // ----------------------------------------------------------------------
    /**
     * Creates a MessageFormat with the given pattern and uses it
     * to format the given arguments. This is equivalent to
     * <blockquote>
     * `(new [MessageFormat][.MessageFormat](pattern)).[format][.format](arguments, new StringBuffer(), null).toString()`
    </blockquote> *
     *
     * @param pattern   the pattern string
     * @param arguments object(s) to format
     * @return the formatted string
     * @exception IllegalArgumentException if the pattern is invalid,
     * or if an argument in the `arguments` array
     * is not of the type expected by the format element(s)
     * that use it.
     */
    fun formatMessage(pattern: String, arguments: Array<Any?>?): String {
      val temp = ExtendedMessageFormat(pattern)
      return temp.formatMessage(arguments)
    }
    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    /**
     * A comma.
     */
    private const val START_FMT = ','

    /**
     * Not null marker represented as a question marker.
     */
    private const val NOT_NULL_MARKER = '?'

    /**
     * A right side squigly brace.
     */
    private const val END_FE = '}'

    /**
     * A left side squigly brace.
     */
    private const val START_FE = '{'

    /**
     * A properly escaped character representing a single quote.
     */
    private const val QUOTE = '\''

    // null object representation to allow format execution since
    // default behavior can't be hacked.
    /*package*/
    val NULL_REPRESENTATION = Any()
  }
}
