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
package org.kopi.galite.common

import org.jetbrains.exposed.sql.Table

/**
 * This class represent a list of data from the database
 *
 * @param type                the identifier of the type containing this list
 * @param table               the statement to select data
 * @param action              the field list action
 * @param columns             a description of the columns
 * @param autocompleteType    TODO: add doc
 * @param autocompleteLength  TODO: add doc
 * @param access              true if this field is only an access to a form
 */
class FieldList<T: Comparable<T>?>(val type: String,
                                   val table: Table,
                                   val action: (() -> Unit)?,
                                   val columns: Array<ListDescription<T>>,
                                   val autocompleteType: Int,
                                   val autocompleteLength: Int,
                                   val access: Boolean) {

  /**
   * Returns `true` if the list has a list action.
   */
  fun hasAction(): Boolean {
    return action != null
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * Generate localization for this type.
   *
   */
  fun genLocalization(writer: LocalizationWriter) {
    writer.genFieldList(columns)
  }
}
