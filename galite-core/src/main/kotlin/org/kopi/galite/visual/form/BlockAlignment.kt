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

package org.kopi.galite.visual.form

import java.io.Serializable

/**
 * A class to specify alignment in Layout
 * Creates a new `BlockAlignment` instance.
 *
 * @param          block          Represents the original block block to be aligned with.
 * @param          targets      Represents the alignment targets.
 */
class BlockAlignment(val block: VBlock?,
                     val targets: IntArray)
  : Serializable {

  fun isChart(): Boolean = block != null && !block.noChart()

  fun isAligned(position: Int): Boolean {
    var updatedPosition = position
    updatedPosition--
    return updatedPosition >= 0 && updatedPosition < targets.size && targets[updatedPosition] != -1
  }

  fun getTargetAt(position: Int): Int {
    return if (position < 0 || position >= targets.size) {
      -1
    } else {
      targets[position]
    }
  }

  companion object {
    const val ALG_LEFT = false
    const val ALG_RIGHT = true
  }
}
