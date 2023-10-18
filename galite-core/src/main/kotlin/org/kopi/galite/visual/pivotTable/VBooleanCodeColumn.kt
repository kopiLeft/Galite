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

package org.kopi.galite.visual.pivotTable

import org.kopi.galite.util.base.InconsistencyException

/**
 * Represents a report column description
 *
 * @param     ident           The column identifier
 * @param     options         The column options as bitmap
 * @param     align           The column alignment
 * @param     groups          The index of the column grouped by this one or -1
 * @param     function        An (optional) summation function
 */
class VBooleanCodeColumn(ident: String?,
                         type: String?,
                         source: String?,
                         options: Int,
                         align: Int,
                         groups: Int,
                         function: VCalculateColumn?,
                         width: Int,
                         format: VCellFormat?,
                         names: Array<String>,
                         private val codes: BooleanArray)
  : VCodeColumn(ident,
                type,
                source,
                options,
                align,
                groups,
                function,
                width,
                format,
                names) {

  init {
    if (codes.size > 2) {
      throw InconsistencyException("Can't define more than two codes for a boolean column")
    }
  }

  override fun compareTo(object1: Any, object2: Any): Int {
    return if (object1 == object2) 0 else if (true == object1) 1 else -1
  }

  /**
   * Get the index of the value.
   */
  override fun getIndex(value: Any): Int {
    return if ((value as Boolean) == codes[0]) 0 else 1
  }
}
