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
 * @param end                        the last column onto this field may be placed
 */
class FormMultiFieldPosition(private var line: Int,
                             endLine: Int,
                             private var column: Int = 1) : FormPosition(), Cloneable {

  private var endLine = if (endLine == 0) line else endLine

  fun translate(amount: Int) {
    column += amount
  }

  public override fun clone(): Any {
    return FormMultiFieldPosition(line, column)
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
    return -1
  }

  /**
   * Return the column end pos
   */
  override fun getColumnEnd(): Int {
    return column
  }

  /**
   * Return the line end pos
   */
  override fun getLineEnd(): Int {
    return endLine
  }

  /**
   * Returns the point that is on the most right and bottom from the location
   * of the object and the parameter
   *
   * @param point                the current bottomRight point
   */
  override fun createRBPoint(point: Point, field: FormField<*>) {
    point.x = point.x.coerceAtLeast(column)
    point.y = point.y.coerceAtLeast(line + (field.domain.height ?: 0))
  }

  /**
   * Creates and returns a VPosition model resolved from this form field position
   */
  override fun getPositionModel(): VPosition = VPosition(line, endLine, -1, column)
}
