/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.domain

import org.kopi.galite.common.CodeDescription
import org.kopi.galite.common.LocalizationWriter

/**
 * Represents a code domain.
 */
open class CodeDomain<T : Comparable<T>?> : Domain<T>() {

  /**
   * Contains all values that a domain can take
   */
  val codes: MutableList<CodeDescription<T>> = mutableListOf()

  /**
   * Sets a mapping between the values that the domain can take
   * and a corresponding text to be displayed in a field.
   *
   * @param text the text
   * @param value the value
   */
  infix fun String.keyOf(value: T) {
    val codeDescription = CodeDescription("id$${codes.size}", this, value)
    codes.add(codeDescription)
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * Generates localization.
   */
  override fun genTypeLocalization(writer: LocalizationWriter) {
    writer.genCodeType(codes)
  }
}
