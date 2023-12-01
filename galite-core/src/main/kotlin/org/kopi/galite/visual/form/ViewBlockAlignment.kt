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
 */
class ViewBlockAlignment(var formView: UForm,
                         var align: BlockAlignment) : Serializable {

  //------------------------------------------------------------
  // ACCESSORS
  //------------------------------------------------------------

  fun isChart(): Boolean = align.isChart()

  fun isAligned(x: Int): Boolean = align.isAligned(x)

  fun getMinStart(x: Int): Int {
    var modifiedX = x
    val target: Int

    modifiedX-- // we want to align middle
    target = align.getTargetAt(modifiedX)
    val view = formView.getBlockView(align.block!!)

    if (target != -1) {
      if (view == null) {
        return 0
      }
      val pos = if (isChart()) target else target * 2 + 1

      return view.getColumnPos(pos)
    }
    return 0
  }

  fun getLabelMinStart(x: Int): Int {
    var modifiedX = x
    modifiedX-- // we want to align middle
    val target = align.getTargetAt(modifiedX)
    val view = formView.getBlockView(align.block!!)

    return if (target != -1) {
      view?.getColumnPos(target * 2) ?: 0
    } else 0
  }
}
