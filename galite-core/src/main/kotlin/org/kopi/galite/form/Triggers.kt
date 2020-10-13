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

package org.kopi.galite.form

/**
 * This class implements predefined triggers
 */
object Triggers : VConstants {

  /*
   * ----------------------------------------------------------------------
   * FORM-LEVEL TRIGGERS
   * ----------------------------------------------------------------------
   */

  /**
   * Returns true if form is changed.
   */
  fun isChanged(f: VForm): Boolean = f.isChanged()

  /*
   * ----------------------------------------------------------------------
   * BLOCK-LEVEL TRIGGERS
   * ----------------------------------------------------------------------
   */

  /**
   * Returns always false (= unchanged).
   */
  fun ignoreChanges(b: VBlock): Boolean = false

  /**
   * Returns true iff first block of form is not in query mode
   */
  fun mainBlockInsertable(b: VBlock): Boolean = b.getForm().getBlock(0).getMode() != VConstants.MOD_QUERY

  /*
   * ----------------------------------------------------------------------
   * FIELD-LEVEL TRIGGERS
   * ----------------------------------------------------------------------
   */

  /**
   * Fetches fields of lookup table with key current field.
   * This trigger is normally called as POSTCHG
   * @exception   VException    an exception may occur if
   * next record was deleted by tierce
   */
  fun fetchLookup(f: VField) {
    f.block.fetchLookup(f)
  }
}
