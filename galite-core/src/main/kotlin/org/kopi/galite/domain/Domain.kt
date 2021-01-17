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

import kotlin.reflect.KClass

import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.type.Decimal
import org.kopi.galite.form.VConstants

/**
 * A domain is a data type with predefined list of allowed values.
 *
 * @param width             the width in char of this field
 * @param height            the height in char of this field
 * @param visibleHeight     the visible height in char of this field.
 */
open class Domain<T : Comparable<T>?>(val width: Int? = null,
                                      val height: Int? = null,
                                      val visibleHeight: Int? = null) {
  companion object {
    operator fun <T: Decimal> invoke(width: Int, scale: Int): Domain<Decimal> =
            Domain(width, scale, null)
  }

  private var isFraction = false

  val ident: String = this::class.java.simpleName

  /**
   * Determines the field data type
   */
  var kClass: KClass<*>? = null

  /**
   * Allows to define the possible codes that the domain can take
   *
   * @param init used to initialize the code domain
   */
  fun code(init: CodeDomain<T>.() -> Unit): CodeDomain<T> {
    val codeDomain = CodeDomain<T>()
    codeDomain.init()
    return codeDomain
  }

  /**
   * returns true if this domain is a code domain, false otherwise
   */
  private fun isCodeDomain(): Boolean = this is CodeDomain<T>

  /**
   * returns true if this domain is a list domain, false otherwise
   */
  private fun isListDomain(): Boolean = this is ListDomain<T>

  /**
   * Returns the default alignment
   */
  val defaultAlignment: Int
    get() = if (kClass == Fixed::class) {
      VConstants.ALG_RIGHT
    } else {
      VConstants.ALG_LEFT
    }

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------
  fun hasSize(): Boolean =
          when (kClass) {
            Decimal::class, Int::class, Long::class, String::class -> true
            else -> false
          }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  fun genLocalization(writer: LocalizationWriter) {
    writer.genTypeDefinition(ident, this)
  }

  open fun genTypeLocalization(writer: LocalizationWriter) {
    // DO NOTHING !
  }
}
