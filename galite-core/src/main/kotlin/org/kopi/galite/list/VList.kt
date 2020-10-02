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

import org.kopi.galite.l10n.LocalizationManager

class VList {
  fun hasAutocomplete(): Boolean {
    TODO()
  }

  fun getAutocompleteLength(): Int {
    TODO()
  }

  fun getAutocompleteType(): Int {
    TODO()
  }

  fun localize(manager: LocalizationManager) {
    TODO()
  }

  fun getColumn(pos: Int): VListColumn {
    TODO()
  }

  fun columnCount(): Int {
    TODO()
  }

  fun getColumns(): Array<VListColumn> {
    TODO()
  }

  fun getNewForm(): String? {
    TODO()
  }

  fun getAction(): Int {
    TODO()
  }

  fun getTable(): Int {
    TODO()
  }

  companion object {
    val AUTOCOMPLETE_NONE = 0
    const val AUTOCOMPLETE_CONTAINS = 2
    const val AUTOCOMPLETE_STARTSWITH = 1
  }
}
