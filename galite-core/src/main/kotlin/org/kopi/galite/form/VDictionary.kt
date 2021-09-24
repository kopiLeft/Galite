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

package org.kopi.galite.form

import org.kopi.galite.visual.Executable
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VWindow

/**
 * The `VDictionary` is a meaning to handle three basic operations.
 * These operations are :
 *
 *  1. Search an existing record: this can be reached by calling [search]
 *  1. Edit an existing record: this can be reached by calling [edit]
 *  1. Creates a new record: this can be reached by calling [add]
 *
 * Implementations can be done in a UI context or in any other possible context.
 *
 * @see VDictionaryForm
 */
interface VDictionary : Executable {
  /**
   * Searches for an existing record.
   *
   *
   * The implementation can be done in a UI context or by a simple
   * database query. The returned integer represents the identifier
   * of the selected record after the search operation.
   *
   * @param parent The parent window. This can be used also as database context handler.
   * @return The selected ID of the searched record.
   * @throws VException Any visual errors that occurs during search operation.
   */
  fun search(parent: VWindow): Int

  /**
   * Edits an existing record.
   *
   *
   * The implementation can be done in a UI context or by a simple
   * database query. The returned integer represents the identifier
   * of the edited record after the edit operation.
   *
   * @param parent The parent window. This can be used also as database context handler.
   * @param id The record ID to be edited.
   * @return The edited record ID.
   * @throws VException Any visual errors that occurs during edit operation.
   */
  fun edit(parent: VWindow, id: Int): Int

  /**
   * Adds a new record.
   *
   *
   * The implementation can be done in a UI context or by a simple
   * database query. The returned integer represents the identifier
   * of the created record.
   *
   * @param parent The parent window. This can be used also as database context handler.
   * @return The created record ID.
   * @throws VException Any visual errors that occurs during edit operation.
   */
  fun add(parent: VWindow): Int
}
