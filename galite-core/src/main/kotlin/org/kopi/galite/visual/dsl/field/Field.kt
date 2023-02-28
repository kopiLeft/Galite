/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.dsl.field

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.LocalizableElement

/**
 * A field represents a visual component that can hold values
 *
 * The type of the value and the range of possible values can be specified by the [domain]
 *
 * @param domain the field's domain
 */
abstract class Field<T>(open val domain: Domain<T>, ident: String? = null, source: String? = null): LocalizableElement(ident, source) {
  /** Field's label (text on the left) */
  var label: String? = null

  /** Field's help that describes the expected value of an input field */
  var help: String? = null

  /** true if the field is hidden, false otherwise */
  open var hidden: Boolean? = false

  /**
   * Generates localization for this field
   *
   * @param writer The localization writer.
   */
  abstract fun genLocalization(writer: LocalizationWriter)
}
