/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.report

import org.kopi.galite.visual.field.Field

/**
 * A data line of a [Report].
 *
 * @param reportFields the fields that exists in the report.
 */
class Line(private val reportFields: MutableList<Field<*>>) {
  /** A report data line */
  val reportLine = mutableMapOf<Field<*>, Any>()

  /** List of sub lines or children lines */
  lateinit var subLines : MutableList<Line>

  /**
   * Add a sub line to the line list for grouping
   *
   * @param line the line to add
   */
  fun <T:Line> addLine(line : Line){
    subLines.add(line)
  }

  /**
   * Sets a mapping between the values that the domain can take
   * and a corresponding text to be displayed in a [Field].
   *
   * @param field the field.
   * @param value the field's value.
   */
  operator fun <T: Comparable<T>> set(field: Field<T>, value: T) {
    if(field in reportFields) {
      reportLine.putIfAbsent(field, value)
    }
  }
}
