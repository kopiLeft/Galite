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
package org.kopi.galite.visual.dsl.form

import java.awt.Point

import org.kopi.galite.visual.form.VPosition

/**
 * A position within a block given by x and y location
 *
 * @param line                the line
 * @param column                the column
 */
class FormCoordinatePosition(private val line: Int,
                             endLine: Int,
                             private var column: Int,
                             endColumn: Int,
                             private var chartPos: Int) : FormPosition(), Cloneable {
  constructor(line: Int, endLine: Int, column: Int, endColumn: Int) : this(line, endLine, column, endColumn, -1)
  constructor(chartPos: Int) : this(-1, -1, -1, -1, chartPos)
  constructor(line: Int, column: Int) : this(line, line, column, column, -1)

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var lineEnd = if (endLine == 0) line else endLine
  private var columnEnd = if (endColumn == 0) column else endColumn

  fun translate(amount: Int) {
    column += amount
    columnEnd += amount
    chartPos += amount
  }

  override fun setChartPosition(chartPos: Int) {
    this.chartPos = chartPos
  }

  override fun clone(): FormCoordinatePosition {
    return FormCoordinatePosition(line, column)
  }

  /**
   * Return the line pos
   */
  override fun getLine(): Int {
    return line
  }

  /**
   * Return the column pos
   */
  override fun getColumn(): Int {
    return column
  }

  /**
   * Return the column end pos
   */
  override fun getColumnEnd(): Int {
    return -1
  }

  /**
   * Return the line end pos
   */
  override fun getLineEnd(): Int {
    return -1
  }

  /**
   * Return the chart pos
   */
  override fun getChartPosition(): Int {
    return chartPos
  }

  /**
   * Returns the point that is on the most right and bottom from the location
   * of the object and the parameter
   *
   * @param point                the current bottomRight point
   */
  override fun createRBPoint(point: Point, field: FormField<*>) {
    point.x = point.x.coerceAtLeast(columnEnd)
    point.y = point.y.coerceAtLeast(lineEnd)
  }

  /**
   * Creates and returns a VPosition model resolved from this form field position
   */
  override fun getPositionModel(): VPosition = VPosition(line, lineEnd, column, columnEnd, chartPos)
}
