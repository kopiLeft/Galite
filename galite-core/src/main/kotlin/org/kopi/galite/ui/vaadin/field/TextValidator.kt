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

import com.vaadin.flow.data.binder.Validator

/**
 * A text input validator.
 */
interface TextValidator: Validator<Any?> {

  /**
   * Returns `true` if the character is accepted.
   *
   * @param c The character to validate.
   * @return `true` if the character is accepted.
   */
  fun validate(c: Char): Boolean

  /**
   * Returns `true` if the text is accepted.
   *
   * @param text The text to be validated.
   * @return `true` if the text is accepted.
   */
  fun validate(text: String?): Boolean

  /**
   * Performs a check of the given text according to the validation type.
   *
   * @param text The text to be checked.
   * @throws CheckTypeException when the given value is not valid.
   */
  fun checkType(field: InputTextField<*>, text: String)
}
