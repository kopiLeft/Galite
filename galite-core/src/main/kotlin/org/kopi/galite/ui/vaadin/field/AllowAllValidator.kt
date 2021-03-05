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

import com.vaadin.flow.data.binder.ValidationResult
import com.vaadin.flow.data.binder.ValueContext

/**
 * No restriction applied in this validator.
 *
 * @param maxLength The maximum permitted length.
 */
open class AllowAllValidator(val maxLength: Int) : TextValidator {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun apply(value: Any?, context: ValueContext): ValidationResult =
          if (validate(value?.toString())) ValidationResult.ok() else ValidationResult.error("TODO") // TODO

  override fun validate(c: Char): Boolean = true

  override fun validate(text: String?): Boolean {
    return if (text == null) {
      true // null text is considered as valid one
    } else {
      for (element in text) {
        if (!validate(element)) {
          return false
        }
      }
      true
    }
  }

  override fun checkType(field: TextField, text: String) {
    // nothing to do, all is accepted
  }
}
