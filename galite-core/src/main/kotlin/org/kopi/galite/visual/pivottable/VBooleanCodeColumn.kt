/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.pivottable

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.dsl.pivottable.Dimension

class VBooleanCodeColumn (ident: String?,
                          position: Dimension.Position?,
                          type: String?,
                          source: String?,
                          name: Array<String>,
                          private val codes: BooleanArray)
  : VCodeColumn(ident,
                position,
                type,
                source,
                name) {

  init {
    if (codes.size > 2) {
      throw InconsistencyException("Can't define more than two codes for a boolean column")
    }
  }

  override fun compareTo(object1: Any, object2: Any): Int {
    return if (object1 == object2) 0 else if (true == object1) 1 else -1
  }

  override fun getIndex(value: Any?): Int {
    return if ((value as Boolean) == codes[0]) 0 else 1
  }
}