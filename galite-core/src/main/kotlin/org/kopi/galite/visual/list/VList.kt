/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.list

import java.io.Serializable

import org.jetbrains.exposed.sql.ColumnSet
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.l10n.ListLocalizer
import org.kopi.galite.visual.l10n.LocalizationManager

/**
 * Represents a list
 *
 * @param     ident               the identifier of the list type
 * @param     source              the qualified name of the source file defining the list
 * @param     newForm             the new form name TODO: remove this as it is the old syntax
 * @param     columns             the list columns
 * @param     table               the statement to select data
 * @param     action              the list action
 * @param     autocompleteType    the auto complete type.
 * @param     autocompleteLength  the auto complete length.
 * @param     hasShortcut         the new form name
 * */
class VList(private val ident: String,
            private val source: String,
            val newForm: String?,
            val columns: Array<VListColumn?>,
            val table: () -> ColumnSet,
            val action: (() -> DictionaryForm)?,
            val autocompleteType: Int,
            val autocompleteLength: Int,
            val hasShortcut: Boolean) : VConstants, Serializable {

  constructor(ident: String,
              source: String,
              columns: Array<VListColumn?>,
              table: () -> ColumnSet,
              action: (() -> DictionaryForm)?,
              autocompleteType: Int,
              autocompleteLength: Int,
              newForm: Class<VForm>?,
              hasShortcut: Boolean)
       : this(ident,
              source,
              newForm?.name,
              columns,
              table,
              action,
              autocompleteType,
              autocompleteLength,
              hasShortcut)

  /**
   * Returns the number of columns.
   */
  fun columnCount(): Int = columns.size

  /**
   * Returns the column at index.
   */
  fun getColumn(pos: Int): VListColumn = columns[pos]!!

  /**
   * Returns `true` if the list has auto complete support.
   */
  fun hasAutocomplete(): Boolean = autocompleteLength >= 0 && autocompleteType > AUTOCOMPLETE_NONE

  /**
   * Localize this object.
   *
   * @param     manager
   */
  fun localize(manager: LocalizationManager) {
    val loc: ListLocalizer = manager.getListLocalizer(source, ident)
    columns.forEach { column ->
      column!!.localize(loc)
    }
  }

  companion object {
    const val AUTOCOMPLETE_NONE = 0
    const val AUTOCOMPLETE_STARTSWITH = 1
    const val AUTOCOMPLETE_CONTAINS = 2
  }
}
