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
package org.kopi.galite.form.dsl

import java.io.File

import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Window

/**
 * A block on a form
 * A block contains fields and reference to database
 *
 * @param     ident           the identifier of this block
 * @param     shortcut        the shortcut of this block
 */
abstract class FormElement(ident: String?) {

  open lateinit var shortcut: String

  val ident: String = javaClass.name.removePrefix("${javaClass.packageName}.")
          .substringBeforeLast('$')
          .substringAfterLast('$')

  /**
   * Returns the qualified source file name where this element is defined.
   */
  internal val sourceFile: String
    get() {
      val basename = this.javaClass.packageName.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }

  /**
   * Make a tuning pass in order to create informations about exported elements
   *
   * @param window        the actual context of analyse
   */
  abstract fun initialize(window: Window)

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  abstract fun genLocalization(writer: LocalizationWriter)

  /**
   * Sets the page number
   */
  var pageNumber = 0
}
