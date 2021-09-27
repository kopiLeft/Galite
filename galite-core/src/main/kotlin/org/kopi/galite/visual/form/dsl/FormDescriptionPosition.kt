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

/**
 * A position within a block
 *
 * @param field                the master field
 */
class FormDescriptionPosition(val field: FormField<*>,
                              private var chartPos: Int = -1) : FormPosition() {

  /**
   * Sets chart position to [chartPos]
   */
  override fun setChartPosition(chartPos: Int) {
    this.chartPos = chartPos
  }

  /**
   * Returns the point that is on the most right and bottom from the location
   * of the object and the parameter
   *
   * @param point                the current bottomRight point
   */
  override fun createRBPoint(point: Point, field: FormField<*>) {
    // do nothing
  }

  /**
   * Return the line pos
   */
  override fun getLine(): Int {
    return field.position!!.getLine()
  }

  /**
   * Return the column pos
   */
  override fun getColumn(): Int {
    return field.position!!.getColumn()
  }

  /**
   * Return the column end pos
   */
  override fun getColumnEnd(): Int {
    return field.position!!.getColumnEnd()
  }

  /**
   * Return the line end pos
   */
  override fun getLineEnd(): Int {
    return field.position!!.getLineEnd()
  }

  /**
   * Return the chart pos
   */
  override fun getChartPosition(): Int {
    return chartPos
  }

  /**
   * Creates and returns a VPosition model resolved from this form field position
   */
  override fun getPositionModel(): VPosition =
          VPosition(getLine(), getLineEnd(), getColumn(), getColumnEnd(), getChartPosition())
}
