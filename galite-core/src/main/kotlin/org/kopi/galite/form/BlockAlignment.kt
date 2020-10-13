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

import java.io.Serializable

/**
 * A class to specify alignment in Layout
 * Creates a new `BlockAlignment` instance.
 *
 * @param          ori          Represents the original block to be aligned with.
 * @param          targets      Represents the alignment targets.
 */
class BlockAlignment(var ori : VBlock?,
                     var targets: IntArray)
  : Serializable {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  fun isChart(): Boolean = ori != null && ori!!.isChart()

  fun isAligned(x: Int): Boolean {
    var x = x
    x--
    return x >= 0 && x < targets.size && targets[x] != -1
  }
  fun getBlock():VBlock {
    TODO()
  }
  fun getTargetAt(x: Int): Int {
    return if (x < 0 || x >= targets.size) {
      -1
    } else {
      targets[x]
    }
  }

  companion object {
    const val ALG_LEFT = false
    const val ALG_RIGHT = true
  }
}