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

package org.kopi.galite.list

import java.io.Serializable

import org.kopi.galite.l10n.ListLocalizer
import org.kopi.galite.l10n.LocalizationManager

/**
 * Represents a list
 *
 * @param     ident           the identifier of the list type
 * @param     source          the qualified name of the source file defining the list
 * @param     newForm
 * @param     columns
 * @param     table
 * @param     action
 * @param     autocompleteType
 * @param     autocompleteLength
 * @param     hasShortcut
 * */
class VList(val ident: String,
            val source: String,
            val newForm: String,
            val columns: Array<VListColumn>,
            val table: Int = 0,
            val action: Int = 0,
            val autocompleteType: Int = 0,
            val autocompleteLength: Int = 0,
            val hasShortcut: Boolean)
  : VConstants, Serializable {


  /**
   * Returns the number of columns.
   */
  fun columnCount(): Int {
    return columns.size
  }

  /**
   * Returns the column at index.
   */
  fun getColumn(pos: Int): VListColumn? {
    return columns.get(pos)
  }

  /**
   * Returns `true` if the list has auto complete support.
   */
  fun hasAutocomplete(): Boolean {
    return autocompleteLength >= 0 && autocompleteType > AUTOCOMPLETE_NONE
  }

  /**
   * Localize this object.
   *
   * @param     manager
   */
  fun localize(manager: LocalizationManager) {
    val loc: ListLocalizer = manager.getListLocalizer(source, ident)
    for (i in columns.indices) {
      columns.get(i).localize(loc)
    }
  }

  companion object {
    val AUTOCOMPLETE_NONE = 0
    val AUTOCOMPLETE_STARTSWITH = 1
    val AUTOCOMPLETE_CONTAINS = 2
  }
}
