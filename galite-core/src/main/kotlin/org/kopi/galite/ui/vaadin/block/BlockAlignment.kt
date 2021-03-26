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
package org.kopi.galite.ui.vaadin.block

import com.vaadin.flow.component.Component

/**
 * The block alignment info.
 */
class BlockAlignment {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  var targets: IntArray = intArrayOf()
  var isChart = false
  var ori: Component? = null

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Returns `true` is there is an alignment for the given column.
   * @param x The column index.
   * @return `true` is there is an alignment for the given column.
   */
  fun isAligned(x: Int): Boolean {
    var x = x
    x--
    return x >= 0 && x < targets.size && targets[x] != -1
  }

  /**
   * Returns the target position for for a given column.
   * @param x The column index.
   * @return The target position for for a given column.
   */
  fun getTargetAt(x: Int): Int {
    return if (x < 0 || x >= targets.size) {
      -1
    } else {
      targets[x]
    }
  }

  /**
   * Returns the target position of the block alignment.
   * @param x The aligned column index.
   * @return The target position of the block alignment.
   */
  internal fun getTargetPos(x: Int): Int {
    // block alignment
    if (x % 2 == 1 && isChart && isAligned(x / 2 + 1)) {
      return getFieldTargetPos(x / 2 + 1)
    } else if (!isChart) {
      // alignment if block is not a chart
      return if (x % 2 == 1) {
        // fields
        getFieldTargetPos(x / 2 + 1)
      } else {
        // labels
        getLabelTargetPos(x / 2 + 1)
      }
    }
    return 0
  }

  /**
   * Returns the target position of the block alignment.
   * @param x The aligned column index.
   * @return The target position of the block alignment.
   */
  protected fun getFieldTargetPos(x: Int): Int {
    var x = x
    x-- // we want to align middle
    val target = getTargetAt(x)

    // if (x >= 0 && x < targets.length && targets[x] != -1) {
    if (target != -1) {
      if (ori == null) {
        return 0
      }
      return if (isChart) target else target * 2 + 1
    }
    return 0
  }

  /**
   * Returns the label target position.
   * @param x The column position.
   * @return The label target position.
   */
  protected fun getLabelTargetPos(x: Int): Int {
    var x = x
    x-- // we want to align middle
    val target = getTargetAt(x)

    // if (x >= 0 && x < targets.length && targets[x] != -1) {
    return if (target != -1) {
      if (ori == null) {
        0
      } else 2 * target
    } else 0
  }
}
