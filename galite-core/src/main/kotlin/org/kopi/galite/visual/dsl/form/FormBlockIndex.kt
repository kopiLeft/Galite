/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.dsl.form

import org.kopi.galite.visual.dsl.common.LocalizationWriter

/**
 * A binding to database. This is a position given by x and y location
 *
 * @param ident                  the identifier of the index
 * @param message                the error message in the default locale
 * @param number                 the number of this index
 */
class FormBlockIndex(val ident: String, val message: String, private val number: Int) {

  /**
   * Represents a combination of a list of [FormBlockIndex].
   */
  var indices: MutableList<FormBlockIndex> = mutableListOf(this)

  val indexNumber: Int
    get() {
      var index = 0

      indices.forEach {
        index = index or (1 shl it.number)
      }

      return index
    }

  operator fun plus(index: FormBlockIndex): FormBlockIndex =
    FormBlockIndex("", "", -1).also {
      it.indices = (indices + index.indices).toMutableList()
    }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter).genBlockIndex(ident, message)
  }
}
