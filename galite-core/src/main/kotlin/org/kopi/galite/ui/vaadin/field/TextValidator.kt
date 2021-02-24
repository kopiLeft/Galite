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

import java.util.Optional

import com.vaadin.flow.component.HasValue
import com.vaadin.flow.data.binder.ValidationResult
import com.vaadin.flow.data.binder.Validator
import com.vaadin.flow.data.binder.ValueContext

interface TextValidator<T>: Validator<T> {

}

/**
 * A string validator.
 * @param width The field width.
 * @param height The field height.
 * @param fixedNewLine Use fixed lines or dynamic lines ?
 * @param convertType The convert type to be used.
 */
class StringValidator<T>(
        val width: Int,
        val height: Int,
        val fixedNewLine: Boolean,
        val convertType: TextField.ConvertType?
) : TextValidator<T> {

  override fun apply(value: T, context: ValueContext): ValidationResult {
    return isValid(value, context)
  }

  /**
   * Returns whether the given string matches the regular expression.
   *
   * @param value
   * the string to match
   * @return true if the string matched, false otherwise
   */
  fun isValid(value: T, context: ValueContext): ValidationResult =
          checkType(context.hasValue as Optional<HasValue<*, T>>, value?.toString()?.trim() ?: "")

  fun checkType(field: Optional<HasValue<*, T>>, text: String?) : ValidationResult {
    field.ifPresent {
      var text = text
      if (text == null || "" == text) {
        it.setValue(null)
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
          // return ValidationResult.error() TODO
          throw CheckTypeException(it, "00013")
        }
        it.setValue(text as T)
      }
    }
    return ValidationResult.ok()
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
      if (!found && chars[i].isLetter()) {
        chars[i] = chars[i].toUpperCase()
        found = true
      } else if (chars[i].isWhitespace()) {
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
  private fun checkText(text: String?): Boolean =
          textToModel(text, width, Int.MAX_VALUE, fixedNewLine).length <= width * height

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
}

/**
 * An integer validator.
 * @param minval The minimum accepted value.
 * @param maxval The maximum accepted value.
 */
class IntegerValidator<T>(minval: Double?, maxval: Double?): TextValidator<T> {

  override fun apply(value: T, context: ValueContext?): ValidationResult {
    //TODO("Not yet implemented")
    return ValidationResult.ok()
  }
}

/**
 * An date validator.
 */
class DateValidator<T>: TextValidator<T> {

  override fun apply(value: T, context: ValueContext?): ValidationResult {
    //TODO("Not yet implemented")
    return ValidationResult.ok()
  }
}

/**
 * A week validator.
 */
class WeekValidator<T>(): TextValidator<T> {

  override fun apply(value: T, context: ValueContext?): ValidationResult {
    //TODO("Not yet implemented")
    return ValidationResult.ok()
  }
}

/**
 * A Month validator.
 */
class MonthValidator<T>(): TextValidator<T> {

  override fun apply(value: T, context: ValueContext?): ValidationResult {
    //TODO("Not yet implemented")
    return ValidationResult.ok()
  }
}

/**
 * A time validator.
 */
class TimeValidator<T>(): TextValidator<T> {

  override fun apply(value: T, context: ValueContext?): ValidationResult {
    //TODO("Not yet implemented")
    return ValidationResult.ok()
  }
}

/**
 * A Timestamp validator.
 */
class TimestampValidator<T>(): TextValidator<T> {

  override fun apply(value: T, context: ValueContext?): ValidationResult {
    //TODO("Not yet implemented")
    return ValidationResult.ok()
  }
}
