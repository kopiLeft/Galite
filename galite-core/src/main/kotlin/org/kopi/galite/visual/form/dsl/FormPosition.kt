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
package org.kopi.galite.visual.form.dsl

import java.awt.Point

import org.kopi.galite.visual.form.VPosition
import org.kopi.galite.visual.util.base.InconsistencyException

/**
 * A position within a block. This is a position given by x and y location
 *
 */
abstract class FormPosition protected constructor() {
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  open fun setChartPosition(chartPos: Int) {
    // this method should not be called
    throw InconsistencyException("setChartPosition(chartPos) should not be called from here !!!")
  }

  /**
   * Returns the point that is on the most right and bottom from the location
   * of the object and the parameter
   *
   * @param point                the current bottomRight point
   */
  abstract fun createRBPoint(point: Point, field: FormField<*>)

  // ----------------------------------------------------------------------
  // Position method
  // ----------------------------------------------------------------------
  abstract fun getLine(): Int
  abstract fun getColumn(): Int
  abstract fun getColumnEnd(): Int
  abstract fun getLineEnd(): Int
  open fun getChartPosition(): Int = -1

  /**
   * Creates and returns a VPosition model resolved from this form field position
   */
  abstract fun getPositionModel(): VPosition
}
